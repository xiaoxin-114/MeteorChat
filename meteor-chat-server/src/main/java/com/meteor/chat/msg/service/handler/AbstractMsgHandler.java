package com.meteor.chat.msg.service.handler;

import cn.hutool.core.bean.BeanUtil;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.common.domain.vo.req.ChatMessageReq;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.service.adapter.MsgAdapter;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public abstract class AbstractMsgHandler<T> {
    public Class<T> bodyClass;

    @Resource
    private MessageDao messageDao;

    @PostConstruct
    private void init(){
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<T>) superclass.getActualTypeArguments()[0];
        MsgHandlerFactory.regsiter(getMsgType().getType(), this);
    }

    /**
     * 消息发送请求，对消息进行校验，保存等处理
     * @param req
     * @param uid
     * @return
     */
    public final Long handlerMsg(ChatMessageReq req, Long uid) {
        // 获取信息的内容
        T msgBody = getBody(req);
        // 对信息进行统一校验
        commonCheck(msgBody);
        // 进行子类对信息的补充校验
        checkMsg(msgBody, req.getRoomId(), uid);
        Message message = MsgAdapter.buildMessage(req, uid);
        // 统一保存信息，此时保存的信息只有房间号，发送人id，消息类型
        messageDao.save(message);
        // 子类保存信息额外数据
        saveMessageExtra(message, msgBody);
        return message.getId();
    }

    protected void checkMsg(T body, Long roomId, Long uid) {

    }

    /**
     * 对信息请求进行valid的统一校验，因为body的类型为Object，
     * 所以在方法中，获取实际的消息DTO类型后，手动进行校验
     * @param t 请求中的消息内容
     */
    private void commonCheck(T t) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> validate = validator.validate(t);
        if (CollectionUtils.isEmpty(validate)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<T> constrain:
                validate) {
            sb.append(constrain.getPropertyPath().toString() + ":" + constrain.getMessage()).append(";");
        }
        throw new AssertionError(sb.substring(0, sb.length() - 1).toString());
    }

    private final T getBody(ChatMessageReq req){
        Object body = req.getBody();
        // 为兼容String类型，因为如果使用使用的是String类型，Object转换成String在BeanUtil.toBean会报错
        // String转只能作为对象的属性，但如果body此时已经是String了，自然就会报错
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (T) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }

    abstract void saveMessageExtra(Message message, T body);

    abstract MessageTypeEnum getMsgType();

    /**
     * 返回消息的显示内容
     * @return
     */
    public abstract String messageText(Message message);

    public abstract Object buildMessageBody(Message message);

    public abstract String replyMsgText(Message message);
}

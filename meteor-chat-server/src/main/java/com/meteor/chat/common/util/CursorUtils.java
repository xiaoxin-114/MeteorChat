package com.meteor.chat.common.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.msg.service.adapter.RoomAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 游标工具类，将游标翻页的共同操作抽象到该工具类
 */
public class CursorUtils {

    public static <T> CursorPageBaseResp<T> cursorPage(CursorPageBaseReq request, IService<T> dao, Consumer<LambdaQueryWrapper<T>> consumer, SFunction<T, ?> cursorCollum) {
        int pageSize = request.getPageSize();
        String cursor = request.getCursor();
        // todo 根据cursorColumn获取游标类型
        Class cursorClass = null;
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(cursor)) {
            // 根据游标，定位条件
            queryWrapper.lt(cursorCollum, parseCursor(cursor, cursorClass));
        }
        // 一些独特的条件逻辑，比如查询某人的会话列表，需要用户id条件
        consumer.accept(queryWrapper);
        queryWrapper.orderByAsc(cursorCollum);
        // 根据游标翻页，构建普通翻页的page对象
        Page page = request.plusPage();
        // 查询数据
        Page<T> result = dao.page(page, queryWrapper);
        List<T> records = result.getRecords();
        String newCursor = Optional.ofNullable(records)
                // 因为多查询了一条记录，但是我们要获取返回给前端的最后一条记录的游标
                // 需要判断是否取到pageSize+1条记录，取到了就取倒数第二条，没取到就要取最后一条
                .map(record -> record.get(records.size() == pageSize + 1 ? pageSize - 1 : record.size() - 1))
                .map(cursorCollum)
                .map(CursorUtils::toCursor)
                .orElse(null);
        // 因为比实际前端要求多查询了一条，所以如果查询到pageSize+1条，说明还有下一页。如果没查到说明就是最后一页
        boolean isLast = records.size() != pageSize + 1;
        return new CursorPageBaseResp<>(newCursor, isLast, records.subList(0, pageSize));
    }

    /**
     * 将游标对象转换成字符串类型
     * @param o 目前游标类型兼容两种：字符串和date类型，游标对象
     * @return
     */
    public static String toCursor(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return String.valueOf(((Date) o).getTime());
        } else {
            return o.toString();
        }
    }

    /**
     * 将字符串形式的游标，转换成对应的对象类型
     * @param cursor 游标字符串
     * @param cursorClass 游标类型
     * @return
     */
    public static Object parseCursor(String cursor, Class<?> cursorClass) {
        if (Date.class.isAssignableFrom(cursorClass)) {
            return new Date(Long.parseLong(cursor));
        }else {
            return cursor;
        }
    }
}

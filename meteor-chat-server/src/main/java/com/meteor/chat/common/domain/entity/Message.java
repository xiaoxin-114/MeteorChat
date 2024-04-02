package com.meteor.chat.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息表
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话表id
     */
    private Long roomId;

    /**
     * 消息发送者uid
     */
    private Long fromUid;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 回复的消息内容
     */
    private Long replyMsgId;

    /**
     * 消息状态 0正常 1删除
     */
    private Integer status;

    /**
     * 与回复的消息间隔多少条
     */
    private Integer gapCount;

    /**
     * 消息类型 1正常文本 2.撤回消息
     */
    private Integer type;

    /**
     * 扩展信息
     */
    private Object extra;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Message other = (Message) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRoomId() == null ? other.getRoomId() == null : this.getRoomId().equals(other.getRoomId()))
            && (this.getFromUid() == null ? other.getFromUid() == null : this.getFromUid().equals(other.getFromUid()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getReplyMsgId() == null ? other.getReplyMsgId() == null : this.getReplyMsgId().equals(other.getReplyMsgId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getGapCount() == null ? other.getGapCount() == null : this.getGapCount().equals(other.getGapCount()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getExtra() == null ? other.getExtra() == null : this.getExtra().equals(other.getExtra()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoomId() == null) ? 0 : getRoomId().hashCode());
        result = prime * result + ((getFromUid() == null) ? 0 : getFromUid().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getReplyMsgId() == null) ? 0 : getReplyMsgId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getGapCount() == null) ? 0 : getGapCount().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roomId=").append(roomId);
        sb.append(", fromUid=").append(fromUid);
        sb.append(", content=").append(content);
        sb.append(", replyMsgId=").append(replyMsgId);
        sb.append(", status=").append(status);
        sb.append(", gapCount=").append(gapCount);
        sb.append(", type=").append(type);
        sb.append(", extra=").append(extra);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
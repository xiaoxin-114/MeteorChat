package com.meteor.chat.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 房间表
 * @TableName room
 */
@TableName(value ="room")
@Data
public class Room implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 房间类型 1群聊 2单聊
     */
    private Integer type;

    /**
     * 是否全员展示 0否 1是
     */
    private Integer hotFlag;

    /**
     * 群最后消息的更新时间（热点群不需要写扩散，只更新这里）
     */
    private Date activeTime;

    /**
     * 会话中的最后一条消息id
     */
    private Long lastMsgId;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    private Object extJson;

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
        Room other = (Room) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getHotFlag() == null ? other.getHotFlag() == null : this.getHotFlag().equals(other.getHotFlag()))
            && (this.getActiveTime() == null ? other.getActiveTime() == null : this.getActiveTime().equals(other.getActiveTime()))
            && (this.getLastMsgId() == null ? other.getLastMsgId() == null : this.getLastMsgId().equals(other.getLastMsgId()))
            && (this.getExtJson() == null ? other.getExtJson() == null : this.getExtJson().equals(other.getExtJson()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getHotFlag() == null) ? 0 : getHotFlag().hashCode());
        result = prime * result + ((getActiveTime() == null) ? 0 : getActiveTime().hashCode());
        result = prime * result + ((getLastMsgId() == null) ? 0 : getLastMsgId().hashCode());
        result = prime * result + ((getExtJson() == null) ? 0 : getExtJson().hashCode());
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
        sb.append(", type=").append(type);
        sb.append(", hotFlag=").append(hotFlag);
        sb.append(", activeTime=").append(activeTime);
        sb.append(", lastMsgId=").append(lastMsgId);
        sb.append(", extJson=").append(extJson);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
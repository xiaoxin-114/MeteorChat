package com.meteor.chat.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 本地消息表
 * @TableName secure_invoke_record
 */
@TableName(value ="secure_invoke_record")
@Data
public class SecureInvokeRecord implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 请求快照参数json
     */
    private Object secureInvokeJson;

    /**
     * 状态 1待执行 2已失败
     */
    private Integer status;

    /**
     * 下一次重试的时间
     */
    private Date nextRetryTime;

    /**
     * 已经重试的次数
     */
    private Integer retryTimes;

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;

    /**
     * 执行失败的堆栈
     */
    private String failReason;

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
        SecureInvokeRecord other = (SecureInvokeRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSecureInvokeJson() == null ? other.getSecureInvokeJson() == null : this.getSecureInvokeJson().equals(other.getSecureInvokeJson()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getNextRetryTime() == null ? other.getNextRetryTime() == null : this.getNextRetryTime().equals(other.getNextRetryTime()))
            && (this.getRetryTimes() == null ? other.getRetryTimes() == null : this.getRetryTimes().equals(other.getRetryTimes()))
            && (this.getMaxRetryTimes() == null ? other.getMaxRetryTimes() == null : this.getMaxRetryTimes().equals(other.getMaxRetryTimes()))
            && (this.getFailReason() == null ? other.getFailReason() == null : this.getFailReason().equals(other.getFailReason()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSecureInvokeJson() == null) ? 0 : getSecureInvokeJson().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getNextRetryTime() == null) ? 0 : getNextRetryTime().hashCode());
        result = prime * result + ((getRetryTimes() == null) ? 0 : getRetryTimes().hashCode());
        result = prime * result + ((getMaxRetryTimes() == null) ? 0 : getMaxRetryTimes().hashCode());
        result = prime * result + ((getFailReason() == null) ? 0 : getFailReason().hashCode());
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
        sb.append(", secureInvokeJson=").append(secureInvokeJson);
        sb.append(", status=").append(status);
        sb.append(", nextRetryTime=").append(nextRetryTime);
        sb.append(", retryTimes=").append(retryTimes);
        sb.append(", maxRetryTimes=").append(maxRetryTimes);
        sb.append(", failReason=").append(failReason);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
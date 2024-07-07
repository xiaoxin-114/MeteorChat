package com.meteor.chat.transaction.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.transaction.domian.SecureInvokeRecord;
import com.meteor.chat.transaction.mapper.SecureInvokeRecordMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SecureInvokeDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> {

    public List<SecureInvokeRecord> getRecords() {
        return lambdaQuery().eq(SecureInvokeRecord::getStatus, SecureInvokeRecord.STATUS_WAIT)
                .le(SecureInvokeRecord::getNextRetryTime, new Date()).list();
    }
}

package com.meteor.chat.msg.api;

import com.meteor.chat.msg.dao.SecureInvokeDao;
import com.meteor.chat.transaction.core.api.SecureInvokeRecordApi;
import com.meteor.chat.transaction.core.domian.SecureInvokeRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
@Service
public class SecureInvokeRecordApiImpl implements SecureInvokeRecordApi {

    @Resource
    private SecureInvokeDao secureInvokeDao;

    @Override
    public List<SecureInvokeRecord> getRecords() {
        return secureInvokeDao.getRecords();
    }

    @Override
    public boolean save(SecureInvokeRecord record) {
        return secureInvokeDao.save(record);
    }

    @Override
    public boolean updateById(SecureInvokeRecord record) {
        return secureInvokeDao.updateById(record);
    }

    @Override
    public boolean removeById(Long id) {
        return secureInvokeDao.removeById(id);
    }
}

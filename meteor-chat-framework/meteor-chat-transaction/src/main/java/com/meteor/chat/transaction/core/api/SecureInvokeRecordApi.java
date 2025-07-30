package com.meteor.chat.transaction.core.api;

import com.meteor.chat.transaction.core.domian.SecureInvokeRecord;

import java.util.List;

public interface SecureInvokeRecordApi {

    List<SecureInvokeRecord> getRecords();

    boolean save(SecureInvokeRecord record);

    boolean updateById(SecureInvokeRecord record);

    boolean removeById(Long id);
}

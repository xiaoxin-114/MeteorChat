package com.meteor.chat.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meteor.chat.transaction.domian.SecureInvokeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
* @author meteor
* @description 针对表【secure_invoke_record(本地消息表)】的数据库操作Mapper
* @createDate 2024-03-29 17:21:14
* @Entity generator.domain.SecureInvokeRecord
*/
@Mapper
public interface SecureInvokeRecordMapper extends BaseMapper<SecureInvokeRecord> {

}





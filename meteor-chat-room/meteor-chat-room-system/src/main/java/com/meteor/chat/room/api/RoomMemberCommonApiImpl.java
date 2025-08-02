package com.meteor.chat.room.api;

import com.meteor.chat.api.room.RoomMemberCommonApi;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.room.domain.entity.RoomGroup;
import com.meteor.chat.room.service.cache.GroupMemberCache;
import com.meteor.chat.room.service.cache.RoomGroupCache;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@RestController
public class RoomMemberCommonApiImpl implements RoomMemberCommonApi {
    @Resource
    private RoomGroupCache roomGroupCache;
    @Resource
    private GroupMemberCache groupMemberCache;
    @Override
    public ApiResult<List<Long>> getMemberList(Long roomId) {
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        if (Objects.isNull(roomGroup)) {
            return ApiResult.success(Collections.emptyList());
        }
        return ApiResult.success(groupMemberCache.getMemberUidList(roomGroup.getId()));
    }
}

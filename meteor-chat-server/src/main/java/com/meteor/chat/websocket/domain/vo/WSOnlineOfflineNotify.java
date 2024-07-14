package com.meteor.chat.websocket.domain.vo;

import com.meteor.chat.common.domain.vo.GroupMemberResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:用户上下线变动的推送类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {
    //新的上下线用户
    private List<GroupMemberResp> changeList = new ArrayList<>();
    //在线人数
    private Long onlineNum;
}

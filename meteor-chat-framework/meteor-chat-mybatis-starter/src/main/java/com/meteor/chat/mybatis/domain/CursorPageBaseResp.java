package com.meteor.chat.mybatis.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="游标分页响应")
public class CursorPageBaseResp <T> implements Serializable {
    @Schema(description ="最后一条数据的游标")
    private String cursor;
    @Schema(description = "是否是最后一页")
    private Boolean isLast = Boolean.FALSE;
    @Schema(description = "查询到的数据")
    private List<T> list;

    public static CursorPageBaseResp empty() {
        return new CursorPageBaseResp(null, true, new ArrayList());
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(list);
    }

    public static <V> CursorPageBaseResp<V> init(CursorPageBaseResp<?> resp, List<V> data) {
        return new CursorPageBaseResp<V>(resp.getCursor(), resp.getIsLast(), data);
    }

}

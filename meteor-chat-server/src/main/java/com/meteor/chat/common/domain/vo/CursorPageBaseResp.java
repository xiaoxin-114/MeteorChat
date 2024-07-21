package com.meteor.chat.common.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("游标分页响应")
public class CursorPageBaseResp <T> {
    @ApiModelProperty("最后一条数据的游标")
    private String cursor;
    @ApiModelProperty("是否是最后一页")
    private Boolean isLast = Boolean.FALSE;
    @ApiModelProperty("查询到的数据")
    private List<T> list;

    public static CursorPageBaseResp empty() {
        return new CursorPageBaseResp(null, true, new ArrayList());
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(list);
    }

    public static CursorPageBaseResp init(CursorPageBaseResp resp, List data) {
        return new CursorPageBaseResp(resp.getCursor(), resp.getIsLast(), data);
    }

}

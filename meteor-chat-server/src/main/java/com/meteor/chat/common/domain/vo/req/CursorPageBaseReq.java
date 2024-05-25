package com.meteor.chat.common.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
@ApiModel("游标翻页请求")
public class CursorPageBaseReq {

    @ApiModelProperty("页面大小")
    private int pageSize;
    @ApiModelProperty("游标")
    private String cursor;

    public Page plusPage() {
        // 表示不需要查询数据总数量，
        // 每次比前端要求的多查询一条数据，这样子能够通过查询到的数据是否为pageSize数量来判断是否后面没数据了
        return new Page(1, pageSize + 1, false);
    }
}

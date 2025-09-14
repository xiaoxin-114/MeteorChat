package com.meteor.chat.mybatis.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
@Schema(description = "游标翻页请求")
public class CursorPageBaseReq implements Serializable {

    @NotNull
    @Min(0)
    @Max(100)
    @Schema(description = "页面大小")
    private Integer pageSize;
    
    @Schema(description = "游标")
    private String cursor;

    public Page plusPage() {
        // 表示不需要查询数据总数量，
        // 每次比前端要求的多查询一条数据，这样子能够通过查询到的数据是否为pageSize数量来判断是否后面没数据了
        return new Page(1, pageSize + 1, false);
    }
}

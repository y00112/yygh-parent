package com.wukong.yygh.common.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created By WuKong on 2022/8/15 15:55
 * 自定义异常
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyException extends RuntimeException{

    @ApiModelProperty(value = "状态码")
    private Integer code;

    private String msg;
}

package com.wukong.yygh.common.handler;

import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.common.result.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created By WuKong on 2022/8/15 15:45
 * 统一异常处理
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseResult error(Exception e){
        e.printStackTrace();
        return ResponseResult.error().message(e.getMessage());
    }

    //自定义异常
    @ExceptionHandler(value = MyException.class)
    public ResponseResult error(MyException e){
        e.printStackTrace();
        return ResponseResult.error().code(e.getCode()).message(e.getMessage());
    }



}

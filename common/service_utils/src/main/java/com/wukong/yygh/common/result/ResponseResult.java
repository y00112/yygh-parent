package com.wukong.yygh.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/15 11:09
 * 统一返回值
 **/
@Data
public class ResponseResult {
    //响应状态码
    private Integer code;
    //响应状态信息
    private String message;
    //提示信息
    private boolean success;
    //响应数据
    private Map<String,Object> data = new HashMap<String,Object>();

    private ResponseResult(){};

    //链式调用
    public static ResponseResult success(){
        ResponseResult resultData = new ResponseResult();
        resultData.setCode(ResultCode.SUCCESS_CODE);
        resultData.setSuccess(true);
        resultData.setMessage("操作成功");
        return resultData;

    }

    public static ResponseResult error(){
        ResponseResult resultData = new ResponseResult();
        resultData.setCode(ResultCode.ERROR_CODE);
        resultData.setSuccess(false);
        resultData.setMessage("操作失败");
        return resultData;

    }

    public ResponseResult code(Integer code){
        this.setCode(code);
        return this;
    }

    public ResponseResult message(String message){
        this.setMessage(message);
        return this;
    }

    public ResponseResult success(Boolean success){
        this.success(success);
        return this;
    }

    public ResponseResult data(String key, Object value){
        this.data.put(key,value);
        return this;
    }

    public ResponseResult data(Map<String,Object> data){
        this.setData(data);
        return this;
    }
}

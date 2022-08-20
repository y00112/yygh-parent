package com.wukong.yygh.hosp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/19 15:30
 **/
public class HttpRequestHelper {
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }
}
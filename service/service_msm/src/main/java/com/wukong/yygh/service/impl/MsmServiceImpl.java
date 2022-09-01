package com.wukong.yygh.service.impl;

import com.wukong.yygh.service.MsmService;
import com.wukong.yygh.utils.HttpUtils;
import com.wukong.yygh.utils.RandomUtil;
import com.wukong.yygh.vo.msm.MsmVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created By WuKong on 2022/8/27 9:53
 **/
@Service
public class MsmServiceImpl implements MsmService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public boolean sendCode(String phone) {
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(redisCode)){
            return true;
        }else {
            String host = "http://dingxin.market.alicloudapi.com";
            String path = "/dx/sendSms";
            String method = "POST";
            String appcode = "bf8ec20ced6f45c192dcedf0a16a2af1";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", phone);
            String code = RandomUtil.getFourBitRandom();
            querys.put("param", "code:" + code);
            querys.put("tpl_id", "TP1711063");
            Map<String, String> bodys = new HashMap<String, String>();


            try {

                /**
                 * 重要提示如下:
                 * HttpUtils请从
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
                 * 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));''

                redisTemplate.opsForValue().set(phone, code, 3, TimeUnit.HOURS);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    @Override
    public void send(MsmVo msmVo) {
        String phone = msmVo.getPhone();
//        this.sendCode(phone);
//        System.out.println(msmVo.getParam().toString());
        System.out.println(phone+"就医提醒短信...");
    }
}

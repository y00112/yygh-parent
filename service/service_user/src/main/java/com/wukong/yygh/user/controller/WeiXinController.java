package com.wukong.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.common.utils.JwtHelper;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.user.service.UserInfoService;
import com.wukong.yygh.user.utils.ConstantPropertiesUtil;
import com.wukong.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/27 14:50
 **/
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeiXinController {

    @Autowired
    private UserInfoService userInfoService;
    /**
     * 获取微信登录参数
     */
    @GetMapping("/getLoginParam")
    @ResponseBody
    public ResponseResult genQrConnect() throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
        return ResponseResult.success().data(map);
    }

    /**
     * 微信服务器回调用户信息
     *  callBack
     */
    @RequestMapping("/callback")
    public String callBack(String code,String state){

      //  https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
            .append(ConstantPropertiesUtil.WX_OPEN_APP_ID)
            .append("&secret=")
            .append(ConstantPropertiesUtil.WX_OPEN_APP_SECRET)
            .append("&code=")
            .append(code)
            .append("&grant_type=authorization_code");

        // 使用HttpClient请求这个地址
        try {
            String json = HttpClientUtils.get(sb.toString());
            JSONObject parseObject = JSONObject.parseObject(json);
            String access_token = parseObject.getString("access_token");
            String openid = parseObject.getString("openid");

            UserInfo userInfo = userInfoService.selectByOpenId(openid);
            if (null == userInfo){
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                // 获取微信信息 https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                StringBuilder userSb = new StringBuilder();
                userSb.append("https://api.weixin.qq.com/sns/userinfo?access_token=")
                        .append(access_token)
                        .append("&openid=")
                        .append(openid);
                String userJson = HttpClientUtils.get(userSb.toString());
                JSONObject UserInfoObject = JSONObject.parseObject(userJson);
                System.out.println(UserInfoObject);
                String nickname = UserInfoObject.getString("nickname");
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }

            // 返回用户信息给前端:name,token
            Map<String,String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }

            //使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            map.put("name", name);

            // 和手机登录不同的地方 绑定手机号
            // 判断根据 openId查询出来的userInfo中手机号是否为空，为空微信说明微信和手机号还没有绑定，返回openid
            if (StringUtils.isEmpty(userInfo.getPhone())){
                map.put("openid",openid);
            }else {
                map.put("openid","");
            }

            return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

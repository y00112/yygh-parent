package com.wukong.yygh.orders.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.wukong.yygh.enums.PaymentStatusEnum;
import com.wukong.yygh.enums.PaymentTypeEnum;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.model.order.PaymentInfo;
import com.wukong.yygh.orders.service.OrderInfoService;
import com.wukong.yygh.orders.service.PaymentService;
import com.wukong.yygh.orders.service.WeiXinPayService;
import com.wukong.yygh.orders.utils.ConstantPropertiesUtils;
import com.wukong.yygh.orders.utils.HttpClient;
import com.wukong.yygh.orders.utils.HttpRequestHelper;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/31 18:23
 **/
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentService paymentService;
    @Override
    public Map createNative(Long orderId) {
        try{

            // 1、根据订单id 获取订单信息
            OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
            // 2、添加支付交易记录
            paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
            // 3、准备参数，xml格式，调用微信服务器接口进行支付
            //1、设置参数
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            Date reserveDate = orderInfo.getReserveDate();
            String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
            String body = reserveDateString + "就诊"+ orderInfo.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//为了测试
            // 终端ip
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");
            //2、HTTPClient来根据URL访问第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            // 4、拿到微信服务器的反回结果  xml 转 map
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //4、封装返回结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", orderInfo.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));

        return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

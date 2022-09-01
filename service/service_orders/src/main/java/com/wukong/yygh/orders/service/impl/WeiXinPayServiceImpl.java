package com.wukong.yygh.orders.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.enums.PaymentStatusEnum;
import com.wukong.yygh.enums.PaymentTypeEnum;
import com.wukong.yygh.enums.RefundStatusEnum;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.model.order.PaymentInfo;
import com.wukong.yygh.model.order.RefundInfo;
import com.wukong.yygh.orders.service.OrderInfoService;
import com.wukong.yygh.orders.service.PaymentService;
import com.wukong.yygh.orders.service.RefundInfoService;
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

    @Autowired
    private RefundInfoService refundInfoService;

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

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try{
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            //1、封装参数
            Map paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            // 2、给微信服务器发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY));
            httpClient.setHttps(true);
            httpClient.post();
            String xml = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean refund(Long orderId) {
        try {
         /*
          2.2.1：退款表添加一条退款数据
         */
            QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
            paymentInfoQueryWrapper.eq("order_id",orderId);
            PaymentInfo paymentInfo = paymentService.getOne(paymentInfoQueryWrapper);
            if (null == paymentInfo){
                throw new MyException(20001,"没有订单记录");
            }
            RefundInfo refundInfo = refundInfoService.saveReFund(paymentInfo);

            if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
                return true;
            }
        /*
            2.2.2：请求微信服务器要退款:cert 证书的支持
            https://api.mch.weixin.qq.com/secapi/pay/refund
        */
            Map<String,String> paramMap = new HashMap<>(8);
            paramMap.put("appid",ConstantPropertiesUtils.APPID);       //公众账号ID
            paramMap.put("mch_id",ConstantPropertiesUtils.PARTNER);   //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
            paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
            //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee","1");
            paramMap.put("refund_fee","1");
            String paramXml = WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(paramXml);
            client.setHttps(true);
            client.setCert(true); // 需要证书的支持
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();

            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

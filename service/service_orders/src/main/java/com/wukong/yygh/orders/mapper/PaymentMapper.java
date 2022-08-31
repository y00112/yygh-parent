package com.wukong.yygh.orders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wukong.yygh.model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created By WuKong on 2022/8/31 18:51
 **/
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentInfo> {
}

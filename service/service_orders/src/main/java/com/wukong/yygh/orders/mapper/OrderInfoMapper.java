package com.wukong.yygh.orders.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.vo.order.OrderCountQueryVo;
import com.wukong.yygh.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author wukong
 * @since 2022-08-30
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> countOrderInfoByQuery(OrderCountQueryVo orderCountQueryVo);
}

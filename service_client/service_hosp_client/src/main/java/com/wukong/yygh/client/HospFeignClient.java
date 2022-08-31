package com.wukong.yygh.client;

import com.wukong.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created By WuKong on 2022/8/30 16:42
 **/
@FeignClient("service-hosp")
public interface HospFeignClient {


    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(
        @ApiParam(name = "scheduleId", value = "排班id", required = true)
        @PathVariable("scheduleId") String scheduleId) ;
}

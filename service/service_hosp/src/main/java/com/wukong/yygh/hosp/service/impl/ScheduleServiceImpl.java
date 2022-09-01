package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wukong.yygh.client.DictFeignClient;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.hosp.repository.ScheduleRepository;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.model.hosp.BookingRule;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.model.hosp.Schedule;
import com.wukong.yygh.vo.hosp.BookingScheduleRuleVo;
import com.wukong.yygh.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By WuKong on 2022/8/20 8:26
 **/
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;


    @Override
    public void save(Map<String, Object> stringObjectMap) {
        String stringJson = JSONObject.toJSONString(stringObjectMap);
        Schedule schedule = JSONObject.parseObject(stringJson, Schedule.class);

        // 1、根据医院编号 和 排版编号 查询排版信息
        Schedule targetSchedule =  scheduleRepository.findByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        // 2、如果有医院信息就做更新操作，没有就做添加操作
        if (null == targetSchedule){
            //0：未上线 1：已上线
            schedule.setStatus(1);
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            // 更新操作
            Integer status = targetSchedule.getStatus();
            schedule.setStatus(status);
            schedule.setIsDeleted(0);
            schedule.setUpdateTime(new Date());
            // 根据 hoscode查询 id
            schedule.setId(targetSchedule.getId());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getSchedulePage(String hoscode, String page, String limit) {

        // 1、分页
        PageRequest pageable = PageRequest.of(Integer.valueOf(page) - 1, Integer.valueOf(limit));
        // 2、创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("hoscode", ExampleMatcher.GenericPropertyMatcher::exact) // 精确查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        // 3、分页条件
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        // 3、 创建查询实例
        Example<Schedule> scheduleExample = Example.of(schedule,matcher);
        Page<Schedule> schedulePage = scheduleRepository.findAll(scheduleExample,pageable);

        return schedulePage;
    }

    @Override
    public void removeSchedule(String hosScheduleId) {
        scheduleRepository.deleteByHosScheduleId(hosScheduleId);
    }

    @Override
    public Map<String, Object> getSchedulePage(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String,Object> map = new HashMap<String,Object>();
        // 1、获取当前页列表
        /*
        TypedAggregation<?> aggregation:
        String inputCollectionName: 输入类型，与集合名称对应的pojo类的字节码
        Class<O> outputType: 输出类型，聚合之后

        select workDate,sum(reservedNumber),sum(availableNumber), count(*) from 表
        where hoscode = #{hoscode} and depcode = #{depcode}
        group by workDate
        Order by workDate ASC
        limit 0, 2
         */
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),   // 设置聚合的查询条件
                Aggregation.group("workDate").first("workDate").as("workDate")
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")  // 预约数
                .sum("availableNumber").as("availableNumber"),   // 空闲数
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                Aggregation.skip((page -1) * limit),
                Aggregation.limit(limit)
        );

        AggregationResults<BookingScheduleRuleVo> aggregationResults =
                mongoTemplate.aggregate(aggregation,Schedule.class, BookingScheduleRuleVo.class);

        // 当前页的列表
        List<BookingScheduleRuleVo> bookingScheduleRuleList = aggregationResults.getMappedResults();

        for(BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleList){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            // Joda Time
            DateTime dateTime = new DateTime(workDate);
            int dayOfWeek = dateTime.getDayOfWeek(); // 周几
            bookingScheduleRuleVo.setDayOfWeek(swapDayWeek(dayOfWeek));
        }
        // 2、获取总记录数 total
        Aggregation aggregationTotal = Aggregation.newAggregation(
                Aggregation.match(criteria),   // 设置聚合的查询条件
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalResult =
                mongoTemplate.aggregate(aggregationTotal,Schedule.class, BookingScheduleRuleVo.class);
        int total = totalResult.getMappedResults().size();


        // 获取最终返回值
        map.put("bookingScheduleRuleList",bookingScheduleRuleList);
        map.put("total",total);


        //获取医院名称
        Hospital hospital = hospitalService.getHospital(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        map.put("baseMap",baseMap);
        return map;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
       List<Schedule> schedules =
               scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
       return schedules;
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {

        // 1、根据医院编号获取医院信息
        Hospital hospital = hospitalService.getHospital(hoscode);
        if (null == hospital){
            throw new MyException(200001,"没有相关医院信息");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获取可预约日期分页数据
        IPage iPage = this.getListDate(page, limit, bookingRule);
        //当前页可预约日期
        List<Date> dateList = iPage.getRecords();

        // 按照预约日期进行分页
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),   // 设置聚合的查询条件
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")  // 预约数
                        .sum("availableNumber").as("availableNumber")   // 空闲数
        );

        AggregationResults<BookingScheduleRuleVo> aggregationResults =
                mongoTemplate.aggregate(aggregation,Schedule.class, BookingScheduleRuleVo.class);

        // 当前页的列表
        List<BookingScheduleRuleVo> bookingScheduleRuleList = aggregationResults.getMappedResults();

        // 使用stream流进行转换
        Map<Date, BookingScheduleRuleVo> collect = bookingScheduleRuleList
                .stream()
                .collect(
                        Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));

        List<BookingScheduleRuleVo> resultBookingList = new ArrayList<>();
        // 当前页的时间列表遍历
        for (int i = 0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(dateList.get(i));
            // 当前日期没有医生出诊的情况
            if (null == bookingScheduleRuleVo){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                // -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            // 周几
            DateTime dateTime = new DateTime(date);
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setDayOfWeek(swapDayWeek(dateTime.getDayOfWeek()));
            int len = dateList.size();
            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == len-1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // 判断第一页第一条的时间，是否已将过了当天放号的时间
            if(i == 0 && page == 1) {
               // 获取当前放号的时间
                DateTime dayTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (dayTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            resultBookingList.add(bookingScheduleRuleVo);
        }
        Map<String, Object> result = new HashMap<>();
        //可预约日期规则数据
        result.put("bookingScheduleList", resultBookingList);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospital(hoscode).getHosname());
        //科室
        Department department =departmentService.getDepartByHoscodeAndDepcode(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    @Override
    public Schedule getScheduleInfoByscheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        packageSchedule(schedule);
        return schedule;
    }


    //根据排班id获取预约下单数据实现
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        //排班信息
        Schedule schedule = this.getScheduleInfoByscheduleId(scheduleId);
        if(null == schedule) {
            throw new MyException(20001,"排班信息不存在");
        }

        //医院信息
        Hospital hospital = hospitalService.getHospital(schedule.getHoscode());
        if(null == hospital) {
            throw new MyException();
        }
        // 医院的预约规则
        BookingRule bookingRule = hospital.getBookingRule();
        if(null == bookingRule) {
            throw new MyException();
        }

        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepartByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()).getDepname());
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());
        return scheduleOrderVo;
    }

    @Override
    public void update(Schedule schedule) {
        Schedule schedule1 = scheduleRepository.findById(schedule.getId()).get();
        schedule1.setReservedNumber(schedule.getReservedNumber());
        schedule1.setAvailableNumber(schedule.getAvailableNumber());
        scheduleRepository.save(schedule1);

    }

    private void packageSchedule(Schedule schedule) {
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        schedule.getParam().put("hosname",hospitalService.getHospital(hoscode).getHosname());
        schedule.getParam().put("depname",(departmentService.getDepartByHoscodeAndDepcode(hoscode,depcode).getDepname()));
        schedule.getParam().put("dayOfWeek",swapDayWeek(new DateTime(schedule.getWorkDate()).getDayOfWeek()));
    }

    /**
     * 获取可预约日期分页数据
     */
    private IPage<Date> getListDate(int page, int limit, BookingRule bookingRule) {
        //当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //预约周期
        int cycle = bookingRule.getCycle();
        //如果当天放号时间已过，则预约周期后一天为即将放号时间，周期加1
        if(releaseTime.isBeforeNow()) cycle += 1;
        //可预约所有日期，最后一天显示即将放号倒计时
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            //计算当前预约日期
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        //日期分页，由于预约周期不一样，页面一排最多显示7天数据，多了就要分页显示
        List<Date> pageDateList = new ArrayList<>();
        int start = (page-1)*limit;
        int end = (page-1)*limit+limit;
        if(end >dateList.size()) end = dateList.size();
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }

        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private String swapDayWeek(int dayOfWeek) {
        if (dayOfWeek == 1){
            return "周一";
        }else if (dayOfWeek == 2){
            return "周二";
        }else if (dayOfWeek == 3){
            return "周三";
        }else if (dayOfWeek == 4){
            return "周四";
        }else if (dayOfWeek == 5){
            return "周五";
        }else if (dayOfWeek == 6){
            return "周六";
        }else if (dayOfWeek == 7){
            return "周日";
        }
        return "";
    }
}

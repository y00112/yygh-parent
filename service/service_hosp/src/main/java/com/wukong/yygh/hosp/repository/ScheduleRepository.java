package com.wukong.yygh.hosp.repository;

import com.wukong.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created By WuKong on 2022/8/20 8:24
 **/
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule findByHoscodeAndHosScheduleId(String hoscode,String hosScheduleId);

    void deleteByHosScheduleId(String hosScheduleId);
}

package com.wukong.yygh.hosp.repository;

import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created By WuKong on 2022/8/19 14:57
 **/
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    Hospital findByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);

}

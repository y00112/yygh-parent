package com.wukong.yygh.hosp.repository;

import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created By WuKong on 2022/8/19 14:57
 **/
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department findByHoscodeAndDepcode(String hoscode, String depcode);

    void deleteDepartmentByDepcode(String decode);

}

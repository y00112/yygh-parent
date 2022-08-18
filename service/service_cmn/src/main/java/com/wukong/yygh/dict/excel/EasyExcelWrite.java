package com.wukong.yygh.dict.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created By WuKong on 2022/8/17 16:51
 **/
public class EasyExcelWrite {
    public static void main(String[] args) {

        List<Student> list = new ArrayList<Student>();
        for (int i = 0; i < 10; i++) {
            Student data = new Student();
            data.setId(i);
            data.setName("张三"+i);
            data.setGender(true);
            data.setAddress(UUID.randomUUID().toString().substring(0,6));
            list.add(data);
        }

        EasyExcel.write("C:\\Users\\admin\\Desktop\\student.xlsx",Student.class)
                .sheet()
                .doWrite(list);
    }
}

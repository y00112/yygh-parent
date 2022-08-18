package com.wukong.yygh.dict.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created By WuKong on 2022/8/17 16:56
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @ExcelProperty(value = "id")
    private  Integer id;

    @ExcelProperty(value = "学生姓名")
    private String name;

    @ExcelProperty(value = "性别")
    private boolean gender;

    @ExcelProperty(value = "地址")
    private String address;
}

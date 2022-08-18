package com.wukong.yygh.dict.excel;

import com.alibaba.excel.EasyExcel;

/**
 * Created By WuKong on 2022/8/18 9:13
 **/
public class EasyExcelRead {
    public static void main(String[] args) {
        EasyExcel.read("C:\\Users\\admin\\Desktop\\student.xlsx",Student.class,new DemoDataListener())
                .sheet().doRead();
    }
}

package com.wukong.yygh.dict.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created By WuKong on 2022/8/18 9:14
 **/
@Slf4j
public class DemoDataListener extends AnalysisEventListener<Student> {

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        log.info("Excel文件的表头为：{}",headMap);

    }

    //解析文件逐行解析的
    @Override
    public void invoke(Student student, AnalysisContext analysisContext) {
        log.info("student:{}",student);
    }

    //excel文件中的数据读取完成之后，要做的事情
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}

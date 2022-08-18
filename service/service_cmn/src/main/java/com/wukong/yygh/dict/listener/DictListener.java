package com.wukong.yygh.dict.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.wukong.yygh.dict.mapper.DictMapper;
import com.wukong.yygh.model.cmn.Dict;
import com.wukong.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By WuKong on 2022/8/18 11:42
 **/
public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    private List<Dict> dictList = new ArrayList<>();

    public DictListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictMapper.insert(dict);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}

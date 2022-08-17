package com.wukong.yygh.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wukong.yygh.dict.mapper.DictMapper;
import com.wukong.yygh.dict.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.model.cmn.Dict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author wukong
 * @since 2022-08-17
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public List<Dict> getDickListByPid(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        // 遍历查看子元素
        for(Dict d : dicts){
            Boolean result = hasChild(d);
            d.setHasChildren(result);
        }
        return dicts;
    }

    // 判断当前元素是否有下一子元素
    private Boolean hasChild(Dict d) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",d.getId());
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0 ){
            return true;
        }
        return false;
    }
}

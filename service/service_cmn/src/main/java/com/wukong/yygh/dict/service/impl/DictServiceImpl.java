package com.wukong.yygh.dict.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wukong.yygh.dict.excel.DemoDataListener;
import com.wukong.yygh.dict.excel.Student;
import com.wukong.yygh.dict.listener.DictListener;
import com.wukong.yygh.dict.mapper.DictMapper;
import com.wukong.yygh.dict.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.model.cmn.Dict;
import com.wukong.yygh.vo.cmn.DictEeVo;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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

    @Cacheable(value = "dict")
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

    @Override
    public void exportExcel(HttpServletResponse response) {
        try {
        //导出excel 一定要设置两个头信息：Mime-Type、content-type：attachment
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = null;

            // 防止乱码
            fileName = URLEncoder.encode("数据字典", "UTF-8");

        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        // 查询数据
        List<Dict> dicts = baseMapper.selectList(null);

        List<DictEeVo> dictEeVoList = new ArrayList<>();

        // 遍历赋值
        for(Dict d:dicts){
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(d,dictEeVo); // 作用将一个对的属性值复制到另一个对象的属性上
            dictEeVoList.add(dictEeVo);
        }


        // 写出
        EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                .sheet()
                .doWrite(dictEeVoList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importData(MultipartFile file) {
        try {
            // 解析上传的文件
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper))
                    .sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getDictByParentDictCodeAndValue(String parentDictCode, String value) {
        // 判断 dictCode是否为空
        if (StringUtils.isEmpty(parentDictCode)){
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            if (null != dict){
                return dict.getName();
            }
        }else {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("dict_code",parentDictCode);
            Dict d2 = baseMapper.selectOne(queryWrapper);
            Long parentId = d2.getId();

            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("parent_id",parentId);
            queryWrapper1.eq("value",value);
            Dict dict = baseMapper.selectOne(queryWrapper1);
            if (null != dict){
                return dict.getName();
            }
        }

        return null;
    }

    @Override
    public List<Dict> getDictByDictCode(String dictCode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        if (null != dict){
            Long parentId = dict.getId();
            QueryWrapper<Dict> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("parent_id",parentId);
            List<Dict> list = baseMapper.selectList(queryWrapper1);
            return list;
        }
        return null;
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

package com.wukong.yygh.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author wukong
 * @since 2022-08-17
 */
public interface DictService extends IService<Dict> {

    List<Dict> getDickListByPid(Long id);

    void exportExcel(HttpServletResponse response);

    void importData(MultipartFile file);
}

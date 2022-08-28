package com.wukong.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created By WuKong on 2022/8/28 18:06
 **/
public interface FileService {
    String uploadFile(MultipartFile file);
}

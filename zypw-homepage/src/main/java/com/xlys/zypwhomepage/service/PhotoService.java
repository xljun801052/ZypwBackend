package com.xlys.zypwhomepage.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    String uploadFile(Integer userId,MultipartFile file);
}

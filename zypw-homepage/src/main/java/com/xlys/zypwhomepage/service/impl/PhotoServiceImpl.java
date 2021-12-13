package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.config.FastDFSClient;
import com.xlys.zypwhomepage.domain.Photo;
import com.xlys.zypwhomepage.mapper.PhotoMapper;
import com.xlys.zypwhomepage.service.PhotoService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoMapper photoMapper;

    @Transactional
    @SneakyThrows
    @Override
    public String uploadFile(MultipartFile file) {
        String imagePath = FastDFSClient.uploadFile(file.getInputStream(), file.getName()+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        Photo.PhotoBuilder photoBuilder = Photo.builder();
        Photo photo = photoBuilder
                .name(file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")))
                .encodedName(UUID.nameUUIDFromBytes(file.getOriginalFilename().getBytes(StandardCharsets.UTF_8)).toString())
                .path(imagePath)
                .extType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1))
                .size(file.getSize())
                .createTime(LocalDateTime.now())
                .validFlag(1)
                .build();
       photoMapper.saveUploadedFileInfo(photo);
        if (photo.getPid() > 0) {
            return imagePath;
        }
        return "";
    }
}

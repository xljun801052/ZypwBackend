package com.xlys.zypwhomepage.controller;

import com.xlys.zypwhomepage.service.PhotoService;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/picture")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    /**
     * single picture upload
     */
    @PostMapping("/upload")
    public AxiosResult uploadPicture(@RequestParam("userId") Integer userId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.info("Empty uploaded file.");
            return new AxiosResult(200, "success", "");
        }
        String imageUrl = photoService.uploadFile(userId, file);
        return new AxiosResult(200, "success", imageUrl);
    }

    @PostMapping("/delete")
    public AxiosResult deletePicture(HttpServletRequest request, MultipartFile file) {
        return null;
    }

    @PostMapping("/modify")
    public AxiosResult modifyPicture(HttpServletRequest request, MultipartFile file) {
        return null;
    }

    /**
     * pictures batch upload.
     */
    @PostMapping("batch/upload")
    public AxiosResult batchUploadPictures(@RequestParam("userId") Integer userId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                log.info("Empty uploaded file.");
                return new AxiosResult(200, "success", "");
            }
            filePaths.add(photoService.uploadFile(userId, file));
        }
        return new AxiosResult(200, "success", filePaths);
    }

    /**
     * support for watermark.
     */
    public AxiosResult addWatermarkOnPic() {
        return null;
    }

    /**
     * support for expand and reduce scenarios.
     */
    public AxiosResult performExAndRe() {
        return null;
    }

    /**
     * support for special efficacy.
     */
    public AxiosResult addSpecialEfficacy() {
        return null;
    }

    /**
     * support for picture steal link resolution.
     * */
}

package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.upload.conf.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: UploadService <br/>
 * Description: TODO
 * Date 2020/4/29 15:03
 *
 * @author Lenovo
 **/
@Service
@EnableConfigurationProperties({UploadProperties.class})
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties prop;


    public String uploadImage(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (!prop.getAllowTypes().contains(contentType)){
                throw  new MyException(ExceptionEnums.INVALID_FILE_TYPE);
            }
            BufferedImage read = ImageIO.read(file.getInputStream());
            //文件内容校验  read.getHeight()....  可以读取出来
            if (read==null){
                throw  new MyException(ExceptionEnums.INVALID_FILE_TYPE);
            }
            //1.保存文件
//            File dest = new File("D:\\Java\\idea\\leyou-upload" + file.getOriginalFilename());
//            file.transferTo(dest);
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                    extension, null);
            //2.返回路径
            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            throw new MyException(ExceptionEnums.UPLOAD_FILE_ERROR);
        }

    }
}

package com.mymmall.service.Impl;

import com.google.common.collect.Lists;
import com.mymmall.service.FileService;
import com.mymmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String upload(MultipartFile file, String path) {
        //获得上传文件的名字
        String fileName = file.getOriginalFilename();
        String fileExtendedName = fileName.substring(fileName.lastIndexOf("."));

        String uploadName = UUID.randomUUID().toString().concat(fileExtendedName);
        logger.info("开始上传文件，上传的文件原名{}，上传的地址{}，上传后的名字{}", fileName, path, uploadName);
        //声明文件夹
        File uploadFileDir = new File(path);
        if (!uploadFileDir.exists()) {
            //设置文件可写权限为true
            uploadFileDir.setWritable(true);
            //创建文件夹
            uploadFileDir.mkdirs();
        }
        File newFile = new File(path, uploadName);
        try {
            //文件上传到了项目的文件夹
            file.transferTo(newFile);
            //FIXME 把文件上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(newFile));
            //把项目中的文件删除 防止图片太多硬盘压力
            newFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return newFile.getName();
    }
}

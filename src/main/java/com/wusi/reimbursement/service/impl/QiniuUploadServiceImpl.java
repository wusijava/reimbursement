package com.wusi.reimbursement.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import com.wusi.reimbursement.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlu
 * @date 2019/12/31 17:12
 */

@ConditionalOnProperty(value = "upload.qiniu.bucket")
@Service(value = "qiniuUploadService")
@Slf4j
public class QiniuUploadServiceImpl implements UploadService {

    @Value("${upload.qiniu.bucket}")
    private String bucket;

    @Value("${upload.qiniu.accessKey}")
    private String accessKey;

    @Value("${upload.qiniu.secretKey}")
    private String secretKey;

    @Value("${upload.qiniu.host}")
    private String host;

    private Region region = Region.huanan();

    @Override
    public Map<String, Object> getUploadToken(String fileName) {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket, fileName);
        Map<String, Object> result = new HashMap<>();
        result.put("token", upToken);
        result.put("key", fileName);
        result.put("host", host);
        return result;
    }

    @Override
    public boolean moveFile(String oldFileName, String newFileName){
        Auth auth = Auth.create(accessKey, secretKey);
        Configuration cfg = new Configuration(region);
        BucketManager bucketManager = new BucketManager(auth,cfg);
        try {
            bucketManager.move(bucket,oldFileName,bucket,newFileName);
            return true;
        } catch (QiniuException e) {
            log.error("七牛移动文件出错{}",e);
            return false;
        }
    }
}

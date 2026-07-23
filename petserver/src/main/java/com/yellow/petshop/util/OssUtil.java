package com.yellow.petshop.util;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
public class OssUtil {
    @Value("${aliyun.oss.endpoint}")
    private String ENDPOINT;

    @Value("${aliyun.oss.bucket-name}")
    private String BUCKET_NAME;

    @Value("${aliyun.oss.region-id}")
    private String REGION_ID;

    @Value("${aliyun.oss.url-prefix}")
    private String URL_PREFIX;

    private OSS getOssClient() throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider =
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
                .endpoint(ENDPOINT)
                .region(REGION_ID)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(conf)
                .build();
    }

    /**
     * 上传文件到OSS
     *
     * @param file         上传的文件
     * @param businessPath 业务子目录路径（如 "user/avatar"、"comment"、"goods"），传 null 或空字符串则直接放在根目录
     * @return 文件的完整OSS访问URL
     */
    public String uploadFile(MultipartFile file, String businessPath) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 拼接对象名：businessPath/filename
        String objectName;
        if (businessPath != null && !businessPath.isEmpty()) {
            objectName = businessPath + "/" + filename;
        } else {
            objectName = filename;
        }

        OSS ossClient = getOssClient();
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectResult result = ossClient.putObject(BUCKET_NAME, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        // 返回完整OSS访问URL
        return URL_PREFIX + "/" + objectName;
    }

    /**
     * 上传文件到OSS（不带业务子目录，直接放在根目录）
     */
    public String uploadFile(MultipartFile file) throws Exception {
        return uploadFile(file, null);
    }

    /**
     * 从OSS删除文件
     *
     * @param relativePath 文件的相对路径（objectName），例如 "goods/uuid.jpg" 或 "user/avatar/uuid.jpg"
     * @return 是否删除成功
     */
    public Boolean deleteFile(String relativePath) {
        OSS ossClient = null;
        try {
            ossClient = getOssClient();
            ossClient.deleteObject(BUCKET_NAME, relativePath);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("删除OSS文件失败: " + relativePath, e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
package com.yellow.petshop.util;

import com.yellow.petshop.config.FileUploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传工具类
 * 统一处理各种业务场景的文件上传
 */
@Component
public class FileUploadUtil {

    private static FileUploadConfig fileUploadConfig;

    @Autowired
    public void setFileUploadConfig(FileUploadConfig config) {
        FileUploadUtil.fileUploadConfig = config;
    }

    /**
     * 业务类型枚举
     */
    public enum BusinessType {
        USER_AVATAR("user/avatar", 2 * 1024 * 1024, Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp")),
        COMMENT_IMAGE("comment", 5 * 1024 * 1024, Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp")),
        GOODS_IMAGE("goods", 10 * 1024 * 1024, Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp"));

        private final String path;                // 存储路径
        private final long maxSize;               // 最大文件大小（字节）
        private final List<String> allowedTypes;  // 允许的文件类型

        BusinessType(String path, long maxSize, List<String> allowedTypes) {
            this.path = path;
            this.maxSize = maxSize;
            this.allowedTypes = allowedTypes;
        }

        public String getPath() { return path; }
        public long getMaxSize() { return maxSize; }
        public List<String> getAllowedTypes() { return allowedTypes; }
    }

    /**
     * 上传结果类
     */
    public static class UploadResult {
        private boolean success;
        private String message;
        private String imageUrl;
        private String fileName;
        private Long fileSize;
        private String uploadTime;

        public UploadResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public UploadResult(boolean success, String message, String imageUrl,
                            String fileName, Long fileSize, String uploadTime) {
            this.success = success;
            this.message = message;
            this.imageUrl = imageUrl;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.uploadTime = uploadTime;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        public String getUploadTime() { return uploadTime; }
        public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }
    }

    /**
     * 上传文件
     *
     * @param file         上传的文件
     * @param businessType 业务类型
     * @param businessId   业务ID（如用户ID、商品ID等）
     * @return 上传结果
     */
    public static UploadResult uploadFile(MultipartFile file, BusinessType businessType, Long businessId) {
        // 1. 验证文件是否为空
        if (file == null || file.isEmpty()) {
            return new UploadResult(false, "请选择要上传的文件");
        }

        // 2. 通过魔数验证真实文件类型（防止伪造 Content-Type 绕过校验）
        String detectedType = detectMimeType(file);
        if (detectedType == null || !businessType.getAllowedTypes().contains(detectedType)) {
            return new UploadResult(false, "不支持的文件类型，仅支持 " + getAllowedTypesText(businessType.getAllowedTypes()));
        }

        // 3. 验证文件大小
        if (file.getSize() > businessType.getMaxSize()) {
            return new UploadResult(false, "文件大小超过限制，最大支持 " + formatFileSize(businessType.getMaxSize()));
        }

        try {
            // 4. 服务端根据真实 MIME 类型决定扩展名（不信任客户端文件名）
            String extension = mimeToExtension(detectedType);

            // 5. 生成文件名
            String fileName = generateFileName(businessType, businessId, extension);

            // 6. 确定保存路径
            String uploadDir = fileUploadConfig.getBaseDir() + "/" + businessType.getPath() + "/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 7. 保存文件
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            // 8. 生成相对路径URL
            String businessPath = businessType.getPath() + "/" + fileName;
            String imageUrl = fileUploadConfig.getRelativeUrl(businessPath);

            // 9. 删除旧文件（仅用户头像场景，且 businessId 不为空时）
            if (businessType == BusinessType.USER_AVATAR && businessId != null) {
                deleteOldFiles(directory, businessType, businessId, fileName);
            }

            // 10. 返回结果
            String uploadTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            return new UploadResult(true, "上传成功", imageUrl, fileName, file.getSize(), uploadTime);

        } catch (IOException e) {
            e.printStackTrace();
            return new UploadResult(false, "文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new UploadResult(false, "文件上传失败，请稍后重试");
        }
    }

    /**
     * 删除文件（含路径穿越防护）
     *
     * @param imageUrl 图片URL（相对路径）
     * @return 是否删除成功
     */
    public static boolean deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        try {
            String relativePath = imageUrl;
            if (imageUrl.startsWith(fileUploadConfig.getUrlPrefix())) {
                relativePath = imageUrl.substring(fileUploadConfig.getUrlPrefix().length());
            }

            // 路径穿越防护：确保目标路径在 baseDir 范围内
            Path basePath = Paths.get(fileUploadConfig.getBaseDir()).toAbsolutePath().normalize();
            Path targetPath = Paths.get(fileUploadConfig.getBaseDir() + relativePath).toAbsolutePath().normalize();
            if (!targetPath.startsWith(basePath)) {
                return false;
            }

            File file = targetPath.toFile();
            return file.exists() && file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 通过读取文件魔数（Magic Bytes）检测真实 MIME 类型
     */
    private static String detectMimeType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            int read = is.read(header);
            if (read < 4) return null;
            // JPEG: FF D8 FF
            if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF) {
                return "image/jpeg";
            }
            // PNG: 89 50 4E 47
            if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50
                    && (header[2] & 0xFF) == 0x4E && (header[3] & 0xFF) == 0x47) {
                return "image/png";
            }
            // GIF: 47 49 46 38
            if ((header[0] & 0xFF) == 0x47 && (header[1] & 0xFF) == 0x49
                    && (header[2] & 0xFF) == 0x46 && (header[3] & 0xFF) == 0x38) {
                return "image/gif";
            }
            // WebP: RIFF????WEBP
            if (read >= 12
                    && (header[0] & 0xFF) == 0x52 && (header[1] & 0xFF) == 0x49
                    && (header[2] & 0xFF) == 0x46 && (header[3] & 0xFF) == 0x46
                    && (header[8] & 0xFF) == 0x57 && (header[9] & 0xFF) == 0x45
                    && (header[10] & 0xFF) == 0x42 && (header[11] & 0xFF) == 0x50) {
                return "image/webp";
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 根据 MIME 类型返回安全的文件扩展名（服务端强制决定）
     */
    private static String mimeToExtension(String mimeType) {
        if (mimeType == null) return ".bin";
        switch (mimeType) {
            case "image/jpeg": return ".jpg";
            case "image/png":  return ".png";
            case "image/gif":  return ".gif";
            case "image/webp": return ".webp";
            default:           return ".bin";
        }
    }

    /**
     * 生成文件名
     */
    private static String generateFileName(BusinessType businessType, Long businessId, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        String prefix;
        switch (businessType) {
            case USER_AVATAR:   prefix = "user";    break;
            case COMMENT_IMAGE: prefix = "comment"; break;
            case GOODS_IMAGE:   prefix = "goods";   break;
            default:            prefix = "file";
        }
        if (businessId != null) {
            return prefix + "_" + businessId + "_" + timestamp + "_" + random + extension;
        } else {
            return prefix + "_" + timestamp + "_" + random + extension;
        }
    }

    /**
     * 删除旧文件（用于替换头像时清理旧文件）
     */
    private static void deleteOldFiles(File directory, BusinessType businessType,
                                       Long businessId, String currentFileName) {
        String prefix;
        switch (businessType) {
            case USER_AVATAR:   prefix = "user_" + businessId + "_";    break;
            case COMMENT_IMAGE: prefix = "comment_" + businessId + "_"; break;
            case GOODS_IMAGE:   prefix = "goods_" + businessId + "_";   break;
            default: return;
        }
        File[] oldFiles = directory.listFiles((dir, name) ->
                name.startsWith(prefix) && !name.equals(currentFileName));
        if (oldFiles != null) {
            for (File oldFile : oldFiles) {
                oldFile.delete();
            }
        }
    }

    /**
     * 获取允许的文件类型描述文本
     */
    private static String getAllowedTypesText(List<String> allowedTypes) {
        List<String> extensions = new ArrayList<>();
        for (String type : allowedTypes) {
            if (type.contains("jpeg"))      extensions.add("jpg");
            else if (type.contains("png"))  extensions.add("png");
            else if (type.contains("gif"))  extensions.add("gif");
            else if (type.contains("webp")) extensions.add("webp");
        }
        return String.join("、", extensions) + " 格式";
    }

    /**
     * 格式化文件大小
     */
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + "KB";
        } else {
            return (size / (1024 * 1024)) + "MB";
        }
    }
}

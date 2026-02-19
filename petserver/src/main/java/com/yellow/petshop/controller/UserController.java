package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.UserInfo;
import com.yellow.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 统一管理用户相关的所有接口（登录、注册等）
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * 访问路径: POST /api/user/login
     *
     * @param loginDTO 登录信息（用户名、密码）
     * @return 登录结果，包含用户ID或token
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        // 参数校验
        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        try {
            // 调用服务层进行登录
            String token = userService.login(loginDTO);
            return Result.success(token);
        } catch (RuntimeException e) {
            // 捕获业务异常
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 用户注册接口
     * 访问路径: POST /api/user/register
     *
     * @param registerDTO 注册信息（用户名、密码、邮箱、昵称、头像）
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        // 参数校验
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }

        try {
            // 调用服务层进行注册
            userService.register(registerDTO);
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            // 捕获业务异常
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * 访问路径: GET /api/user/info
     * 需要JWT认证
     *
     * @param userId 从JWT拦截器中提取的用户ID
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<UserInfo> getInfo(@RequestAttribute("userId") Long userId) {
        try {
            UserInfo userInfo = userService.getInfo(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 更新用户信息接口
     * 访问路径: POST /api/user/update_info
     * 需要JWT认证
     *
     * @param userInfo 用户信息
     * @return 更新结果
     */
    @PostMapping("/update_info")
    public Result<String> updateInfo(@RequestBody UserInfo userInfo) {
        // 参数校验
        if (userInfo.getUsername() == null || userInfo.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }

        try {
            // 调用服务层进行更新
            userService.updateInfo(userInfo);
            return Result.success("更新成功");
        } catch (RuntimeException e) {
            // 捕获业务异常
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出登录接口
     * 访问路径: POST /api/user/logout
     * 需要JWT认证
     *
     * @return 退出结果
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，前端删除token即可
        // 如果需要服务端记录token黑名单，可以在这里实现
        return Result.success("退出成功");
    }

    /**
     * 上传头像接口
     * 访问路径: POST /api/user/upload_avatar
     * 需要JWT认证
     *
     * @param file 上传的头像文件
     * @param userId 从JWT拦截器中提取的用户ID
     * @return 上传结果，包含头像URL
     */
    @PostMapping("/upload_avatar")
    public Result<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("userId") Long userId) {
        
        // 1. 验证文件是否为空
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        // 2. 验证文件类型
        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            return Result.error("不支持的文件类型，仅支持 jpg、png、gif、webp 格式");
        }

        // 3. 验证文件大小（2MB）
        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return Result.error("文件大小超过限制，最大支持 2MB");
        }

        try {
            // 4. 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 5. 生成文件名：user_{userId}_{timestamp}.{extension}
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "user_" + userId + "_" + timestamp + extension;

            // 6. 确定保存路径：static/images/user/avatar/
            String uploadDir = "src/main/resources/static/images/user/avatar/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 7. 保存新文件
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            // 8. 生成访问URL（使用 /api/images 避免与前端 public/images 冲突）
            String avatarUrl = "/api/images/user/avatar/" + fileName;

            // 9. 更新数据库中的头像URL
            userService.updateAvatar(userId, avatarUrl);

            // 10. 删除该用户的旧头像文件（在数据库更新成功后再删除）
            File[] oldFiles = directory.listFiles((dir, name) -> 
                name.startsWith("user_" + userId + "_") && !name.equals(fileName)
            );
            if (oldFiles != null) {
                for (File oldFile : oldFiles) {
                    oldFile.delete();
                }
            }

            // 11. 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            data.put("fileName", fileName);
            data.put("fileSize", file.getSize());
            data.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            return Result.success(data);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败，请稍后重试");
        }
    }
}

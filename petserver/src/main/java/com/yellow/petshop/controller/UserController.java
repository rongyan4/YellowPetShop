package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.UserInfo;
import com.yellow.petshop.service.UserService;
import com.yellow.petshop.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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
        
        // 使用统一的文件上传工具
        FileUploadUtil.UploadResult result = FileUploadUtil.uploadFile(
            file, 
            FileUploadUtil.BusinessType.USER_AVATAR, 
            userId
        );
        
        if (!result.isSuccess()) {
            return Result.error(result.getMessage());
        }
        
        try {
            // 更新数据库中的头像URL
            userService.updateAvatar(userId, result.getImageUrl());
            
            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", result.getImageUrl());
            data.put("fileName", result.getFileName());
            data.put("fileSize", result.getFileSize());
            data.put("uploadTime", result.getUploadTime());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新头像失败：" + e.getMessage());
        }
    }
}

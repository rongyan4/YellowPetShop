package com.yellow.petshop.service;

import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.model.user.UserInfo;

public interface UserService {
    public void register(RegisterDTO registerDTO);
    public String login(LoginDTO loginDTO);
    /** 双Token登录，返回 [refreshToken, accessToken] */
    public String[] loginDualToken(LoginDTO loginDTO);
    /** 用 Refresh Token 换取新 Access Token */
    public String refreshAccessToken(String refreshToken);
    /** 吊销 Refresh Token（登出时调用） */
    public void revokeRefreshToken(String refreshToken);
    public UserInfo getInfo(Long userId);
    public void updateInfo(UserInfo userInfo);
    public void updateAvatar(Long userId, String avatarUrl);
}

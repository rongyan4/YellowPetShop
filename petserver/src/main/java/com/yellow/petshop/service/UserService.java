package com.yellow.petshop.service;

import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.model.user.UserInfo;

public interface UserService {
    public void register(RegisterDTO registerDTO);
    public String login(LoginDTO loginDTO);
    public UserInfo getInfo(Long userId);
    public void updateInfo(UserInfo userInfo);
    public void updateAvatar(Long userId, String avatarUrl);
}

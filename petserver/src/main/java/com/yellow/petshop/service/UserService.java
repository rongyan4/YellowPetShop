package com.yellow.petshop.service;

import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.model.user.UserInfo;

public interface UserService {
    public void register(RegisterDTO registerDTO);
    public String login(LoginDTO loginDTO);
    public User getUserById(Long id);
    public UserInfo getInfo(Long id);
    public UserInfo getUserInfo(Long userId);
}

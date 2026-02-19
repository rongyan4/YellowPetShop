package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.UserMapper;
import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.model.user.UserInfo;
import com.yellow.petshop.service.UserService;
import com.yellow.petshop.util.BCryptUtil;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查邮箱是否已存在（如果提供了邮箱）
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty()) {
            User existingEmail = userMapper.selectByEmail(registerDTO.getEmail());
            if (existingEmail != null) {
                throw new RuntimeException("邮箱已被注册");
            }
        }

        // 3. 密码加密
        String encryptedPassword = BCryptUtil.encrypt(registerDTO.getPassword());

        // 4. 创建用户对象
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(encryptedPassword)
                .email(registerDTO.getEmail())
                .nickname(registerDTO.getNickname() != null && !registerDTO.getNickname().trim().isEmpty()
                        ? registerDTO.getNickname()
                        : registerDTO.getUsername())
                .status("active") // 默认状态为活跃
                .role("user") // 默认角色为普通用户
                .build();

        // 5. 使用UserMapper保存到数据库
        userMapper.insert(user);
    }

    @Override
    public String login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());

        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        // 2. 验证密码
        boolean passwordMatch = BCryptUtil.verify(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账户已被禁用");
        }

        // 4. 返回token
        JwtUtil jwtUtil = new JwtUtil();
        return jwtUtil.generateToken(user);
    }

    @Override
    public UserInfo getInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 将User转换为UserInfo（不返回密码）
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .role(user.getRole())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .build();
    }

    @Override
    public void updateInfo(UserInfo userInfo){
        User user = userMapper.selectById(userInfo.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setAvatar(userInfo.getAvatar());
        user.setNickname(userInfo.getNickname());
        user.setGender(userInfo.getGender());
        user.setBirthday(userInfo.getBirthday());
        userMapper.updateById(user);
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
    }
}

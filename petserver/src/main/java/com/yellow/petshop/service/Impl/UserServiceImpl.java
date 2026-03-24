package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.UserMapper;
import com.yellow.petshop.model.user.LoginDTO;
import com.yellow.petshop.model.user.RegisterDTO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.model.user.UserInfo;
import com.yellow.petshop.service.RefreshTokenStore;
import com.yellow.petshop.service.UserService;
import com.yellow.petshop.util.BCryptUtil;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RefreshTokenStore refreshTokenStore;

    /** RT 有效期：7天（毫秒） */
    private static final long RT_TTL_MS = 1000L * 60 * 60 * 24 * 7;

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
        return loginDualToken(loginDTO)[0];
    }

    @Override
    public String[] loginDualToken(LoginDTO loginDTO) {
        // 1. 查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        // 2. 验证密码
        if (!BCryptUtil.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 3. 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账户已被禁用");
        }
        // 4. 生成双Token
        JwtUtil jwtUtil = new JwtUtil();
        String refreshToken = jwtUtil.generateToken(user);      // RT，7天，存Cookie
        String accessToken  = jwtUtil.generateAccessToken(user); // AT，2分钟，存localStorage
        // 5. 将 RT 持久化（有状态，支持服务端主动吊销）
        refreshTokenStore.save(refreshToken, user.getId(), "customer", user.getUsername(), RT_TTL_MS);
        return new String[]{refreshToken, accessToken};
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        // 1. 从存储层验证 RT（有状态校验：存在 + 未吊销 + 未过期）
        com.yellow.petshop.model.token.RefreshToken stored = refreshTokenStore.validate(refreshToken);
        if (stored == null) {
            throw new RuntimeException("Refresh Token 无效或已过期，请重新登录");
        }
        if (!"customer".equals(stored.getUserType())) {
            throw new RuntimeException("Token 类型错误");
        }
        // 2. 查询用户（验证账户仍有效）
        User user = userMapper.selectById(stored.getUserId());
        if (user == null || !"active".equals(user.getStatus())) {
            throw new RuntimeException("账户不存在或已被禁用");
        }
        // 3. 生成新 AT 返回（RT 不轮换，继续使用直到过期）
        JwtUtil jwtUtil = new JwtUtil();
        return jwtUtil.generateAccessToken(user);
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        refreshTokenStore.revoke(refreshToken);
    }

    @Override
    public UserInfo getInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 计算会员等级与积分进度
        int totalPoints = user.getPoints() != null ? user.getPoints() : 0;
        MembershipInfo membershipInfo = calculateMembership(totalPoints);

        String levelLabel = "S" + membershipInfo.getLevel();
        // 这里约定最高等级为 S5，之后不再显示下一等级
        int maxLevel = 5;
        String nextLevelLabel = membershipInfo.getLevel() < maxLevel
                ? "S" + (membershipInfo.getLevel() + 1)
                : null;

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
                .createTime(user.getCreateTime() != null ? user.getCreateTime().toString() : null)
                .points(totalPoints)
                .level(levelLabel)
                .currentPoints(membershipInfo.getCurrentPoints())
                .nextLevelPoints(membershipInfo.getNextLevelPoints())
                .nextLevel(nextLevelLabel)
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

    /**
     * 会员等级计算规则：
     * - 初始为 S1
     * - 升级到 S2 需要 20 积分
     * - 之后每升级一级所需积分翻倍：S2→S3 需要 40，S3→S4 需要 80，以此类推
     * - currentPoints 表示当前等级下已累计到下一等级的积分
     * - nextLevelPoints 表示从当前等级升到下一等级所需的总积分
     */
    private MembershipInfo calculateMembership(int totalPoints) {
        int level = 1;
        int stepCost = 20; // S1 -> S2 需要 20 积分
        int maxLevel = 5;  // 最高显示到 S5
        int remaining = Math.max(totalPoints, 0);

        while (level < maxLevel && remaining >= stepCost) {
            remaining -= stepCost;
            level++;
            stepCost *= 2;
        }

        MembershipInfo info = new MembershipInfo();
        info.setLevel(level);
        // 已到最高等级则不再显示进度条
        if (level >= maxLevel) {
            info.setCurrentPoints(0);
            info.setNextLevelPoints(0);
        } else {
            info.setCurrentPoints(remaining);
            info.setNextLevelPoints(stepCost);
        }
        return info;
    }

    /**
     * 内部使用的会员信息结构体
     */
    private static class MembershipInfo {
        private int level;
        private int currentPoints;
        private int nextLevelPoints;

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getCurrentPoints() {
            return currentPoints;
        }

        public void setCurrentPoints(int currentPoints) {
            this.currentPoints = currentPoints;
        }

        public int getNextLevelPoints() {
            return nextLevelPoints;
        }

        public void setNextLevelPoints(int nextLevelPoints) {
            this.nextLevelPoints = nextLevelPoints;
        }
    }
}

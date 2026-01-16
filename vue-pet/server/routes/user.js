const express = require("express");
const router = express.Router();
const { jwtMiddleware } = require('../middlewares/jwtMiddleware');
const jwtUtil = require('../utils/jwtUtil');
const bcrypt = require('bcryptjs');

// 模拟用户数据库（实际项目应该连接真实数据库）
const users = [];

/**
 * 用户注册接口
 * POST /api/user/register
 */
router.post("/register", async (req, res) => {
  try {
    const { username, password, email, nickname } = req.body;

    // 参数验证
    if (!username || !password || !email) {
      return res.json({
        code: 400,
        msg: '用户名、密码和邮箱不能为空',
        data: null
      });
    }

    // 检查用户名是否已存在
    const existingUser = users.find(u => u.username === username);
    if (existingUser) {
      return res.json({
        code: 400,
        msg: '用户名已存在',
        data: null
      });
    }

    // 密码加密
    const hashedPassword = await bcrypt.hash(password, 10);

    // 创建新用户
    const newUser = {
      id: users.length + 1,
      username,
      password: hashedPassword,
      email,
      nickname: nickname || username,
      avatar: null,
      status: 'active',
      role: 'user',
      createdAt: new Date()
    };

    users.push(newUser);

    res.json({
      code: 200,
      msg: '注册成功',
      data: null
    });
  } catch (error) {
    console.error('注册失败:', error);
    res.json({
      code: 500,
      msg: '注册失败，请稍后重试',
      data: null
    });
  }
});

/**
 * 用户登录接口
 * POST /api/user/login
 */
router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    // 参数验证
    if (!username || !password) {
      return res.json({
        code: 400,
        msg: '用户名和密码不能为空',
        data: null
      });
    }

    // 查找用户
    const user = users.find(u => u.username === username);
    if (!user) {
      return res.json({
        code: 400,
        msg: '用户名或密码错误',
        data: null
      });
    }

    // 验证密码
    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return res.json({
        code: 400,
        msg: '用户名或密码错误',
        data: null
      });
    }

    // 检查用户状态
    if (user.status !== 'active') {
      return res.json({
        code: 403,
        msg: '账户已被禁用',
        data: null
      });
    }

    // 生成JWT Token
    const token = jwtUtil.generateToken(user);

    // 返回用户信息（不包含密码）
    const userInfo = {
      id: user.id,
      username: user.username,
      email: user.email,
      nickname: user.nickname,
      avatar: user.avatar,
      role: user.role
    };

    res.json({
      code: 200,
      msg: '登录成功',
      data: {
        token,
        userInfo
      }
    });
  } catch (error) {
    console.error('登录失败:', error);
    res.json({
      code: 500,
      msg: '登录失败，请稍后重试',
      data: null
    });
  }
});

/**
 * 获取当前用户信息接口（需要JWT认证）
 * GET /api/user/info
 */
router.get("/info", jwtMiddleware, (req, res) => {
  try {
    // 从JWT中间件获取用户ID
    const userId = req.userId;

    // 查找用户
    const user = users.find(u => u.id === userId);
    if (!user) {
      return res.json({
        code: 404,
        msg: '用户不存在',
        data: null
      });
    }

    // 返回用户信息（不包含密码）
    const userInfo = {
      id: user.id,
      username: user.username,
      email: user.email,
      nickname: user.nickname,
      avatar: user.avatar,
      status: user.status,
      role: user.role
    };

    res.json({
      code: 200,
      msg: '获取成功',
      data: userInfo
    });
  } catch (error) {
    console.error('获取用户信息失败:', error);
    res.json({
      code: 500,
      msg: '获取用户信息失败',
      data: null
    });
  }
});

/**
 * 退出登录接口（需要JWT认证）
 * POST /api/user/logout
 */
router.post("/logout", jwtMiddleware, (req, res) => {
  // JWT是无状态的，前端删除token即可
  // 如果需要服务端记录token黑名单，可以在这里实现
  res.json({
    code: 200,
    msg: '退出成功',
    data: null
  });
});

module.exports = router;

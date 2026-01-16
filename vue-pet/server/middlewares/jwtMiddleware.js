const jwtUtil = require('../utils/jwtUtil');

/**
 * JWT认证中间件
 * 验证请求中的JWT Token
 */
const jwtMiddleware = (req, res, next) => {
  // 允许OPTIONS请求（CORS预检请求）
  if (req.method === 'OPTIONS') {
    return next();
  }

  // 从请求头中提取Token
  const token = jwtUtil.extractTokenFromHeader(req);

  // 检查Token是否存在
  if (!token) {
    return res.status(401).json({
      code: 401,
      msg: '未提供认证令牌',
      data: null
    });
  }

  // 验证Token
  const payload = jwtUtil.verifyToken(token);
  if (!payload) {
    return res.status(401).json({
      code: 401,
      msg: '认证令牌无效或已过期',
      data: null
    });
  }

  // 将用户信息存储到请求对象中，供后续路由使用
  req.userId = payload.userId;
  req.username = payload.username;

  // Token验证通过，继续处理请求
  next();
};

/**
 * 可选的JWT认证中间件
 * Token存在时验证，不存在时也允许通过
 */
const optionalJwtMiddleware = (req, res, next) => {
  const token = jwtUtil.extractTokenFromHeader(req);

  if (token) {
    const payload = jwtUtil.verifyToken(token);
    if (payload) {
      req.userId = payload.userId;
      req.username = payload.username;
    }
  }

  next();
};

module.exports = {
  jwtMiddleware,
  optionalJwtMiddleware
};

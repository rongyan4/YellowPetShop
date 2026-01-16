const jwt = require('jsonwebtoken');

/**
 * JWT工具类
 * 用于生成和验证JWT Token
 */
class JwtUtil {
  constructor() {
    // JWT密钥（生产环境应该从环境变量读取）
    this.secret = process.env.JWT_SECRET || '2F9s7k8d6j5g4h3f2d1s0a9s8d7f6g5h4j3k2l1m0n9b8v7c6x5z4a8s7d6f5g4h3j2=';
    // Token过期时间（7天）
    this.expirationTime = '7d';
  }

  /**
   * 生成JWT Token
   * @param {Object} user - 用户信息
   * @param {number} user.id - 用户ID
   * @param {string} user.username - 用户名
   * @returns {string} JWT Token
   */
  generateToken(user) {
    const payload = {
      userId: user.id,
      username: user.username,
    };

    return jwt.sign(payload, this.secret, {
      expiresIn: this.expirationTime,
      algorithm: 'HS256'
    });
  }

  /**
   * 验证JWT Token
   * @param {string} token - JWT Token
   * @returns {Object|null} 解码后的payload，验证失败返回null
   */
  verifyToken(token) {
    try {
      return jwt.verify(token, this.secret);
    } catch (error) {
      console.error('Token验证失败:', error.message);
      return null;
    }
  }

  /**
   * 解码JWT Token（不验证签名）
   * @param {string} token - JWT Token
   * @returns {Object|null} 解码后的payload
   */
  decodeToken(token) {
    try {
      return jwt.decode(token);
    } catch (error) {
      console.error('Token解码失败:', error.message);
      return null;
    }
  }

  /**
   * 从请求头中提取Token
   * @param {Object} req - Express请求对象
   * @returns {string|null} Token字符串，不存在返回null
   */
  extractTokenFromHeader(req) {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return null;
    }
    return authHeader.substring(7);
  }
}

module.exports = new JwtUtil();

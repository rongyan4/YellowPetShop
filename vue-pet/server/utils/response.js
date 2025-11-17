/**
 * 统一响应处理工具
 * 提供统一的 API 响应格式
 */

/**
 * 成功响应
 * @param {Object} res - Express 响应对象
 * @param {*} data - 响应数据
 * @param {String} msg - 响应消息，默认为 '操作成功'
 * @param {Number} code - 响应码，默认为 200
 */
const success = (res, data = null, msg = '操作成功', code = 200) => {
  res.json({
    code,
    msg,
    data
  });
};

/**
 * 失败响应
 * @param {Object} res - Express 响应对象
 * @param {String} msg - 错误消息
 * @param {Number} code - 错误码，默认为 500
 * @param {*} data - 额外的错误数据
 */
const error = (res, msg = '操作失败', code = 500, data = null) => {
  res.json({
    code,
    msg,
    data
  });
};

/**
 * 参数错误响应
 * @param {Object} res - Express 响应对象
 * @param {String} msg - 错误消息，默认为 '参数错误'
 */
const badRequest = (res, msg = '参数错误') => {
  error(res, msg, 400);
};

/**
 * 未授权响应
 * @param {Object} res - Express 响应对象
 * @param {String} msg - 错误消息，默认为 '未授权'
 */
const unauthorized = (res, msg = '未授权') => {
  error(res, msg, 401);
};

/**
 * 禁止访问响应
 * @param {Object} res - Express 响应对象
 * @param {String} msg - 错误消息，默认为 '禁止访问'
 */
const forbidden = (res, msg = '禁止访问') => {
  error(res, msg, 403);
};

/**
 * 资源不存在响应
 * @param {Object} res - Express 响应对象
 * @param {String} msg - 错误消息，默认为 '资源不存在'
 */
const notFound = (res, msg = '资源不存在') => {
  error(res, msg, 404);
};

/**
 * 服务器错误响应
 * @param {Object} res - Express 响应对象
 * @param {String} msg - 错误消息，默认为 '服务器内部错误'
 */
const serverError = (res, msg = '服务器内部错误') => {
  error(res, msg, 500);
};

/**
 * 异步控制器包装器
 * 自动捕获异步错误，统一处理异常
 * @param {Function} fn - 异步控制器函数
 * @returns {Function} 包装后的控制器函数
 * 
 * @example
 * // 使用方式
 * exports.getData = asyncHandler(async (req, res) => {
 *   const data = await someAsyncOperation();
 *   success(res, data, '获取成功');
 * });
 */
const asyncHandler = (fn) => {
  return (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch((err) => {
      console.error('控制器错误:', err);
      serverError(res, err.message || '服务器内部错误');
    });
  };
};

module.exports = {
  success,
  error,
  badRequest,
  unauthorized,
  forbidden,
  notFound,
  serverError,
  asyncHandler
};


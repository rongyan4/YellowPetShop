import request from '@/utils/request';

/**
 * 创建新会话
 */
export function createChatSession() {
  return request({
    url: '/chat/session/create',
    method: 'post'
  });
}

/**
 * 获取用户所有会话
 */
export function getUserSessions() {
  return request({
    url: '/chat/session/list',
    method: 'get'
  });
}

/**
 * 获取会话历史记录
 */
export function getSessionHistory(sessionId) {
  return request({
    url: `/chat/history/${sessionId}`,
    method: 'get'
  });
}

/**
 * 发送消息（同步）
 * AI 响应时间较长，单独设置 120s 超时，避免过早触发超时错误
 */
export function sendMessage(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    params: data,
    timeout: 120000
  });
}

/**
 * 清空会话历史
 */
export function clearHistory(sessionId) {
  return request({
    url: `/chat/history/${sessionId}`,
    method: 'delete'
  });
}

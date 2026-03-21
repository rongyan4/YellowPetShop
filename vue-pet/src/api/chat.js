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
 * 构建 SSE 流式发送消息的 URL（供 EventSource 使用）
 * token 通过 HttpOnly Cookie 自动携带，无需手动传递
 * 走 /api 代理路径以确保 Cookie 正确转发
 */
export function buildStreamUrl({ message, sessionId }) {
  const params = new URLSearchParams({ message, sessionId });
  return `/api/chat/send?${params.toString()}`;
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

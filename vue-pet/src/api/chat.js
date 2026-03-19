import request from '@/utils/request';
import { getToken } from '@/utils/auth';

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
 * EventSource 只支持 GET，token 通过 query 参数传递
 */
export function buildStreamUrl({ message, sessionId }) {
  const token = getToken();
  const params = new URLSearchParams({ message, sessionId, token });
  return `http://localhost:3000/api/chat/send?${params.toString()}`;
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

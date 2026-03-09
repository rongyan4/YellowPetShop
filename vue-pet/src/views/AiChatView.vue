<template>
  <div class="ai-chat-view">
    <!-- 顶部导航栏 -->
    <div class="chat-header">
      <div class="header-left">
        <i class="iconfont icon-back" @click="goBack"></i>
        <span class="header-title">AI助手</span>
      </div>
      <div class="header-right">
        <i class="iconfont icon-list" @click="showSessionList = true"></i>
      </div>
    </div>

    <!-- 聊天消息区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="welcome-icon">🤖</div>
        <h3>你好！我是AI助手</h3>
        <p>有什么可以帮助你的吗？</p>
      </div>
      
      <div v-for="msg in messages" :key="msg.id" 
           :class="['message-item', msg.role === 'user' ? 'user-message' : 'assistant-message']">
        <div class="message-avatar">
          <img v-if="msg.role === 'user'" :src="userAvatar" alt="用户">
          <div v-else class="ai-avatar">🤖</div>
        </div>
        <div class="message-content">
          <div class="message-bubble">
            <!-- 用户消息直接显示文本 -->
            <template v-if="msg.role === 'user'">{{ msg.content }}</template>
            <!-- AI消息渲染Markdown -->
            <div v-else class="markdown-body" v-html="renderMarkdown(msg.content)"></div>
          </div>
          <div class="message-time">{{ formatTime(msg.datetime) }}</div>
        </div>
      </div>

      <!-- 加载中提示 -->
      <div v-if="isLoading" class="message-item assistant-message">
        <div class="message-avatar">
          <div class="ai-avatar">🤖</div>
        </div>
        <div class="message-content">
          <div class="message-bubble loading">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area">
      <div class="input-wrapper">
        <textarea 
          v-model="inputMessage" 
          placeholder="输入消息..."
          @keydown.enter.prevent="handleEnter"
          rows="1"
          ref="textarea"
        ></textarea>
        <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim() || isLoading">
          <i class="iconfont icon-send"></i>
        </button>
      </div>
    </div>

    <!-- 会话列表侧边栏 -->
    <div class="session-drawer" :class="{ 'show': showSessionList }">
      <div class="drawer-overlay" @click="showSessionList = false"></div>
      <div class="drawer-content">
        <div class="drawer-header">
          <h3>会话列表</h3>
          <i class="iconfont icon-close" @click="showSessionList = false"></i>
        </div>
        <div class="drawer-body">
          <div class="new-session-btn" @click="createNewSession">
            <i class="iconfont icon-add"></i>
            <span>新建会话</span>
          </div>
          <div class="session-list">
            <div v-for="session in sessions" :key="session.sessionId"
                 :class="['session-item', { 'active': session.sessionId === currentSessionId }]"
                 @click="switchSession(session.sessionId)">
              <div class="session-info">
                <div class="session-preview">{{ session.lastMessage || '新会话' }}</div>
                <div class="session-time">{{ formatTime(session.updateTime) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { marked } from 'marked';
import hljs from 'highlight.js';
import 'highlight.js/styles/github-dark.css';
import { 
  createChatSession, 
  getUserSessions, 
  getSessionHistory, 
  sendMessage as sendChatMessage 
} from '@/api/chat';
import { getToken } from '@/utils/auth';

// 配置marked
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value;
      } catch (err) {
        console.error(err);
      }
    }
    return hljs.highlightAuto(code).value;
  },
  breaks: true,
  gfm: true
});

const router = useRouter();
const messages = ref([]);
const inputMessage = ref('');
const isLoading = ref(false);
const currentSessionId = ref('');
const sessions = ref([]);
const showSessionList = ref(false);
const messagesContainer = ref(null);
const textarea = ref(null);
const userAvatar = ref('/images/default_avatar.png');

// 渲染Markdown内容
function renderMarkdown(content) {
  if (!content) return '';
  try {
    return marked.parse(content);
  } catch (error) {
    console.error('Markdown渲染错误:', error);
    return content;
  }
}

// 初始化
onMounted(async () => {
  if (!getToken()) {
    router.push('/login');
    return;
  }
  
  await loadSessions();
  
  // 如果没有会话，创建一个新会话
  if (sessions.value.length === 0) {
    await createNewSession();
  } else {
    // 加载最近的会话
    currentSessionId.value = sessions.value[0].sessionId;
    await loadHistory();
  }
});

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom();
  });
}, { deep: true });

// 加载会话列表
async function loadSessions() {
  try {
    const res = await getUserSessions();
    if (res.code === 200) {
      sessions.value = res.data;
    }
  } catch (error) {
    console.error('加载会话列表失败', error);
  }
}

// 加载历史记录
async function loadHistory() {
  try {
    const res = await getSessionHistory(currentSessionId.value);
    if (res.code === 200) {
      messages.value = res.data;
    }
  } catch (error) {
    console.error('加载历史记录失败', error);
  }
}

// 创建新会话
async function createNewSession() {
  try {
    const res = await createChatSession();
    if (res.code === 200) {
      currentSessionId.value = res.data;
      messages.value = [];
      await loadSessions();
      showSessionList.value = false;
    }
  } catch (error) {
    console.error('创建会话失败', error);
  }
}

// 切换会话
async function switchSession(sessionId) {
  currentSessionId.value = sessionId;
  await loadHistory();
  showSessionList.value = false;
}

// 发送消息（使用流式输出）
async function sendMessage() {
  if (!inputMessage.value.trim() || isLoading.value) return;
  
  const userMessage = inputMessage.value.trim();
  inputMessage.value = '';
  
  // 添加用户消息到界面
  const userMsgId = Date.now();
  messages.value.push({
    id: userMsgId,
    role: 'user',
    content: userMessage,
    datetime: new Date().toISOString()
  });
  
  isLoading.value = true;
  
  // 创建AI消息占位符
  const aiMsgId = Date.now() + 1;
  const aiMessage = {
    id: aiMsgId,
    role: 'assistant',
    content: '',
    datetime: new Date().toISOString()
  };
  messages.value.push(aiMessage);
  
  try {
    // 使用流式接口
    const token = getToken();
    const baseURL = '/api';
    const url = `${baseURL}/chat/sendStream?message=${encodeURIComponent(userMessage)}&sessionId=${currentSessionId.value}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'text/event-stream'
      }
    });
    
    if (!response.ok) {
      throw new Error('请求失败');
    }
    
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      
      const chunk = decoder.decode(value, { stream: true });
      // 更新AI消息内容
      aiMessage.content += chunk;
      
      // 触发滚动
      nextTick(() => {
        scrollToBottom();
      });
    }
    
    // 刷新会话列表
    await loadSessions();
    
  } catch (error) {
    console.error('发送消息失败', error);
    // 移除失败的AI消息
    const index = messages.value.findIndex(m => m.id === aiMsgId);
    if (index > -1) {
      messages.value.splice(index, 1);
    }
  } finally {
    isLoading.value = false;
  }
}

// 处理回车键
function handleEnter(e) {
  if (e.shiftKey) {
    return; // Shift+Enter 换行
  }
  sendMessage();
}

// 格式化时间
function formatTime(datetime) {
  if (!datetime) return '';
  const date = new Date(datetime);
  const now = new Date();
  const diff = now - date;
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`;
}

// 滚动到底部
function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
}

// 返回
function goBack() {
  router.back();
}
</script>

<style lang="scss" scoped>
.ai-chat-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #eee;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .icon-back {
      font-size: 20px;
      color: #333;
      cursor: pointer;
    }
    
    .header-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }
  
  .header-right {
    .iconfont {
      font-size: 20px;
      color: #333;
      cursor: pointer;
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px;
  background: #f5f5f5;
  
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #999;
    
    .welcome-icon {
      font-size: 48px;
      margin-bottom: 12px;
      animation: float 3s ease-in-out infinite;
    }
    
    h3 {
      font-size: 18px;
      margin-bottom: 8px;
      font-weight: 500;
      color: #333;
    }
    
    p {
      font-size: 14px;
      color: #999;
    }
  }
  
  .message-item {
    display: flex;
    margin-bottom: 16px;
    animation: slideIn 0.3s ease-out;
    
    &.user-message {
      flex-direction: row-reverse;
      
      .message-content {
        align-items: flex-end;
      }
      
      .message-bubble {
        background: #07c160;
        color: #fff;
        border-radius: 8px 8px 2px 8px;
      }
      
      .message-time {
        color: #999;
      }
    }
    
    &.assistant-message {
      .message-bubble {
        background: #fff;
        color: #333;
        border-radius: 8px 8px 8px 2px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      }
    }
  }
  
  .message-avatar {
    width: 36px;
    height: 36px;
    border-radius: 4px;
    overflow: hidden;
    flex-shrink: 0;
    margin: 0 8px;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .ai-avatar {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fff;
      font-size: 20px;
      border: 1px solid #eee;
    }
  }
  
  .message-content {
    display: flex;
    flex-direction: column;
    max-width: 70%;
  }
  
  .message-bubble {
    padding: 10px 12px;
    font-size: 14px;
    line-height: 1.5;
    word-wrap: break-word;
    
    &.loading {
      display: flex;
      gap: 4px;
      padding: 12px 16px;
      
      .dot {
        width: 6px;
        height: 6px;
        background: #999;
        border-radius: 50%;
        animation: bounce 1.4s infinite ease-in-out both;
        
        &:nth-child(1) { animation-delay: -0.32s; }
        &:nth-child(2) { animation-delay: -0.16s; }
      }
    }
  }
  
  // Markdown样式
  .markdown-body {
    font-size: 14px;
    line-height: 1.6;
    color: #333;
    
    // 标题
    h1, h2, h3, h4, h5, h6 {
      margin: 16px 0 8px 0;
      font-weight: 600;
      line-height: 1.25;
      
      &:first-child {
        margin-top: 0;
      }
    }
    
    h1 { font-size: 1.6em; border-bottom: 1px solid #eee; padding-bottom: 8px; }
    h2 { font-size: 1.4em; border-bottom: 1px solid #eee; padding-bottom: 6px; }
    h3 { font-size: 1.2em; }
    h4 { font-size: 1.1em; }
    h5 { font-size: 1em; }
    h6 { font-size: 0.9em; color: #666; }
    
    // 段落
    p {
      margin: 8px 0;
      
      &:first-child {
        margin-top: 0;
      }
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    // 列表
    ul, ol {
      margin: 8px 0;
      padding-left: 24px;
      
      li {
        margin: 4px 0;
      }
    }
    
    // 代码块
    pre {
      background: #282c34;
      border-radius: 6px;
      padding: 12px;
      margin: 12px 0;
      overflow-x: auto;
      
      code {
        background: transparent;
        padding: 0;
        color: #abb2bf;
        font-size: 13px;
        line-height: 1.5;
      }
    }
    
    // 行内代码
    code {
      background: #f6f8fa;
      padding: 2px 6px;
      border-radius: 3px;
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
      font-size: 0.9em;
      color: #e83e8c;
    }
    
    // 引用
    blockquote {
      margin: 12px 0;
      padding: 8px 12px;
      border-left: 4px solid #07c160;
      background: #f6f8fa;
      color: #666;
      
      p {
        margin: 0;
      }
    }
    
    // 表格
    table {
      border-collapse: collapse;
      width: 100%;
      margin: 12px 0;
      
      th, td {
        border: 1px solid #ddd;
        padding: 8px 12px;
        text-align: left;
      }
      
      th {
        background: #f6f8fa;
        font-weight: 600;
      }
      
      tr:nth-child(even) {
        background: #f9f9f9;
      }
    }
    
    // 链接
    a {
      color: #07c160;
      text-decoration: none;
      
      &:hover {
        text-decoration: underline;
      }
    }
    
    // 分割线
    hr {
      border: none;
      border-top: 1px solid #eee;
      margin: 16px 0;
    }
    
    // 图片
    img {
      max-width: 100%;
      height: auto;
      border-radius: 4px;
      margin: 8px 0;
    }
    
    // 强调
    strong {
      font-weight: 600;
    }
    
    em {
      font-style: italic;
    }
    
    // 删除线
    del {
      text-decoration: line-through;
      color: #999;
    }
  }
  
  .message-time {
    font-size: 11px;
    color: #999;
    margin-top: 4px;
    padding: 0 4px;
  }
}

.chat-input-area {
  padding: 12px;
  background: #fff;
  border-top: 1px solid #eee;
  
  .input-wrapper {
    display: flex;
    align-items: flex-end;
    gap: 8px;
    background: #f5f5f5;
    border-radius: 20px;
    padding: 8px 12px;
    
    textarea {
      flex: 1;
      border: none;
      outline: none;
      resize: none;
      font-size: 14px;
      line-height: 1.5;
      max-height: 80px;
      background: transparent;
      color: #333;
      
      &::placeholder {
        color: #999;
      }
    }
    
    .send-btn {
      width: 32px;
      height: 32px;
      border: none;
      border-radius: 50%;
      background: #07c160;
      color: #fff;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;
      flex-shrink: 0;
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
      
      &:active:not(:disabled) {
        transform: scale(0.95);
      }
      
      .iconfont {
        font-size: 16px;
      }
    }
  }
}

.session-drawer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  pointer-events: none;
  
  &.show {
    pointer-events: auto;
    
    .drawer-overlay {
      opacity: 1;
    }
    
    .drawer-content {
      transform: translateX(0);
    }
  }
  
  .drawer-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    opacity: 0;
    transition: opacity 0.3s;
  }
  
  .drawer-content {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    width: 75%;
    max-width: 280px;
    background: #fff;
    transform: translateX(100%);
    transition: transform 0.3s;
    display: flex;
    flex-direction: column;
  }
  
  .drawer-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #eee;
    
    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .icon-close {
      font-size: 20px;
      color: #666;
      cursor: pointer;
    }
  }
  
  .drawer-body {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
  }
  
  .new-session-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
    background: #07c160;
    color: #fff;
    border-radius: 8px;
    cursor: pointer;
    margin-bottom: 12px;
    transition: opacity 0.2s;
    font-size: 14px;
    
    &:active {
      opacity: 0.8;
    }
    
    .iconfont {
      font-size: 16px;
    }
    
    span {
      font-weight: 500;
    }
  }
  
  .session-list {
    .session-item {
      padding: 12px;
      background: #f8f9fa;
      border-radius: 8px;
      margin-bottom: 8px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:active {
        background: #e9ecef;
      }
      
      &.active {
        background: #e8f5e9;
        border: 1px solid #07c160;
      }
      
      .session-info {
        .session-preview {
          font-size: 14px;
          color: #333;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .session-time {
          font-size: 11px;
          color: #999;
        }
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}
</style>

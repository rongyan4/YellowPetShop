<template>
  <div class="ai-chat-page">
    <!-- 顶部导航栏 -->
    <div class="chat-header">
      <button class="back-btn" @click="goBack">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </button>
      <div class="header-title">
        <div class="ai-avatar-header">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2zM7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16 1.5 1.5 0 0 0 9 14.5 1.5 1.5 0 0 0 7.5 13zm9 0A1.5 1.5 0 0 0 15 14.5a1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 16.5 13z"/>
          </svg>
        </div>
        <div>
          <span class="title-text">AI 宠物助手</span>
          <span class="title-sub">随时为您解答</span>
        </div>
      </div>
      <button class="session-list-btn" @click="showSessionList = true">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="8" y1="6" x2="21" y2="6"></line>
          <line x1="8" y1="12" x2="21" y2="12"></line>
          <line x1="8" y1="18" x2="21" y2="18"></line>
          <line x1="3" y1="6" x2="3.01" y2="6"></line>
          <line x1="3" y1="12" x2="3.01" y2="12"></line>
          <line x1="3" y1="18" x2="3.01" y2="18"></line>
        </svg>
      </button>
    </div>

    <!-- 消息列表 -->
    <div class="chat-body" ref="messagesContainer">
      <div v-if="messages.length === 0" class="welcome-area">
        <div class="welcome-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2zM7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16 1.5 1.5 0 0 0 9 14.5 1.5 1.5 0 0 0 7.5 13zm9 0A1.5 1.5 0 0 0 15 14.5a1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 16.5 13z"/>
          </svg>
        </div>
        <h2 class="welcome-title">你好，我是 AI 宠物助手</h2>
        <p class="welcome-desc">我可以帮您解答宠物喂养、健康、护理等各类问题，快来试试吧！</p>
        <div class="suggest-list">
          <button
            v-for="(tip, index) in suggestions"
            :key="index"
            class="suggest-chip"
            @click="useSuggestion(tip)"
          >{{ tip }}</button>
        </div>
      </div>

      <div v-for="msg in messages" :key="msg.id" class="msg-row" :class="msg.role">
        <div v-if="msg.role === 'assistant'" class="msg-avatar">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2zM7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16 1.5 1.5 0 0 0 9 14.5 1.5 1.5 0 0 0 7.5 13zm9 0A1.5 1.5 0 0 0 15 14.5a1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 16.5 13z"/>
          </svg>
        </div>
        <div class="msg-bubble">
          <!-- AI 加载中 -->
          <div v-if="msg.role === 'assistant' && msg.loading" class="typing-dots">
            <span></span><span></span><span></span>
          </div>
          <!-- 用户消息 -->
          <template v-else-if="msg.role === 'user'">{{ msg.content }}</template>
          <!-- AI 消息渲染 Markdown / HTML 商品卡片 -->
          <div v-else class="markdown-body" v-html="renderMarkdown(msg.content)" @click="handleBubbleClick"></div>
        </div>
      </div>
    </div>

    <!-- 底部输入区 -->
    <div class="chat-footer">
      <div class="input-wrap">
        <textarea
          v-model="inputMessage"
          class="chat-input"
          placeholder="输入您的问题..."
          rows="1"
          @keydown.enter.exact.prevent="sendMessage"
          @input="autoResize"
          ref="textarea"
        ></textarea>
        <button
          class="send-btn"
          :class="{ active: inputMessage.trim() }"
          :disabled="!inputMessage.trim() || isLoading"
          @click="sendMessage"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 会话列表侧边栏 -->
    <div class="session-drawer" :class="{ 'show': showSessionList }">
      <div class="drawer-overlay" @click="showSessionList = false"></div>
      <div class="drawer-content">
        <div class="drawer-header">
          <h3>会话列表</h3>
          <button class="close-btn" @click="showSessionList = false">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        <div class="drawer-body">
          <div class="new-session-btn" @click="createNewSession">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            <span>新建会话</span>
          </div>
          <div class="session-list">
            <div
              v-for="session in sessions"
              :key="session.sessionId"
              :class="['session-item', { 'active': session.sessionId === currentSessionId }]"
              @click="switchSession(session.sessionId)"
            >
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
import { ref, onMounted, nextTick, watch } from 'vue';
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

// 配置 marked（允许 HTML 直通，用于 AI 输出的商品卡片等富文本）
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
  gfm: true,
  // 允许 HTML 标签直接透传，使 AI 返回的商品卡片 HTML 能正常渲染
  sanitize: false,
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

const suggestions = [
  '狗狗每天需要喂几次？',
  '猫咪感冒了怎么办？',
  '宠物疫苗怎么安排？',
  '如何给宠物洗澡？',
];

function renderMarkdown(content) {
  if (!content) return '';
  try {
    // marked 在 gfm+html 模式下会将 HTML 标签原样保留，
    // AI 输出的 <div class="product-card">...</div> 等结构可直接渲染
    return marked.parse(content);
  } catch (error) {
    console.error('Markdown渲染错误:', error);
    return content;
  }
}

// 商品卡片「加入购物车」点击处理（AI 渲染的卡片按钮通过事件委托触发）
function handleBubbleClick(event) {
  const btn = event.target.closest('.pc-add-cart');
  if (!btn) return;
  const card = btn.closest('.product-card');
  if (!card) return;
  const productId = card.dataset.id;
  const productName = card.querySelector('.pc-name')?.textContent || '';
  // 跳转到商品详情或直接加购（此处预留路由跳转，可按项目实际接口替换）
  if (productId) {
    router.push({
        path: '/good-details',
        query: { id: productId } // 推荐：通过query传递参数，而非手动拼接
    });
  }
}

onMounted(async () => {
  if (!getToken()) {
    router.push('/login');
    return;
  }
  await loadSessions();
  if (sessions.value.length === 0) {
    await createNewSession();
  } else {
    currentSessionId.value = sessions.value[0].sessionId;
    await loadHistory();
  }
  textarea.value?.focus();
});

watch(messages, () => {
  nextTick(() => scrollToBottom());
}, { deep: true });

async function loadSessions() {
  try {
    const res = await getUserSessions();
    if (res.code === 200) sessions.value = res.data;
  } catch (error) {
    console.error('加载会话列表失败', error);
  }
}

async function loadHistory() {
  try {
    const res = await getSessionHistory(currentSessionId.value);
    if (res.code === 200) {
      messages.value = res.data.map((item, idx) => ({
        id: item.id || idx,
        role: item.role,
        content: item.content,
        datetime: item.datetime,
        loading: false,
      }));
    }
  } catch (error) {
    console.error('加载历史记录失败', error);
  }
}

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

async function switchSession(sessionId) {
  currentSessionId.value = sessionId;
  await loadHistory();
  showSessionList.value = false;
}

function useSuggestion(text) {
  inputMessage.value = text;
  sendMessage();
}

async function sendMessage() {
  if (!inputMessage.value.trim() || isLoading.value) return;

  const userMessage = inputMessage.value.trim();
  inputMessage.value = '';
  resetInputHeight();

  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: userMessage,
    datetime: new Date().toISOString(),
    loading: false,
  });

  isLoading.value = true;

  const aiMsgId = Date.now() + 1;
  const aiMessage = {
    id: aiMsgId,
    role: 'assistant',
    content: '',
    datetime: new Date().toISOString(),
    loading: true,
  };
  messages.value.push(aiMessage);

  try {
    const res = await sendChatMessage({ message: userMessage, sessionId: currentSessionId.value });
    const idx = messages.value.findIndex(m => m.id === aiMsgId);
    if (idx > -1) {
      messages.value[idx] = {
        ...messages.value[idx],
        content: res.data || '收到您的问题，正在处理...',
        loading: false,
      };
    }
    await loadSessions();
  } catch (error) {
    console.error('发送消息失败', error);
    const idx = messages.value.findIndex(m => m.id === aiMsgId);
    if (idx > -1) {
      messages.value[idx] = {
        ...messages.value[idx],
        content: '抱歉，服务暂时不可用，请稍后重试。',
        loading: false,
      };
    }
  } finally {
    isLoading.value = false;
  }
}

function handleEnter(e) {
  if (e.shiftKey) return;
  sendMessage();
}

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

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
}

function autoResize() {
  const el = textarea.value;
  if (!el) return;
  el.style.height = 'auto';
  el.style.height = Math.min(el.scrollHeight, 120) + 'px';
}

function resetInputHeight() {
  const el = textarea.value;
  if (!el) return;
  el.style.height = 'auto';
}

function goBack() {
  router.back();
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f6fa;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 顶部导航 */
.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 1px 6px rgba(0,0,0,.06);
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: #f5f6fa;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
  transition: background .2s;
  color: #333;
}
.back-btn:hover { background: #eaeaea; }
.back-btn svg { width: 20px; height: 20px; }

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.ai-avatar-header {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFCC44 0%, #FF8C00 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ai-avatar-header svg { width: 22px; height: 22px; fill: #fff; }

.title-text {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.2;
}
.title-sub {
  display: block;
  font-size: 12px;
  color: #999;
  line-height: 1.4;
}

.session-list-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: #f5f6fa;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
  transition: background .2s;
  color: #333;
}
.session-list-btn:hover { background: #eaeaea; }
.session-list-btn svg { width: 18px; height: 18px; }

/* 消息区 */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 欢迎区 */
.welcome-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 40px 20px 20px;
}
.welcome-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFCC44 0%, #FF8C00 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(255, 140, 0, 0.3);
}
.welcome-icon svg { width: 42px; height: 42px; fill: #fff; }
.welcome-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}
.welcome-desc {
  font-size: 14px;
  color: #888;
  line-height: 1.6;
  max-width: 280px;
  margin-bottom: 24px;
}
.suggest-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}
.suggest-chip {
  padding: 8px 16px;
  border: 1.5px solid #FFCC44;
  border-radius: 20px;
  background: #fff;
  color: #c87800;
  font-size: 13px;
  cursor: pointer;
  transition: all .2s;
  font-family: inherit;
}
.suggest-chip:hover {
  background: #FFCC44;
  color: #fff;
}

/* 消息行 */
.msg-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}
.msg-row.user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFCC44 0%, #FF8C00 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.msg-avatar svg { width: 20px; height: 20px; fill: #fff; }

.msg-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}
.msg-row.assistant .msg-bubble {
  background: #fff;
  color: #1a1a1a;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
  white-space: normal;
}
.msg-row.user .msg-bubble {
  background: linear-gradient(135deg, #FFCC44 0%, #FF9800 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

/* Markdown 样式 */
.markdown-body {
  font-size: 14px;
  line-height: 1.6;
  color: #1a1a1a;
}
.markdown-body h1, .markdown-body h2, .markdown-body h3,
.markdown-body h4, .markdown-body h5, .markdown-body h6 {
  margin: 12px 0 6px;
  font-weight: 600;
  line-height: 1.25;
}
.markdown-body h1:first-child, .markdown-body h2:first-child,
.markdown-body h3:first-child { margin-top: 0; }
.markdown-body h1 { font-size: 1.5em; border-bottom: 1px solid #eee; padding-bottom: 6px; }
.markdown-body h2 { font-size: 1.3em; border-bottom: 1px solid #eee; padding-bottom: 4px; }
.markdown-body h3 { font-size: 1.1em; }
.markdown-body p { margin: 6px 0; }
.markdown-body p:first-child { margin-top: 0; }
.markdown-body p:last-child { margin-bottom: 0; }
.markdown-body ul, .markdown-body ol { margin: 6px 0; padding-left: 22px; }
.markdown-body li { margin: 3px 0; }
.markdown-body pre {
  background: #282c34;
  border-radius: 6px;
  padding: 12px;
  margin: 10px 0;
  overflow-x: auto;
}
.markdown-body pre code {
  background: transparent;
  padding: 0;
  color: #abb2bf;
  font-size: 13px;
  line-height: 1.5;
}
.markdown-body code {
  background: #f6f8fa;
  padding: 2px 5px;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.88em;
  color: #c0392b;
}
.markdown-body blockquote {
  margin: 10px 0;
  padding: 6px 12px;
  border-left: 4px solid #FFCC44;
  background: #fffbf0;
  color: #666;
}
.markdown-body blockquote p { margin: 0; }
.markdown-body table {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
}
.markdown-body th, .markdown-body td {
  border: 1px solid #ddd;
  padding: 6px 10px;
  text-align: left;
}
.markdown-body th { background: #f6f8fa; font-weight: 600; }
.markdown-body tr:nth-child(even) { background: #f9f9f9; }
.markdown-body a { color: #FF8C00; text-decoration: none; }
.markdown-body a:hover { text-decoration: underline; }
.markdown-body hr { border: none; border-top: 1px solid #eee; margin: 12px 0; }
.markdown-body img { max-width: 100%; height: auto; border-radius: 4px; }
.markdown-body strong { font-weight: 600; }
.markdown-body em { font-style: italic; }
.markdown-body del { text-decoration: line-through; color: #999; }

/* ===== 商品卡片组件样式（AI 输出的 HTML 商品展示块）===== */
/* 注意：scoped 下 v-html 内的元素无法匹配 scoped 选择器，需用 :deep() */
:deep(.product-card-list) {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 8px 0;
}

:deep(.product-card) {
  display: flex;
  align-items: stretch;
  background: #fff;
  border: 1.5px solid #f0e6c8;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(255, 152, 0, 0.08);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  max-width: 100%;
}
:deep(.product-card:hover) {
  box-shadow: 0 4px 18px rgba(255, 152, 0, 0.18);
  transform: translateY(-1px);
}

:deep(.pc-img-wrap) {
  width: 90px;
  min-width: 90px;
  height: 90px;
  overflow: hidden;
  background: #fff8ee;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
:deep(.pc-img-wrap img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
:deep(.pc-img-placeholder) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  background: linear-gradient(135deg, #fff8ee 0%, #fff3d6 100%);
}

:deep(.pc-info) {
  flex: 1;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

:deep(.pc-name) {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.pc-tag-row) {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 6px;
}
:deep(.pc-tag) {
  display: inline-block;
  padding: 2px 7px;
  background: #fff3d6;
  color: #c87800;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  line-height: 1.5;
}

:deep(.pc-desc) {
  font-size: 12px;
  color: #888;
  line-height: 1.5;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

:deep(.pc-bottom) {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

:deep(.pc-price) {
  font-size: 16px;
  font-weight: 700;
  color: #FF6B00;
  line-height: 1;
}
:deep(.pc-price .pc-unit) {
  font-size: 12px;
  font-weight: 500;
}

:deep(.pc-add-cart) {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #FFCC44 0%, #FF9800 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.15s;
  outline: none;
  font-family: inherit;
}
:deep(.pc-add-cart:active) {
  opacity: 0.82;
  transform: scale(0.96);
}

/* 商品列表标题说明文字 */
:deep(.product-recommend-title) {
  font-size: 13px;
  color: #888;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}
:deep(.product-recommend-title::before) {
  content: '';
  display: inline-block;
  width: 3px;
  height: 13px;
  background: linear-gradient(180deg, #FFCC44, #FF9800);
  border-radius: 2px;
}

/* 打字动画 */
.typing-dots {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}
.typing-dots span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #ccc;
  animation: bounce 1.2s infinite ease-in-out;
}
.typing-dots span:nth-child(1) { animation-delay: 0s; }
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.7); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* 底部输入区 */
.chat-footer {
  background: #fff;
  border-top: 1px solid #eee;
  padding: 10px 16px;
  padding-bottom: calc(10px + env(safe-area-inset-bottom));
}
.input-wrap {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: #f5f6fa;
  border-radius: 24px;
  padding: 8px 8px 8px 16px;
  border: 1.5px solid #eee;
  transition: border-color .2s;
}
.input-wrap:focus-within {
  border-color: #FFCC44;
}
.chat-input {
  flex: 1;
  border: none;
  background: transparent;
  resize: none;
  font-size: 15px;
  color: #1a1a1a;
  line-height: 1.5;
  max-height: 120px;
  outline: none;
  font-family: inherit;
  padding: 2px 0;
}
.chat-input::placeholder { color: #bbb; }
.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #e0e0e0;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: background .2s, transform .15s;
}
.send-btn svg { width: 18px; height: 18px; }
.send-btn.active {
  background: linear-gradient(135deg, #FFCC44 0%, #FF9800 100%);
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.4);
}
.send-btn.active:hover { transform: scale(1.08); }
.send-btn:disabled { cursor: not-allowed; }

/* 会话侧边栏 */
.session-drawer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  pointer-events: none;
}
.session-drawer.show {
  pointer-events: auto;
}
.session-drawer.show .drawer-overlay {
  opacity: 1;
}
.session-drawer.show .drawer-content {
  transform: translateX(0);
}
.drawer-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  opacity: 0;
  transition: opacity .3s;
}
.drawer-content {
  position: absolute;
  top: 0; right: 0; bottom: 0;
  width: 75%;
  max-width: 280px;
  background: #fff;
  transform: translateX(100%);
  transition: transform .3s;
  display: flex;
  flex-direction: column;
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}
.drawer-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}
.close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f6fa;
  border-radius: 50%;
  cursor: pointer;
  color: #666;
  transition: background .2s;
}
.close-btn:hover { background: #eaeaea; }
.close-btn svg { width: 16px; height: 16px; }
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
  background: linear-gradient(135deg, #FFCC44 0%, #FF9800 100%);
  color: #fff;
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 12px;
  transition: opacity .2s;
  font-size: 14px;
  font-weight: 500;
}
.new-session-btn:active { opacity: 0.85; }
.new-session-btn svg { width: 18px; height: 18px; stroke: #fff; }
.session-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 10px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all .2s;
  border: 1.5px solid transparent;
}
.session-item:active { background: #e9ecef; }
.session-item.active {
  background: #fffbf0;
  border-color: #FFCC44;
}
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
</style>
  
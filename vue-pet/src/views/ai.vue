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
        <div class="ai-avatar">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2zM7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16 1.5 1.5 0 0 0 9 14.5 1.5 1.5 0 0 0 7.5 13zm9 0A1.5 1.5 0 0 0 15 14.5a1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 16.5 13z"/>
          </svg>
        </div>
        <div>
          <span class="title-text">AI 宠物助手</span>
          <span class="title-sub">随时为您解答</span>
        </div>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="chat-body" ref="chatBody">
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

      <div v-for="(msg, index) in messages" :key="index" class="msg-row" :class="msg.role">
        <div v-if="msg.role === 'assistant'" class="msg-avatar">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2zM7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16 1.5 1.5 0 0 0 9 14.5 1.5 1.5 0 0 0 7.5 13zm9 0A1.5 1.5 0 0 0 15 14.5a1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 16.5 13z"/>
          </svg>
        </div>
        <div class="msg-bubble">
          <div v-if="msg.role === 'assistant' && msg.loading" class="typing-dots">
            <span></span><span></span><span></span>
          </div>
          <template v-else>{{ msg.content }}</template>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-footer">
      <div class="input-wrap">
        <textarea
          v-model="inputText"
          class="chat-input"
          placeholder="输入您的问题..."
          rows="1"
          @keydown.enter.exact.prevent="sendMessage"
          @input="autoResize"
          ref="inputRef"
        ></textarea>
        <button
          class="send-btn"
          :class="{ active: inputText.trim() }"
          :disabled="loading || !inputText.trim()"
          @click="sendMessage"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createChatSession, getSessionHistory, sendMessage as apiSendMessage } from '@/api/chat'

const router = useRouter()
const chatBody = ref(null)
const inputRef = ref(null)
const inputText = ref('')
const loading = ref(false)
const historyLoading = ref(false)
const messages = ref([])
const sessionId = ref('')

const suggestions = [
  '狗狗每天需要喂几次？',
  '猫咪感冒了怎么办？',
  '宠物疫苗怎么安排？',
  '如何给宠物洗澡？',
]

function goBack() {
  router.back()
}

function useSuggestion(text) {
  inputText.value = text
  sendMessage()
}

// 初始化会话：先创建会话，再加载历史记录
async function initSession() {
  try {
    const res = await createChatSession()
    // 响应拦截器已解包为 { code, data, msg }，data 即 sessionId 字符串
    sessionId.value = res.data
    if (sessionId.value) {
      await loadHistory()
    }
  } catch (e) {
    console.error('创建会话失败', e)
  }
}

// 加载历史聊天记录
async function loadHistory() {
  historyLoading.value = true
  try {
    const res = await getSessionHistory(sessionId.value)
    // res.data 是 List<ChatHistoryVO>，每条有 role、content 字段
    const history = res.data
    if (Array.isArray(history) && history.length > 0) {
      messages.value = history.map(item => ({
        role: item.role,       // 'user' 或 'assistant'
        content: item.content,
        loading: false,
      }))
      scrollToBottom()
    }
  } catch (e) {
    console.error('加载历史记录失败', e)
  } finally {
    historyLoading.value = false
  }
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text, loading: false })
  inputText.value = ''
  resetInputHeight()
  scrollToBottom()

  loading.value = true
  const loadingMsg = { role: 'assistant', content: '', loading: true }
  messages.value.push(loadingMsg)
  scrollToBottom()

  try {
    // 后端 @RequestParam，chat.js 用 params 传递，正确
    const res = await apiSendMessage({ message: text, sessionId: sessionId.value })
    const idx = messages.value.indexOf(loadingMsg)
    if (idx !== -1) {
      messages.value[idx] = {
        role: 'assistant',
        // res.data 是 AI 回复的字符串
        content: res.data || '收到您的问题，正在处理...',
        loading: false,
      }
    }
  } catch (e) {
    const idx = messages.value.indexOf(loadingMsg)
    if (idx !== -1) {
      messages.value[idx] = {
        role: 'assistant',
        content: '抱歉，服务暂时不可用，请稍后重试。',
        loading: false,
      }
    }
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

function autoResize() {
  const el = inputRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

function resetInputHeight() {
  const el = inputRef.value
  if (!el) return
  el.style.height = 'auto'
}

onMounted(() => {
  initSession()
  inputRef.value?.focus()
})
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

/* 顶部 */
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
}

.ai-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFCC44 0%, #FF8C00 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ai-avatar svg { width: 22px; height: 22px; fill: #fff; }

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
}
.msg-row.user .msg-bubble {
  background: linear-gradient(135deg, #FFCC44 0%, #FF9800 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
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
</style>
  
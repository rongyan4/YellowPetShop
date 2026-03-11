<template>
  <!-- 遮罩层 -->
  <transition name="fade">
    <div class="overlay" v-if="show" @click="handleSkip"></div>
  </transition>

  <!-- 底部弹出的登录选择框 -->
  <transition name="slide-up">
    <div class="login-modal" v-if="show">
      <!-- 关闭按钮 -->
      <div class="close-btn" @click="handleSkip">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M18 6L6 18M6 6L18 18" stroke="#999" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>

      <!-- Logo图标 -->
      <div class="logo-container">
        <div class="logo-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
            <!-- 标签图标 -->
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="rgba(255,255,255,0.3)"/>
            <line x1="7" y1="7" x2="7.01" y2="7" stroke="#333" stroke-width="2.5" stroke-linecap="round"/>
          </svg>
        </div>
      </div>

      <!-- 标题文字 -->
      <h2 class="title">登录宠物商城</h2>
      <p class="subtitle">立即登录，享受更多会员权益</p>

      <!-- 登录按钮 -->
      <button class="login-btn" @click="showLoginModal">
        登录
      </button>

      <!-- 注册链接 -->
      <div class="skip-link" @click="showRegisterModal">
        注册
      </div>

      <!-- 协议勾选 -->
      <div class="agreement">
        <div class="checkbox" :class="{ checked: agreed }" @click="agreed = !agreed">
          <svg v-if="agreed" width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M2 7L5.5 10.5L12 4" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="agreement-text">
          已阅读并同意
          <span class="link">《会员须知》</span>
          <span class="link">《隐私协议》</span>
        </span>
      </div>
    </div>
  </transition>

  <!-- 登录弹窗 -->
  <Login v-if="isLoginVisible" @close="closeLogin" />
  
  <!-- 注册弹窗 -->
  <Register v-if="isRegisterVisible" @close="closeRegister" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Login from './Login.vue'
import Register from './Register.vue'

// 定义 emits
const emit = defineEmits(['close'])

// 响应式数据
const show = ref(false)
const isLoginVisible = ref(false)
const isRegisterVisible = ref(false)
const agreed = ref(true)

// 生命周期
onMounted(() => {
  setTimeout(() => {
    show.value = true
  }, 100)
})

// 方法
const handleSkip = () => {
  show.value = false
  setTimeout(() => {
    emit('close')
  }, 300)
}

const showLoginModal = () => {
  if (!agreed.value) {
    // 可以添加提示：请先同意协议
    return
  }
  isLoginVisible.value = true
}

const showRegisterModal = () => {
  if (!agreed.value) {
    // 可以添加提示：请先同意协议
    return
  }
  isRegisterVisible.value = true
}

const closeLogin = () => {
  isLoginVisible.value = false
  // 不关闭 LoginChoice，只关闭 Login 弹窗
}

const closeRegister = () => {
  isRegisterVisible.value = false
  // 不关闭 LoginChoice，只关闭 Register 弹窗
}
</script>

<style scoped lang="scss">
/* 遮罩层 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 1.6rem; // 不覆盖底部栏
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

/* 登录弹窗 */
.login-modal {
  position: fixed;
  bottom: 1.6rem; // 在底部栏上方
  left: 0;
  right: 0;
  height: 10rem;
  background: #ffffff;
  border-radius: 16px 16px 0 0;
  padding: 0.4rem 0.6rem 0.5rem;
  z-index: 1000;
  box-shadow: 0 -2px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

/* 关闭按钮 */
.close-btn {
  position: absolute;
  top: 0.3rem;
  right: 0.3rem;
  cursor: pointer;
  padding: 0.08rem;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 0.44rem;
    height: 0.44rem;
  }
  
  &:active {
    opacity: 0.6;
  }
}

/* Logo容器 */
.logo-container {
  display: flex;
  justify-content: center;
  margin: 0.8rem 0 0.5rem;
}

.logo-icon {
  width: 1.8rem;
  height: 1.8rem;
  background: linear-gradient(135deg, #FFE033 0%, #FFD700 100%);
  border-radius: 0.32rem;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0.12rem 0.36rem rgba(255, 215, 0, 0.4);
  
  svg {
    width: 1.2rem;
    height: 1.2rem;
  }
}

/* 标题 */
.title {
  font-size: 0.42rem;
  font-weight: 500;
  color: #333333;
  text-align: center;
  margin: 0 0 0.2rem;
  letter-spacing: 0.01rem;
}

.subtitle {
  font-size: 0.26rem;
  color: #999999;
  text-align: center;
  margin: 0 0 0.8rem;
  line-height: 1.4;
}

/* 登录按钮 */
.login-btn {
  width: 80%;
  margin: 0 auto;
  height: 0.9rem;
  background: linear-gradient(135deg, #FFE033 0%, #FFD700 100%);
  border: none;
  border-radius: 0.45rem;
  color: #333333;
  font-size: 0.32rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0.06rem 0.18rem rgba(255, 215, 0, 0.4);
  flex-shrink: 0;
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 0.03rem 0.12rem rgba(255, 215, 0, 0.4);
  }
}

/* 注册链接 */
.skip-link {
  text-align: center;
  color: #666666;
  font-size: 0.28rem;
  padding: 0.4rem 0;
  cursor: pointer;
  flex-shrink: 0;
  
  &:active {
    color: #333333;
  }
}

/* 协议勾选 */
.agreement {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.12rem;
  padding: 0.3rem 0 0;
  flex-shrink: 0;
}

.checkbox {
  width: 0.32rem;
  height: 0.32rem;
  border: 0.04rem solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s ease;
  
  &.checked {
    background: #FFD700;
    border-color: #FFD700;
  }
  
  &:active {
    transform: scale(0.9);
  }
  
  svg {
    width: 0.24rem;
    height: 0.24rem;
  }
}

.agreement-text {
  font-size: 0.22rem;
  color: #999999;
  line-height: 1.4;
  
  .link {
    color: #666666;
    text-decoration: underline;
    cursor: pointer;
    
    &:active {
      color: #333333;
    }
  }
}

/* 动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from {
  transform: translateY(100%);
}

.slide-up-leave-to {
  transform: translateY(100%);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

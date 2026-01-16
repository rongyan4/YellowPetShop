<template>
  <div class="login-page">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      title="授权登录"
      left-arrow
      @click-left="goBack"
    >
      <template #right>
        <van-icon name="ellipsis" size="20" />
      </template>
    </van-nav-bar>

    <!-- 主内容区 -->
    <div class="content">
      <!-- 插图 -->
      <div class="illustration">
        <div class="illustration-placeholder">
          <div class="illustration-icon">👤</div>
          <div class="illustration-text">Good me</div>
        </div>
      </div>

      <!-- 标语 -->
      <div class="slogan">立即登录，享受更多福利～</div>

      <!-- 按钮组 -->
      <div class="button-group">
        <van-button 
          type="primary" 
          block 
          round 
          size="large"
          @click="showLoginModal"
          color="#2c3e50"
        >
          登录
        </van-button>
        <van-button 
          block 
          round 
          size="large"
          @click="showRegisterModal"
          plain
        >
          注册
        </van-button>
      </div>

      <!-- 暂不登录 -->
      <div class="skip-login" @click="skipLogin">暂不登录</div>
    </div>

    <!-- 登录弹窗 -->
    <Login v-if="isLoginModalVisible" @close="closeLoginModal" />

    <!-- 注册弹窗 -->
    <Register v-if="isRegisterModalVisible" @close="closeRegisterModal" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Login from '@/components/login/Login.vue'
import Register from '@/components/login/Register.vue'

const router = useRouter()

// 响应式数据
const isLoginModalVisible = ref(false)
const isRegisterModalVisible = ref(false)

// 方法
const goBack = () => {
  router.back()
}

const showLoginModal = () => {
  isLoginModalVisible.value = true
}

const showRegisterModal = () => {
  isRegisterModalVisible.value = true
}

const closeLoginModal = () => {
  isLoginModalVisible.value = false
}

const closeRegisterModal = () => {
  isRegisterModalVisible.value = false
}

const skipLogin = () => {
  router.push('/home')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
}

/* 主内容区 */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 30px;
}

.illustration {
  width: 280px;
  height: 280px;
  margin-bottom: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.illustration-icon {
  font-size: 120px;
  margin-bottom: 20px;
  opacity: 0.8;
}

.illustration-text {
  font-size: 24px;
  color: #999;
  font-weight: 300;
  letter-spacing: 2px;
}

.slogan {
  font-size: 20px;
  color: #333;
  margin-bottom: 60px;
  text-align: center;
  font-weight: 400;
}

/* 按钮组 */
.button-group {
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
  position: relative;
  z-index: 1;
}

.skip-login {
  color: #999;
  font-size: 14px;
  margin-bottom: 40px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.skip-login:hover {
  color: #666;
}
</style>

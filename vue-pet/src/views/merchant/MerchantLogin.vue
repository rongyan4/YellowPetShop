<template>
  <div class="merchant-login">
    <!-- 左侧大图 -->
    <div class="login-image">
      <div class="image-overlay">
        <h1 class="brand-title">大黄宠物商城</h1>
        <p class="brand-subtitle">商家管理系统</p>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-area">
      <div class="login-box">
        <div class="login-header">
          <h2 class="login-title">商家登录</h2>
          <p class="login-desc">欢迎回来，请登录您的账号</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label class="form-label">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              账号
            </label>
            <input
              v-model="username"
              type="text"
              class="form-input"
              placeholder="请输入商家账号"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
              密码
            </label>
            <div class="password-wrapper">
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="请输入密码"
                required
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
              >
                <svg v-if="showPassword" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
                <svg v-else class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                  <line x1="1" y1="1" x2="23" y2="23"></line>
                </svg>
              </button>
            </div>
          </div>

          <button type="submit" class="login-button" :disabled="loading">
            <span v-if="!loading">登录</span>
            <span v-else class="loading-text">
              <svg class="spinner" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" opacity="0.25"></circle>
                <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round"></path>
              </svg>
              登录中...
            </span>
          </button>
        </form>

        <div class="login-footer">
          <p class="footer-text">默认账号：admin / 密码：admin123</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { merchantLogin, getMerchantInfo } from '@/api/merchant';
import { useMerchantStore } from '@/stores/merchant';
import { showToast, showSuccessToast, showFailToast } from 'vant';

const router = useRouter();
const merchantStore = useMerchantStore();

const username = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);

const handleLogin = async () => {
  if (!username.value || !password.value) {
    showToast('请填写完整信息');
    return;
  }

  loading.value = true;

  try {
    const response = await merchantLogin({
      username: username.value,
      password: password.value
    });

    if (response && response.code === 200) {
      // token 已由后端写入 HttpOnly Cookie，前端只需获取商家信息
      const infoRes = await getMerchantInfo();
      const merchantInfo = infoRes?.data || { username: username.value };

      merchantStore.login({ merchantInfo });

      showSuccessToast('登录成功！');
      
      setTimeout(() => {
        router.push('/merchant/dashboard');
      }, 1000);
    } else {
      showFailToast(response.msg || '登录失败');
    }
  } catch (error) {
    console.error('登录错误：', error);
    showFailToast(error.message || '登录失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.merchant-login {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 左侧大图区域 */
.login-image {
  flex: 0 0 65%;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  position: relative;
  overflow: hidden;
}

.login-image::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(30px, 30px) rotate(5deg); }
}

.image-overlay {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  padding: 60px;
}

.brand-title {
  font-size: 72px;
  font-weight: 800;
  margin: 0 0 20px 0;
  letter-spacing: -2px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  font-family: 'Courier New', monospace;
}

.brand-subtitle {
  font-size: 28px;
  font-weight: 300;
  margin: 0;
  opacity: 0.95;
  letter-spacing: 8px;
}

/* 右侧登录区域 */
.login-area {
  flex: 0 0 35%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 40px;
}

.login-box {
  width: 100%;
  max-width: 420px;
}

.login-header {
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3436;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.login-desc {
  font-size: 15px;
  color: #636e72;
  margin: 0;
}

/* 表单样式 */
.login-form {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
  margin-bottom: 10px;
}

.form-label .icon {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  stroke-width: 2.5;
  color: #98D8C8;
}

.form-input {
  width: 100%;
  height: 50px;
  padding: 0 16px;
  font-size: 15px;
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  outline: none;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.form-input:focus {
  border-color: #98D8C8;
  background: white;
  box-shadow: 0 0 0 4px rgba(152, 216, 200, 0.1);
}

.password-wrapper {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #636e72;
  transition: color 0.2s;
}

.password-toggle:hover {
  color: #98D8C8;
}

.password-toggle .icon {
  width: 20px;
  height: 20px;
  stroke-width: 2;
}

.login-button {
  width: 100%;
  height: 54px;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 32px;
  box-shadow: 0 4px 15px rgba(152, 216, 200, 0.3);
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(152, 216, 200, 0.4);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.spinner {
  width: 18px;
  height: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e1e8ed;
}

.footer-text {
  font-size: 13px;
  color: #95a5a6;
  margin: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .login-image {
    flex: 0 0 55%;
  }
  .login-area {
    flex: 0 0 45%;
  }
  .brand-title {
    font-size: 56px;
  }
}
</style>

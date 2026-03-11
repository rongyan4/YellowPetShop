<template>
  <div class="modal-wrapper">
    <transition name="van-slide-up">
      <div class="modal-content" v-if="show">
        <!-- 标题 -->
        <div class="modal-header">
          <h2 class="modal-title">登录账号</h2>
          <van-icon name="cross" size="20" @click="closeModal" class="close-icon" />
        </div>

        <!-- 表单 -->
        <van-form @submit="handleLogin">
          <!-- 用户名输入框 -->
          <van-cell-group inset>
            <van-field
              v-model="username"
              name="username"
              label="用户名"
              placeholder="请输入用户名"
              :rules="[{ required: true, message: '请填写用户名' }]"
            />
            
            <!-- 密码输入框 -->
            <van-field
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              name="password"
              label="密码"
              placeholder="请输入密码"
              :rules="[{ required: true, message: '请填写密码' }]"
              :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
              @click-right-icon="togglePassword"
            />
          </van-cell-group>

          <!-- 登录按钮 -->
          <div class="submit-wrapper">
            <van-button 
              round 
              block 
              type="primary" 
              native-type="submit"
              color="#FFD700"
              size="large"
              style="color: #333; font-weight: 600;"
            >
              确定
            </van-button>
          </div>
        </van-form>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showSuccessToast, showFailToast, showLoadingToast, closeToast } from 'vant'
import { login } from '@/api/user'
import { parseJWT } from '@/utils/auth'

const router = useRouter()
const userStore = useUserStore()

// 定义 emits
const emit = defineEmits(['close'])

// 响应式数据
const show = ref(false)
const username = ref('')
const password = ref('')
const showPassword = ref(false)

// 生命周期
onMounted(() => {
  // 延迟显示以触发动画
  setTimeout(() => {
    show.value = true
  }, 10)
})

// 方法
const closeModal = () => {
  show.value = false
  setTimeout(() => {
    emit('close')
  }, 300)
}

const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    showToast('请填写完整信息')
    return
  }
  
  // 显示加载提示
  showLoadingToast({
    message: '登录中...',
    forbidClick: true,
    duration: 0
  })
  
  try {
    // 调用登录接口
    const response = await login({
      username: username.value,
      password: password.value
    })
    
    closeToast()
    
    if (response && response.code === 200) {
      // 后端返回的是token
      const token = response.data
      
      // 解析token获取用户信息
      const payload = parseJWT(token)
      const userInfo = {
        id: payload?.userId || payload?.sub,
        username: payload?.username || username.value,
      }
      
      // 保存token和用户信息到Pinia store（会自动同步到localStorage）
      userStore.login({
        token: token,
        userInfo: userInfo
      })
      
      console.log('登录成功，Token已保存:', token)
      console.log('用户信息:', userInfo)
      
      showSuccessToast('登录成功！')
      closeModal()
      
      // 重载当前页面
      setTimeout(() => {
        window.location.reload()
      }, 1000)
    } else {
      showFailToast(response.msg || '登录失败')
    }
  } catch (error) {
    closeToast()
    console.error('登录错误：', error)
    showFailToast(error.message || '登录失败，请稍后重试')
  }
}
</script>

<style scoped>
.modal-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 1001;
  pointer-events: none;
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 30px 20px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  pointer-events: auto;
}

.modal-header {
  position: relative;
  margin-bottom: 30px;
}

.modal-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin: 0;
}

.close-icon {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #999;
}

.submit-wrapper {
  margin-top: 30px;
  padding: 0 16px;
}

/* Vant 组件样式覆盖 */
:deep(.van-cell-group) {
  margin: 0;
}

:deep(.van-cell) {
  padding: 16px;
}

:deep(.van-field__label) {
  width: 70px;
  color: #646566;
}
</style>

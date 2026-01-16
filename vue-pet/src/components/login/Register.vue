<template>
  <van-overlay :show="true" @click="closeModal">
    <div class="modal-wrapper" @click.stop>
      <transition name="van-slide-up">
        <div class="modal-content" v-if="show">
          <!-- 标题 -->
          <div class="modal-header">
            <h2 class="modal-title">注册账号</h2>
            <van-icon name="cross" size="20" @click="closeModal" class="close-icon" />
          </div>

          <!-- 表单 -->
          <van-form @submit="handleRegister">
            <!-- 邮箱输入框 -->
            <van-cell-group inset>
              <van-field
                v-model="email"
                name="email"
                label="邮箱"
                placeholder="请输入邮箱"
                type="email"
                :rules="[
                  { required: true, message: '请填写邮箱' },
                  { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '请输入正确的邮箱格式' }
                ]"
              />
              
              <!-- 用户名输入框 -->
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
                @input="checkPasswordStrength"
              />
              
              <!-- 确认密码输入框 -->
              <van-field
                v-model="confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                name="confirmPassword"
                label="确认密码"
                placeholder="请确认密码"
                :rules="[{ required: true, message: '请确认密码' }]"
                :right-icon="showConfirmPassword ? 'eye-o' : 'closed-eye'"
                @click-right-icon="toggleConfirmPassword"
              />
            </van-cell-group>

            <!-- 密码强度指示器 -->
            <div v-if="password" class="password-strength">
              <div class="strength-info">
                <span class="strength-label">密码强度：</span>
                <span class="strength-text" :class="strengthClass">{{ strengthText }}</span>
              </div>
              <van-progress 
                :percentage="strengthPercentage" 
                :color="strengthColor"
                :show-pivot="false"
                stroke-width="6"
              />
              
              <!-- 密码要求提示 -->
              <div class="password-requirements">
                <div class="requirement-item" :class="{ completed: isLengthValid }">
                  <van-icon :name="isLengthValid ? 'success' : 'cross'" :color="isLengthValid ? '#26de81' : '#ff4757'" size="14" />
                  <span class="requirement-text">密码长度至少8位</span>
                </div>
                <div class="requirement-item" :class="{ completed: hasLetterAndNumber }">
                  <van-icon :name="hasLetterAndNumber ? 'success' : 'cross'" :color="hasLetterAndNumber ? '#26de81' : '#ff4757'" size="14" />
                  <span class="requirement-text">必须包含英文和数字</span>
                </div>
              </div>
            </div>

            <!-- 注册按钮 -->
            <div class="submit-wrapper">
              <van-button 
                round 
                block 
                type="primary" 
                native-type="submit"
                :disabled="!canRegister"
                :color="canRegister ? '#2c3e50' : '#bbb'"
                size="large"
              >
                注册
              </van-button>
            </div>
          </van-form>
        </div>
      </transition>
    </div>
  </van-overlay>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast, showSuccessToast, showFailToast, showLoadingToast, closeToast } from 'vant'
import { register } from '@/api/user'

// 定义 emits
const emit = defineEmits(['close'])

// 响应式数据
const show = ref(false)
const email = ref('')
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const passwordStrength = ref(0) // 0: 弱, 1: 中, 2: 强

// 计算属性
const strengthClass = computed(() => {
  const classes = ['weak', 'medium', 'strong']
  return classes[passwordStrength.value]
})

const strengthText = computed(() => {
  const texts = ['弱', '中', '强']
  return texts[passwordStrength.value]
})

const strengthPercentage = computed(() => {
  const percentages = [33.33, 66.66, 100]
  return percentages[passwordStrength.value]
})

const strengthColor = computed(() => {
  const colors = ['#ff4757', '#ffa502', '#26de81']
  return colors[passwordStrength.value]
})

// 检查密码长度是否有效
const isLengthValid = computed(() => {
  return password.value.length >= 8
})

// 检查是否同时包含英文和数字
const hasLetterAndNumber = computed(() => {
  const hasLetter = /[a-zA-Z]/.test(password.value)
  const hasNumber = /[0-9]/.test(password.value)
  return hasLetter && hasNumber
})

const canRegister = computed(() => {
  // 密码强度不能为弱，且两次密码必须一致，邮箱格式正确
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return passwordStrength.value > 0 && 
         password.value === confirmPassword.value &&
         email.value.trim() !== '' &&
         emailPattern.test(email.value) &&
         username.value.trim() !== '' &&
         password.value !== '' &&
         confirmPassword.value !== ''
})

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

const toggleConfirmPassword = () => {
  showConfirmPassword.value = !showConfirmPassword.value
}

const checkPasswordStrength = () => {
  const pwd = password.value
  
  // 检查是否包含字母
  const hasLetter = /[a-zA-Z]/.test(pwd)
  // 检查是否包含数字
  const hasNumber = /[0-9]/.test(pwd)
  // 检查是否包含特殊字符
  const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(pwd)
  
  // 密码长度小于8 或 (只含英文或数字) -> 弱
  if (pwd.length < 8 || 
      (hasLetter && !hasNumber && !hasSpecial) || 
      (!hasLetter && hasNumber && !hasSpecial)) {
    passwordStrength.value = 0 // 弱
  }
  // 包含字母、数字和特殊字符 -> 强
  else if (hasLetter && hasNumber && hasSpecial) {
    passwordStrength.value = 2 // 强
  }
  // 其他情况 -> 中
  else {
    passwordStrength.value = 1 // 中
  }
}

const handleRegister = async () => {
  if (!canRegister.value) {
    return
  }
  
  if (password.value !== confirmPassword.value) {
    showToast('两次输入的密码不一致')
    return
  }
  
  // 显示加载提示
  showLoadingToast({
    message: '注册中...',
    forbidClick: true,
    duration: 0
  })
  
  try {
    // 调用注册接口
    const response = await register({
      email: email.value,
      username: username.value,
      password: password.value,
      nickname: username.value, // 默认使用用户名作为昵称
    })
    
    closeToast()
    
    if (response && response.code === 200) {
      showSuccessToast('注册成功！请登录')
      closeModal()
    } else {
      showFailToast(response.msg || '注册失败')
    }
  } catch (error) {
    closeToast()
    console.error('注册错误：', error)
    showFailToast(error.message || '注册失败，请稍后重试')
  }
}
</script>

<style scoped>
.modal-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
  position: relative;
  z-index: 2001;
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 30px 20px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  max-height: 90vh;
  overflow-y: auto;
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

/* 密码强度指示器 */
.password-strength {
  margin: 20px 16px 10px;
}

.strength-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.strength-label {
  font-size: 14px;
  color: #646566;
  margin-right: 8px;
}

.strength-text {
  font-size: 14px;
  font-weight: 500;
}

.strength-text.weak {
  color: #ff4757;
}

.strength-text.medium {
  color: #ffa502;
}

.strength-text.strong {
  color: #26de81;
}

/* 密码要求提示 */
.password-requirements {
  margin-top: 15px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
}

.requirement-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.requirement-item:last-child {
  margin-bottom: 0;
}

.requirement-item .van-icon {
  margin-right: 8px;
  flex-shrink: 0;
}

.requirement-text {
  font-size: 13px;
  color: #646566;
  transition: color 0.3s ease;
}

.requirement-item.completed .requirement-text {
  color: #26de81;
  font-weight: 500;
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
  width: 80px;
  color: #646566;
}

:deep(.van-button--disabled) {
  opacity: 0.6;
}
</style>

<template>
  <div class="modal-wrapper" v-if="modelValue" @click.self="closeModal">
    <transition name="van-slide-up">
      <div class="modal-content" v-if="modelValue">
        <!-- 标题 -->
        <div class="modal-header">
          <h2 class="modal-title">编辑昵称</h2>
          <van-icon name="cross" size="20" @click="closeModal" class="close-icon" />
        </div>

        <!-- 表单 -->
        <van-form @submit="handleSubmit">
          <!-- 昵称输入框 -->
          <van-cell-group inset>
            <van-field
              v-model="nickname"
              name="nickname"
              label="昵称"
              placeholder="请输入昵称"
              :rules="[{ required: true, message: '请填写昵称' }]"
              maxlength="20"
              show-word-limit
            />
          </van-cell-group>

          <!-- 提交按钮 -->
          <div class="submit-wrapper">
            <van-button 
              round 
              block 
              type="primary" 
              native-type="submit"
              color="#2c3e50"
              size="large"
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
import { ref, watch } from 'vue'
import { showToast, showSuccessToast } from 'vant'

// 定义 props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  initialValue: {
    type: String,
    default: ''
  }
})

// 定义 emits
const emit = defineEmits(['update:modelValue', 'confirm'])

// 响应式数据
const nickname = ref('')

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    nickname.value = props.initialValue || ''
  }
}, { immediate: true })

// 监听 initialValue 变化
watch(() => props.initialValue, (newVal) => {
  if (props.modelValue) {
    nickname.value = newVal || ''
  }
})

// 方法
const closeModal = () => {
  emit('update:modelValue', false)
}

const handleSubmit = () => {
  if (!nickname.value || !nickname.value.trim()) {
    showToast('请填写昵称')
    return
  }
  
  if (nickname.value.trim().length > 20) {
    showToast('昵称不能超过20个字符')
    return
  }
  
  emit('confirm', nickname.value.trim())
  showSuccessToast('保存成功')
  closeModal()
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
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 30px 20px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
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

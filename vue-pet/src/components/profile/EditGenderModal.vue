<template>
  <div class="modal-wrapper" v-if="modelValue" @click.self="closeModal">
    <transition name="van-slide-up">
      <div class="modal-content" v-if="modelValue">
        <!-- 标题 -->
        <div class="modal-header">
          <h2 class="modal-title">选择性别</h2>
          <van-icon name="cross" size="20" @click="closeModal" class="close-icon" />
        </div>

        <!-- 性别选择 -->
        <div class="gender-options">
          <div 
            class="gender-option" 
            :class="{ active: selectedGender === '男' }"
            @click="selectGender('男')"
          >
            <div class="gender-icon">♂</div>
            <div class="gender-text">男</div>
          </div>
          <div 
            class="gender-option" 
            :class="{ active: selectedGender === '女' }"
            @click="selectGender('女')"
          >
            <div class="gender-icon">♀</div>
            <div class="gender-text">女</div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-wrapper">
          <van-button 
            round 
            block 
            type="primary" 
            @click="handleSubmit"
            color="#2c3e50"
            size="large"
          >
            确定
          </van-button>
        </div>
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
const selectedGender = ref('')

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    selectedGender.value = props.initialValue || '男'
  }
}, { immediate: true })

// 监听 initialValue 变化
watch(() => props.initialValue, (newVal) => {
  if (props.modelValue) {
    selectedGender.value = newVal || '男'
  }
})

// 方法
const closeModal = () => {
  emit('update:modelValue', false)
}

const selectGender = (gender) => {
  selectedGender.value = gender
}

const handleSubmit = () => {
  if (!selectedGender.value) {
    showToast('请选择性别')
    return
  }
  
  emit('confirm', selectedGender.value)
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

.gender-options {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin: 30px 0;
}

.gender-option {
  flex: 1;
  max-width: 150px;
  padding: 30px 20px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fafafa;
}

.gender-option:hover {
  border-color: #2c3e50;
}

.gender-option.active {
  border-color: #2c3e50;
  background-color: #f0f4f8;
}

.gender-icon {
  font-size: 48px;
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
}

.gender-option.active .gender-icon {
  color: #2c3e50;
}

.gender-text {
  font-size: 18px;
  color: #666;
  font-weight: 500;
}

.gender-option.active .gender-text {
  color: #2c3e50;
  font-weight: 600;
}

.submit-wrapper {
  margin-top: 30px;
  padding: 0 16px;
}
</style>

<template>
  <div>
    <!-- 主弹窗 -->
    <div class="modal-wrapper" v-if="modelValue" @click.self="closeModal">
      <transition name="van-slide-up">
        <div class="modal-content" v-if="modelValue">
          <!-- 标题 -->
          <div class="modal-header">
            <h2 class="modal-title">选择生日</h2>
            <van-icon name="cross" size="20" @click="closeModal" class="close-icon" />
          </div>

          <!-- 日期选择器 -->
          <div class="date-picker-wrapper">
            <van-cell-group inset>
              <van-field
                v-model="displayDate"
                name="birthday"
                label="生日"
                placeholder="请选择生日"
                readonly
                is-link
                @click="handleFieldClick"
              />
            </van-cell-group>
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
              :disabled="!selectedDate"
            >
              确定
            </van-button>
          </div>
        </div>
      </transition>
    </div>
    
    <!-- 日期选择器弹窗 - 独立在外层，避免 z-index 问题 -->
    <van-popup v-model:show="showDatePicker" position="bottom" :z-index="2000">
      <van-date-picker
        v-model="currentDate"
        title="选择生日"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
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
const showDatePicker = ref(false)
const selectedDate = ref('')
// Vant 4 DatePicker 需要字符串数组格式 ['2000', '01', '01']
const currentDate = ref(['2000', '01', '01'])

// 计算属性
const displayDate = computed(() => {
  return selectedDate.value || '请选择生日'
})

// 日期范围（最小日期：100年前，最大日期：今天）
const minDate = new Date(1924, 0, 1)
const maxDate = new Date()

// 初始化日期数据
const initDateData = () => {
  if (props.initialValue) {
    const dateParts = props.initialValue.split('-')
    if (dateParts.length === 3) {
      // Vant 4 DatePicker 需要字符串数组
      currentDate.value = [
        dateParts[0],
        dateParts[1],
        dateParts[2]
      ]
      selectedDate.value = props.initialValue
      return
    }
  }
  // 默认选择当前日期
  const now = new Date()
  const year = String(now.getFullYear())
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  currentDate.value = [year, month, day]
  selectedDate.value = ''
}

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    initDateData()
  } else {
    showDatePicker.value = false
  }
}, { immediate: true })

// 监听 initialValue 变化
watch(() => props.initialValue, (newVal) => {
  if (props.modelValue && newVal) {
    const dateParts = newVal.split('-')
    if (dateParts.length === 3) {
      // Vant 4 DatePicker 需要字符串数组
      currentDate.value = [
        dateParts[0],
        dateParts[1],
        dateParts[2]
      ]
      selectedDate.value = newVal
    }
  }
})

// 方法
const closeModal = () => {
  showDatePicker.value = false
  emit('update:modelValue', false)
}

const handleFieldClick = () => {
  console.log('点击了生日字段，打开日期选择器')
  console.log('当前日期值:', currentDate.value)
  showDatePicker.value = true
}

const onDateConfirm = () => {
  console.log('确认选择日期:', currentDate.value)
  // Vant 4 DatePicker 确认时，currentDate 已经更新为选中的值
  if (currentDate.value && currentDate.value.length === 3) {
    const [year, month, day] = currentDate.value
    // currentDate 已经是字符串数组，直接拼接
    selectedDate.value = `${year}-${month}-${day}`
  }
  showDatePicker.value = false
}

const handleSubmit = () => {
  if (!selectedDate.value) {
    showToast('请选择生日')
    return
  }
  
  emit('confirm', selectedDate.value)
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

.date-picker-wrapper {
  margin: 20px 0;
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
  cursor: pointer;
}

:deep(.van-field__label) {
  width: 70px;
  color: #646566;
}
</style>

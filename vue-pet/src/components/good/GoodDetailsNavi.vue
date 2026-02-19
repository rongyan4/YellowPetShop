<template>
  <div class="good-details-navi" :style="{ backgroundColor: `rgba(255, 255, 255, ${opacity})` }">
    <div class="navi-tabs">
      <div 
        v-for="(tab, index) in tabs" 
        :key="index"
        class="navi-tab"
        :class="{ active: activeTab === index }"
        @click="handleTabClick(index)"
      >
        {{ tab.label }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';

defineProps({
  opacity: {
    type: Number,
    default: 0
  },
  activeTab: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits(['tab-click']);

const tabs = [
  { label: '宝贝', value: 'goods' },
  { label: '评价', value: 'review' },
  { label: '详情', value: 'detail' }
];

const handleTabClick = (index) => {
  emit('tab-click', index);
};
</script>

<style scoped>
.good-details-navi {
  position: fixed;
  top: 56px;
  left: 0;
  right: 0;
  z-index: 999;
  transition: background-color 0.3s;
  border-bottom: 1px solid rgba(240, 240, 240, 0.5);
}

.navi-tabs {
  display: flex;
  align-items: center;
  height: 44px;
  padding: 0 16px;
}

.navi-tab {
  flex: 1;
  text-align: center;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  position: relative;
  padding: 12px 0;
  transition: color 0.3s;
}

.navi-tab.active {
  color: #333;
  font-weight: 500;
}

.navi-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 2px;
  background-color: #333;
  border-radius: 1px;
}
</style>

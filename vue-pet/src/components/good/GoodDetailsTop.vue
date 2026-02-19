<template>
  <div class="good-details-top" :style="{ backgroundColor: `rgba(255, 255, 255, ${opacity})` }">
    <div class="top-content">
      <!-- 返回按钮 -->
      <div class="back-btn" @click="goBack">
        <van-icon name="arrow-left" size="20" />
      </div>

      <!-- 搜索框 -->
      <div class="search-box" :style="{ opacity: opacity }">
        <van-icon name="search" size="16" color="#999" />
        <span class="search-text">20kg猫粮</span>
      </div>

      <!-- 右侧按钮组 -->
      <div class="right-actions">
        <div class="action-btn">
          <van-icon name="share-o" size="20" />
        </div>
        <div class="action-btn cart-btn">
          <van-icon name="cart-o" size="20" />
          <van-badge content="99+" class="cart-badge" />
        </div>
        <div class="action-btn">
          <van-icon name="star-o" size="20" />
        </div>
        <div class="action-btn">
          <van-icon name="ellipsis" size="20" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

defineProps({
  opacity: {
    type: Number,
    default: 0
  }
});

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.good-details-top {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: background-color 0.3s;
}

.top-content {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  height: 56px;
}

.back-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
}

.search-box {
  flex: 1;
  margin: 0 12px;
  height: 36px;
  background-color: #f5f5f5;
  border-radius: 18px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 8px;
}

.search-text {
  font-size: 14px;
  color: #666;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
}

.cart-btn {
  background-color: rgba(255, 96, 52, 0.9);
}

.cart-badge {
  position: absolute;
  top: -2px;
  right: -2px;
}

:deep(.van-badge) {
  background-color: #ff3b30;
  border: 1px solid #fff;
}
</style>

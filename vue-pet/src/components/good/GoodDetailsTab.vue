<template>
  <div class="good-details-tab">
    <van-goods-action>
      <van-goods-action-icon icon="shop-o" text="店铺" @click="goToShop" />
      <van-goods-action-icon icon="chat-o" text="客服" @click="contactService" />
      <van-goods-action-icon icon="star-o" text="1万+" @click="toggleFavorite" />
      <van-goods-action-button
        type="warning"
        text="秒杀价 ¥ 17.9"
        @click="handleBuy"
      >
        <template #default>
          <div class="buy-button">
            <div class="buy-price">秒杀价 ¥ 17.9</div>
            <div class="buy-subtitle">新客专享 去购买</div>
          </div>
        </template>
      </van-goods-action-button>
    </van-goods-action>
  </div>
</template>

<script setup>
import { showToast } from 'vant';

const goToShop = () => {
  showToast('店铺功能开发中');
};

const contactService = () => {
  showToast('客服功能开发中');
};

const toggleFavorite = () => {
  showToast('收藏成功');
};

const handleBuy = () => {
  showToast('商品仅支持在客户端购买');
};
</script>

<style scoped>
.good-details-tab {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
}

:deep(.van-goods-action) {
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
}

:deep(.van-goods-action-icon) {
  font-size: 11px;
  color: #666;
}

:deep(.van-goods-action-button) {
  background: linear-gradient(to right, #ff6034, #ff8f34);
  border: none;
  flex: 2;
}

.buy-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 4px 0;
}

.buy-price {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  line-height: 1.2;
}

.buy-subtitle {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 2px;
}
</style>

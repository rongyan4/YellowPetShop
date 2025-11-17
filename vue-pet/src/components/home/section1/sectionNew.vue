<template>
  <div class="section-new">
    <!-- Swipe 轮播图，高度与图片一致 -->
    <div class="swipe-container">
      <swipe :autoplay="3000" lazy-render>
        <swipe-item v-for="(image, index) in SwipeImages" :key="index">
          <img class="swipe-image" :src="image" alt="轮播图" @load="onImageLoad" />
        </swipe-item>
      </swipe>
      <!-- 用户信息条，中部对齐到 swipe 底部 -->
      <div class="user-info-wrapper">
        <UserInfoBar :userInfo="userInfo" />
      </div>
    </div>

    <!-- 服务选择按钮 -->
    <div class="service-buttons-wrapper">
      <ServiceButtons @service-click="handleServiceClick" />
    </div>

    <!-- 功能按钮 -->
    <FeatureButtons :features="features" @feature-click="handleFeatureClick" />

    <!-- 三个图片框 -->
    <div class="image-boxes">
      <div class="image-box" v-for="(image, index) in bannerImages" :key="index">
        <img :src="image" alt="图片" />
      </div>
    </div>
  </div>
</template>

<script setup>
import UserInfoBar from '@/components/home/UserInfoBar.vue';
import ServiceButtons from '@/components/home/ServiceButtons.vue';
import FeatureButtons from '@/components/home/FeatureButtons.vue';
import { Swipe, SwipeItem } from 'vant';
import { ref, onMounted } from 'vue';
import { getSwipeImagesSafe } from '@/api/home';

const SwipeImages = ref([]);
const bannerImages = ref([
  '/images/banner/banner1.jpg',
  '/images/banner/banner2.jpg',
  '/images/banner/banner1.jpg' // 先用banner图片填充，可以后续替换
]);

const userInfo = ref({
  avatar: '/images/default-avatar.png',
  username: '爱宠物的用户',
  level: 'S1',
  currentPoints: 0,
  nextLevelPoints: 3,
  nextLevel: 'S2',
  points: 0,
  coupons: 0
});

const features = ref([
  { type: 'coupon-exchange', title: '券码兑换', icon: 'iconfont icon-piao' },
  { type: 'member-exchange', title: '会员兑换', icon: 'iconfont icon-gift' },
  { type: 'points-lottery', title: '积分抽奖', icon: 'iconfont icon-lottery', badge: '周三特惠' },
  { type: 'promotions', title: '优惠活动', icon: 'iconfont icon-promotion' }
]);

const fetchSwipeImageUrl = async () => {
  const data = await getSwipeImagesSafe();
  if (data) {
    SwipeImages.value = data;
  }
}

const handleServiceClick = (type) => {
  console.log('选择服务类型:', type);
  // 处理服务选择逻辑
};

const handleFeatureClick = (type) => {
  console.log('点击功能:', type);
  // 处理功能点击逻辑
};

const onImageLoad = (event) => {
  // 图片加载完成，确保图片显示正确
  const img = event.target;
  if (img && img.complete) {
    // 图片已加载完成
  }
};

onMounted(fetchSwipeImageUrl);
</script>

<style lang="scss" scoped>
.section-new {
  background: #f7f5f0; // 浅米色背景
  min-height: 100vh;
  padding-bottom: 1.6rem;

  .swipe-container {
    position: relative;
    width: 100%;
    overflow: visible; // 允许用户信息条溢出显示

    // 让 swipe 高度自适应图片
    :deep(.van-swipe) {
      height: auto;
    }

    :deep(.van-swipe-item) {
      height: auto;
    }

    .swipe-image {
      width: 100%;
      height: auto;
      max-width: 100%;
      display: block;
      // 保持图片原始宽高比，完整显示不裁剪
      object-fit: contain;
      object-position: center;
    }

    .user-info-wrapper {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      // 使用 transform 将用户信息条向上移动自身高度的一半
      // 这样用户信息条的中部就会对齐到 swipe 的底部
      transform: translateY(50%);
      z-index: 10;
      padding: 0 .2667rem;
    }
  }

  .service-buttons-wrapper {
    margin-top: 1.2rem; // 为用户信息条的下半部分留出空间
  }

  .image-boxes {
    display: flex;
    flex-direction: column;
    gap: .2667rem;
    padding: 0 .2667rem;
    margin-top: .5333rem;

    .image-box {
      width: 100%;
      background: #fff;
      border-radius: .2667rem; // 与上方组件相同的圆角
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      aspect-ratio: 16 / 9; // 横向矩形比例

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
      }
    }
  }
}
</style>


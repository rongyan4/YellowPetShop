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
      <div class="user-info-wrapper" @click="handleUserInfoClick">
        <UserInfoBar :userInfo="computedUserInfo" />
      </div>
    </div>

    <!-- 服务选择按钮 -->
    <div class="service-buttons-wrapper">
      <div class="service-buttons">
        <div class="service-btn self-pickup" @click="handleServiceClick('self-pickup')">
          <div class="btn-content">
            <div class="btn-title">门店自取</div>
            <div class="btn-desc">下单享优惠</div>
          </div>
        </div>
        <div class="service-divider"></div>
        <div class="service-btn delivery" @click="handleServiceClick('delivery')">
          <div class="btn-content">
            <div class="btn-title">快递配送</div>
            <div class="btn-desc">新鲜送上门</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能按钮 -->
    <div class="feature-buttons">
      <div 
        class="feature-btn" 
        v-for="(item, index) in features" 
        :key="index"
        @click="handleFeatureClick(item.type)"
      >
        <div class="feature-icon">
          <svg v-if="item.svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path :d="item.svg"></path>
          </svg>
          <i :class="item.icon" v-else-if="item.icon"></i>
        </div>
        <div class="feature-title">{{ item.title }}</div>
        <div class="feature-badge" v-if="item.badge">{{ item.badge }}</div>
      </div>
    </div>

    <!-- 三个图片框 -->
    <div class="image-boxes">
      <div class="image-box" v-for="(image, index) in bannerImages" :key="index">
        <img :src="image" alt="图片" />
      </div>
    </div>

    <!-- 登录选择弹窗 -->
    <LoginChoice v-if="showLoginChoice" @close="showLoginChoice = false" />
  </div>
</template>

<script setup>
import UserInfoBar from '@/components/shopping/UserInfoBar.vue';
import LoginChoice from '@/components/login/LoginChoice.vue';
import { Swipe, SwipeItem } from 'vant';
import { ref, onMounted, computed } from 'vue';
import { getSwipeImagesSafe } from '@/api/home';
import { useRouter } from 'vue-router';

const router = useRouter(); 

const props = defineProps({
  isLoggedIn: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object,
    default: () => ({
      avatar: '/images/default_avatar.png',
      username: '请登录',
      level: '',
      currentPoints: 0,
      nextLevelPoints: 0,
      nextLevel: '',
      points: '-',
      coupons: '-'
    })
  }
});

const SwipeImages = ref([]);
const showLoginChoice = ref(false);
const bannerImages = ref([
  '/images/banner/banner1.jpg',
  '/images/banner/banner2.jpg',
  '/images/banner/banner1.jpg'
]);

const features = ref([
  {
    type: 'pet-profile',
    title: '宠物档案',
    svg: 'M12 2C8.5 2 5.5 4.5 5.5 8c0 2 .9 3.8 2.3 5C4.7 14.3 3 17 3 20h18c0-3-1.7-5.7-4.8-7 1.4-1.2 2.3-3 2.3-5 0-3.5-3-6-6.5-6zm0 2c2.5 0 4.5 1.8 4.5 4s-2 4-4.5 4S7.5 10.2 7.5 8 9.5 4 12 4z M8 20c0-2.2 1.8-4 4-4s4 1.8 4 4'
  },
  {
    type: 'member-exchange',
    title: '会员兑换',
    svg: 'M20 12V22H4V12 M22 7H2v5h20V7z M12 22V7 M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z'
  },
  {
    type: 'points-lottery',
    title: '积分抽奖',
    badge: '周三特惠',
    svg: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z'
  },
  {
    type: 'promotions',
    title: '优惠活动',
    svg: 'M9 14L3 8l6-6 M3 8h13a5 5 0 0 1 0 10h-1 M15 18l-3 3-3-3'
  }
]);

// 计算用户信息，未登录时不显示等级条
const computedUserInfo = computed(() => {
  if (!props.isLoggedIn) {
    return {
      ...props.userInfo,
      level: '', // 不显示等级
      currentPoints: 0,
      nextLevelPoints: 0
    };
  }
  return props.userInfo;
});

const fetchSwipeImageUrl = async () => {
  const data = await getSwipeImagesSafe();
  if (data) {
    SwipeImages.value = data;
  }
}

// 点击用户信息条
const handleUserInfoClick = () => {
  if (!props.isLoggedIn) {
    // 未登录，弹出登录选择框
    showLoginChoice.value = true;
  } else {
    // 已登录，跳转到我的页面
    router.push('/my');
  }
};

const handleServiceClick = (type) => {
  console.log('选择服务类型:', type);
  // 跳转到购物页面并携带参数
  if (type === 'self-pickup') {
    router.push({ path: '/shopping', query: { by: 'take' } });
  } else if (type === 'delivery') {
    router.push({ path: '/shopping', query: { by: 'delivery' } });
  }
};

const handleFeatureClick = (type) => {
  console.log('点击功能:', type);
  
  if (type === 'pet-profile') {
    // 宠物档案功能
    if (!props.isLoggedIn) {
      showLoginChoice.value = true;
    } else {
      router.push('/pet-profile');
    }
  } else {
    // 其他功能暂未实现
    console.log('功能开发中:', type);
  }
};

const onImageLoad = (event) => {
  // 图片加载完成
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
      cursor: pointer;
    }
  }

  .service-buttons-wrapper {
    margin-top: 1.5rem; // 为用户信息条的下半部分留出空间
  }

  .service-buttons {
    display: flex;
    align-items: stretch;
    gap: 0;
    padding: 0 .2667rem;
    margin: .5333rem 0;
    background: #fff;
    border-radius: .2667rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    overflow: hidden;

    .service-btn {
      flex: 1;
      padding: .4rem .2667rem;
      position: relative;
      transition: background-color 0.2s;

      &:active {
        background-color: #f5f5f5;
      }

      .btn-content {
        .btn-title {
          font-size: .4267rem;
          font-weight: 600;
          color: #333;
          margin-bottom: .1333rem;
        }

        .btn-desc {
          font-size: .32rem;
          color: #999;
        }
      }
    }

    .service-divider {
      width: 1px;
      background: #e0e0e0;
      margin: .2667rem 0;
    }
  }

  .feature-buttons {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: .2667rem;
    padding: 0 .2667rem;
    margin: .2667rem 0 .5333rem 0;

    .feature-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: .4rem .1333rem;
      background: #fff;
      border-radius: .2667rem;
      position: relative;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: transform 0.2s, box-shadow 0.2s;
      cursor: pointer;

      &:active {
        transform: scale(0.95);
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
      }

      .feature-icon {
        width: .8rem;
        height: .8rem;
        margin-bottom: .1333rem;
        display: flex;
        align-items: center;
        justify-content: center;

        svg {
          width: 100%;
          height: 100%;
          color: #F5A623;
          stroke: #F5A623;
        }

        i {
          font-size: .8rem;
          color: #666;
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
        }
      }

      .feature-title {
        font-size: .32rem;
        color: #333;
        text-align: center;
      }

      .feature-badge {
        position: absolute;
        top: .1333rem;
        right: .1333rem;
        background: #FF6B6B;
        color: #fff;
        font-size: .2133rem;
        padding: 0.0267rem 0.1067rem;
        border-radius: 0.1067rem;
        white-space: nowrap;
      }
    }
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


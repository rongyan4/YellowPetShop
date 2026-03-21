<template>
  <div class="new-home">
    <div class="home-content">
      <Home :isLoggedIn="isLoggedIn" :userInfo="userInfo"></Home>
    </div>
    <Tabbar></Tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Home from "@/components/home/Home.vue";
import Tabbar from "@/components/TabBar.vue";
import { isAuthenticated } from '@/utils/auth';
import { useUserStore } from '@/stores/user';
import { getCurrentUserInfo } from '@/api/user';

const router = useRouter();
const userStore = useUserStore();
const isLoggedIn = ref(false);
const userInfo = ref({
  avatar: '/images/default_avatar.png',
  username: '请登录',
  level: '',
  currentPoints: 0,
  nextLevelPoints: 0,
  nextLevel: '',
  points: '-',
  coupons: '-'
});

onMounted(async () => {
  // 检查登录状态（token 存于 HttpOnly Cookie，前端不可读，用 localStorage 的 userInfo 判断）
  if (isAuthenticated()) {
    isLoggedIn.value = true;

    try {
      // 优先从后端获取最新的用户信息（包含积分和等级）
      const res = await getCurrentUserInfo();
      if (res.code === 200 && res.data) {
        const data = res.data;
        // 同步到全局 userStore，供其他页面复用
        userStore.setUserInfo(data);

        userInfo.value = {
          avatar: data.avatar || '/images/default_avatar.png',
          username: data.nickname || data.username || '用户',
          level: data.level || 'S1',
          currentPoints: data.currentPoints ?? 0,
          nextLevelPoints: data.nextLevelPoints ?? 0,
          nextLevel: data.nextLevel || '',
          points: data.points ?? 0,
          // 优惠券目前暂无后端数据，占位为 0
          coupons: 0
        };
        return;
      }
    } catch (e) {
      console.error('获取用户信息失败，从本地 store 兜底展示', e);
    }

    // 后端请求失败时，从本地 store 中读取缓存的用户信息兜底
    const cached = userStore.userInfo;
    if (cached) {
      userInfo.value = {
        avatar: cached.avatar || '/images/default_avatar.png',
        username: cached.nickname || cached.username || '用户',
        level: cached.level || 'S1',
        currentPoints: cached.currentPoints ?? 0,
        nextLevelPoints: cached.nextLevelPoints ?? 20,
        nextLevel: cached.nextLevel || 'S2',
        points: cached.points ?? 0,
        coupons: 0
      };
    }
  } else {
    console.log('未登录状态');
    isLoggedIn.value = false;
    // 未登录时的默认信息
    userInfo.value = {
      avatar: '/images/default_avatar.png',
      username: '请登录',
      level: '',
      currentPoints: 0,
      nextLevelPoints: 0,
      nextLevel: '',
      points: '-',
      coupons: '-'
    };
  }
});
</script>

<style lang="scss" scoped>
.new-home {
  background: #f7f5f0;
  min-height: 100vh;
  padding-bottom: 1.6rem;

  .home-content {
    width: 100%;
  }
}
</style>


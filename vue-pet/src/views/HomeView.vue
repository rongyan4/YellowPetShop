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
import { getToken, parseJWT, isAuthenticated } from '@/utils/auth';

const router = useRouter();
const isLoggedIn = ref(false);
const userInfo = ref({
  avatar: '/images/touxiang.jpg',
  username: '请登录',
  level: '',
  currentPoints: 0,
  nextLevelPoints: 0,
  nextLevel: '',
  points: '-',
  coupons: '-'
});

onMounted(() => {
  // 检查登录状态
  const token = getToken();
  
  if (token && isAuthenticated()) {
    isLoggedIn.value = true;
    
    // 解析token获取用户信息
    const payload = parseJWT(token);
    if (payload) {
      console.log('=== NewHome 加载 - Token信息 ===');
      console.log('Token:', token);
      console.log('Username:', payload.username);
      console.log('============================');
      
      // 更新用户信息
      userInfo.value = {
        avatar: '/images/touxiang.jpg', // 可以从后端获取
        username: payload.username || '用户',
        level: 'S1',
        currentPoints: 0,
        nextLevelPoints: 3,
        nextLevel: 'S2',
        points: 0,
        coupons: 0
      };
    }
  } else {
    console.log('未登录状态');
    isLoggedIn.value = false;
    // 未登录时的默认信息
    userInfo.value = {
      avatar: '/images/touxiang.jpg',
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


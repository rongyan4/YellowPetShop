<template>
  <div class="shopping">
    <Header></Header>
    <div class="content">
      <tabs v-model:active="active">
        <tab 
        v-for="(item, index) in tabList" 
        :title="item.title"
        :key="item.type">
          <div v-if="active === 0">
            <section1></section1>
          </div>
          <div v-if="active === 1">
            <List></List>
          </div>
          <div v-if="active === 2">
            内容3
          </div>
        </tab>
      </tabs>
    <Tabbar></Tabbar>
    </div>
  </div>
</template>

<script setup>
import Tabbar from "@/components/TabBar.vue";
import Header from "@/components/shopping/Header.vue"
import section1 from "@/components/shopping/section1/section1.vue"
import List from "@/components/shopping/list/List.vue"
import { onBeforeMount, onMounted, ref } from 'vue';
import { Tab,Tabs } from 'vant';
import axios from 'axios';
import { getToken, parseJWT } from '@/utils/auth';

onBeforeMount(async() => {
  let res = await({
    url:'/api/home'
  })
})

onMounted(() => {
  // 获取token
  const token = getToken();
  
  if (token) {
    console.log('=== JWT Token 信息 ===');
    console.log('Token:', token);
    
    // 解析token获取用户信息
    const payload = parseJWT(token);
    if (payload) {
      console.log('Token Payload:', payload);
      console.log('Username:', payload.username || payload.sub);
      console.log('User ID:', payload.userId || payload.sub);
      console.log('过期时间:', payload.exp ? new Date(payload.exp * 1000).toLocaleString() : '未知');
    } else {
      console.log('Token解析失败');
    }
    console.log('=====================');
  } else {
    console.log('未登录，没有Token');
  }
});

const active = ref(0);
const tabList = ref([
  { type:1 , title:"推荐"},
  { type:2 , title:"分类"},
  { type:3 , title:"推荐"},
])

</script>

<style scoped>
.content{
  margin-top: 2rem;
}
.shopping{
  height: calc(100vh - 1.6rem);
  overflow-y: auto;
  padding-bottom: .2667rem;
}

:deep(.van-tab__text) {
  font-size: .3733rem;  /* 固定文字大小 */
}

:deep(.van-tabs__line) {
  width: 1.0667rem;     /* 固定下划线宽度 */
  height: .0533rem;     /* 固定下划线高度 */
  background-color: #2c3e50;
  border-radius: .0267rem;
}

:deep(.van-tabs__wrap) {
  height: 1.1733rem;    /* 固定 Tab 栏高度 */
}
</style>

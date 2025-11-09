<template>
  <div class="tabbar">
    <ul>
      <li v-for="(item, index) in tabbarList"
        :key="index"
        @click= "switchItem(item.path)">
        <img :src="item.icon">
        <span>{{item.title}}</span>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from 'vue-router';
const router = useRouter()
const tabbarList = ref([
  {title: "首页",path: "/",icon: "./images/home.png",selectIcon: "./images/home-select.png",},
  {title: "分类",path: "/list",icon: "./images/list.png",selectIcon: "./images/list-select.png",},
  {title: "购物车",path: "/car",icon: "./images/car.png",selectIcon: "./images/car-select.png",},
  {title: "我的",path: "/my",icon: "./images/my.png",selectIcon: "./images/my-select.png",},
])
const switchItem = (path) => {
  router.replace(path)
}
</script>

<style scoped lang="scss">
  .tabbar{
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 50px;
    
  }
</style>
<template>
  <div class="tabbar">
    <ul>
      <li v-for="(item, index) in tabbarList"
        :key="index"
        @click= "switchItem(item.path)"
        >
        <img :src="$route.path.includes(item.path) ? item.selectIcon : item.icon">
        <span 
          :style="{ 
            color: $route.path.includes(item.path) ? '#AEE4FF' : '#999' 
          }"
        >
          {{ item.title }}
        </span>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from 'vue-router';
const router = useRouter()
const tabbarList = ref([
  {title: "首页",path: "/home",icon: "../images/tabbar/home.png",selectIcon: "./images/tabbar/home-select.png",},
  {title: "分类",path: "/list",icon: "../images/tabbar/list.png",selectIcon: "./images/tabbar/list-select.png",},
  {title: "购物车",path: "/car",icon: "../images/tabbar/cart.png",selectIcon: "./images/tabbar/cart-select.png",},
  {title: "我的",path: "/my",icon: "../images/tabbar/my.png",selectIcon: "./images/tabbar/my-select.png",},
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
    height: 2.2rem;
    background-color: plum;
    z-index: 999;
    ul{
      display: flex;
      align-items: center;
      justify-content: space-around;
      width: 100%;
      height: 100%;
      li{
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content:center;
        img{
          height: 1rem;
          width: 1rem;
        }
        span{
          font-size: 0.45rem;
          color: #999;
        }
      }
    }
  }
</style>
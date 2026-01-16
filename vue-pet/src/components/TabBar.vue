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
            color: $route.path.includes(item.path) ? '#333' : '#666' 
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
  {title: "首页",path: "/newhome",icon: "/images/tabbar/home.png",selectIcon: "/images/tabbar/home-select.png",},
  {title: "购物",path: "/home",icon: "/images/tabbar/list.png",selectIcon: "/images/tabbar/list-select.png",},
  {title: "购物车",path: "/car",icon: "/images/tabbar/cart.png",selectIcon: "/images/tabbar/cart-select.png",},
  {title: "我的",path: "/my",icon: "/images/tabbar/my.png",selectIcon: "/images/tabbar/my-select.png",},
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
    height: 1.6rem; 
    background-color: #fff; /* 白色背景 */
    border-top: 1px solid #e5e5e5; /* 添加顶部边框 */
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05); /* 添加阴影 */
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
        cursor: pointer;
        transition: transform 0.2s;
        
        &:active {
          transform: scale(0.95);
        }
        
        img{
          height: 0.64rem; /* 从 1rem 调整为 0.64rem */
          width: 0.64rem;
          margin-bottom: 0.1rem;
          filter: grayscale(100%) brightness(0.4); /* 将图标变为深灰色 #666 */
        }
        span{
          font-size: 0.32rem; /* 从 0.45rem 调整为 0.32rem */
          color: #666; /* 黑色文字 */
        }
      }
    }
  }
</style>
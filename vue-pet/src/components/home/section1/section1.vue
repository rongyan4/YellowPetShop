<template>
  <div class="section1">
    <swipe :autoplay="3000" lazy-render>
      <swipe-item v-for="image in images" :key="image">
        <img style="width:100%; height:100%" :src="image" />
      </swipe-item>
    </swipe>
    <Favirate></Favirate>
  </div>
</template>

<script setup>
  import Favirate from '@/components/home/section1/Favirate.vue';
  
  import {Swipe, SwipeItem} from 'vant';
  import {ref, reactive} from 'vue';
  
 const images = [
  "/images/banner/banner1.jpg",
  "/images/banner/banner2.jpg",
  
];

</script>

<style lang="scss">
.swiper{
  height: 400rpx;
}
</style>
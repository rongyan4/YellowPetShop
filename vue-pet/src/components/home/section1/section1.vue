<template>
  <div class="section1">
    <swipe :autoplay="3000" lazy-render>
      <swipe-item v-for="(image, index) in SwipeImages" :key="index">
        <img style="width:100%; height:100%" :src="image" />
      </swipe-item>
    </swipe>
    <!-- <Recommend></Recommend> -->
  </div>
</template>

<script setup>
  import Recommend from '@/components/home/section1/Recommend.vue';
  
  import {Swipe, SwipeItem} from 'vant';
//  const images = [
//   "/images/banner/banner1.jpg",
//   "/images/banner/banner2.jpg",
  
// ];
  import {ref, onMounted} from 'vue';
  import { getSwipeImages } from '@/api/home';

const SwipeImages = ref([]);

const fetchSwipeImageUrl = async () => {
  try {
    const result = await getSwipeImages();
    SwipeImages.value = result.data;
    console.log('轮播图数据:', result.data);
  } catch (error) {
    console.error('获取图片地址失败:', error);
  }
}

onMounted(fetchSwipeImageUrl)

</script>

<style lang="scss">
.swiper{
  height: 400rpx;
}
</style>
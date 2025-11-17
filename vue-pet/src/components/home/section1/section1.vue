<template>
  <div class="section1">
    <swipe :autoplay="3000" lazy-render>
      <swipe-item v-for="(image, index) in SwipeImages" :key="index">
        <img style="width:100%; height:100%" :src="image" />
      </swipe-item>
    </swipe>
    <Recommend></Recommend>
  </div>
</template>

<script setup>
  import Recommend from '@/components/home/section1/Recommend.vue';
  
  import {Swipe, SwipeItem} from 'vant';
  import {ref, onMounted} from 'vue';
  import { getSwipeImagesSafe } from '@/api/home';

const SwipeImages = ref([]);

const fetchSwipeImageUrl = async () => {
  const result = await getSwipeImagesSafe();
  if (result) {
    SwipeImages.value = result.data;
    console.log('轮播图数据:', result.data);
  }
}

onMounted(fetchSwipeImageUrl)

</script>

<style lang="scss">
.swiper{
  height: 400rpx;
}
</style>
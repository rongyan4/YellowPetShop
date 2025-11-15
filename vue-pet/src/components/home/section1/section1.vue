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
  import {ref, reactive, onMounted} from 'vue';
  import axios from 'axios';
  
//  const images = [
//   "/images/banner/banner1.jpg",
//   "/images/banner/banner2.jpg",
  
// ];

const SwipeImages = ref([]);

const fetchSwipeImageUrl = async () => {
  try {
    const response = await axios.get('/api/home/swipe')
    const result = response.data;
    
    if (result.code === 200){
      SwipeImages.value = result.data;
      console.log(response.data);
    }else{
      
    }
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
<template>
  <div class="container">
    <div class="title">
      <i class="iconfont icon-chongwugou"></i>
      <h1>精品推荐</h1>
    </div>
    <CommodityList :goods="recommendGoods" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import CommodityList from '@/components/CommodityList.vue';
import { getRecommendGoodsSafe } from '@/api/home';

const recommendGoods = ref([]);

const fetchRecommendGoods = async () => {
  const data = await getRecommendGoodsSafe();
  if (data) {
    recommendGoods.value = data;
  }
}

onMounted(fetchRecommendGoods)
</script>

<style lang="scss" scoped>
.container {
  background-color: #f7f7f7;
  padding: 0 .2667rem .5333rem;
  font-size: .3733rem;

  .title {
    display: flex;
    align-items: center;
    padding: .4rem .2667rem;
    margin-bottom: .2667rem;

    .iconfont {
      color: #a0e5ff;
      font-size: .5867rem;
    }

    h1 {
      font-size: .48rem;
      margin-left: .2667rem;
      font-weight: 600;
    }
  }
}
</style>
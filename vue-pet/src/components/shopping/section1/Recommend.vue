<template>
  <div class="container">
    <div class="title">
      <i class="iconfont icon-chongwugou"></i>
      <h1>精品推荐</h1>
    </div>

    
    <!-- 使用 Vant List 组件实现下拉加载 -->
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多内容啦~"
      loading-text="加载中..."
      @load="onLoad"
      :immediate-check="true"
    >
      <CommodityList :goods="recommendGoods" />
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import CommodityList from '@/components/CommodityList.vue';
import { getGoodsByPageSafe } from '@/api/goods';
import { List as VanList } from 'vant';

const recommendGoods = ref([]);
const loading = ref(false);
const finished = ref(false);
const currentPage = ref(1);
const pageSize = ref(8);

// 加载商品数据（分页）
const onLoad = async () => {
  try {
    loading.value = true;
    
    // 调用分页接口
    const pageResult = await getGoodsByPageSafe(currentPage.value, pageSize.value);
    
    if (pageResult && pageResult.records) {
      // 追加数据到列表
      recommendGoods.value = [...recommendGoods.value, ...pageResult.records];
      
      // 判断是否还有更多数据
      if (!pageResult.hasNext || pageResult.records.length === 0) {
        finished.value = true;
      } else {
        currentPage.value++;
      }
      
      console.log(`加载第 ${currentPage.value - 1} 页，共 ${pageResult.records.length} 条数据`);
      console.log(`总数据: ${recommendGoods.value.length}/${pageResult.total}`);
    } else {
      finished.value = true;
    }
  } catch (error) {
    console.error('加载商品失败:', error);
    finished.value = true;
  } finally {
    loading.value = false;
  }
};
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
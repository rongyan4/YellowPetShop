<template>
  <div class="search-container">
    <!-- 搜索结果 -->
    <div v-if="showSearchResult" class="search-result">
      <div v-if="searchResultList.length > 0" class="result-wrapper">
        <CommodityList :goods="searchResultList" />
      </div>
      <div v-else class="empty-result">
        <van-empty description="没有找到相关商品" />
      </div>
    </div>

    <!-- 搜索建议（未搜索时显示） -->
    <div v-else class="search-suggestions">
      <!-- 历史搜索 -->
      <div class="history-section">
        <div class="section-header">
          <h3 class="section-title">历史搜索</h3>
          <button class="clear-history-btn" @click="clearHistory">
            <van-icon name="delete-o" />
            清除
          </button>
        </div>
        <div class="tag-list">
          <div 
            v-for="(item, index) in historyList" 
            :key="index"
            class="tag-item"
            @click="handleTagClick(item)"
          >
            {{ item }}
          </div>
        </div>
      </div>

      <!-- 猜你要搜 -->
      <div class="guess-section">
        <div class="section-header">
          <h3 class="section-title">热门推荐</h3>
        </div>
        <div class="tag-list">
          <div 
            v-for="(item, index) in guessList" 
            :key="index"
            class="tag-item guess-tag"
            @click="handleTagClick(item)"
          >
            {{ item }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits, defineExpose } from 'vue';
import { Icon as VanIcon, Empty as VanEmpty, showToast } from 'vant';
import CommodityList from '@/components/CommodityList.vue';
import { searchGoodsSafe } from '@/api/goods';
import { get, del, safeRequestData } from '@/utils/request';

const emit = defineEmits(['tagClick']);

const historyList = ref([]);
const guessList = ref([]);
const showSearchResult = ref(false);
const searchResultList = ref([]);

// 获取历史搜索
const fetchHistorySearch = async () => {
  try {
    const data = await safeRequestData(get('history_search'));
    if (data && Array.isArray(data)) {
      historyList.value = data;
    }
  } catch (error) {
    console.error('获取历史搜索失败:', error);
  }
};

// 获取猜你要搜
const fetchGuessYouWant = async () => {
  try {
    const data = await safeRequestData(get('guss_you_want'));
    if (data && Array.isArray(data)) {
      guessList.value = data;
    }
  } catch (error) {
    console.error('获取猜你要搜失败:', error);
  }
};

// 执行搜索
const performSearch = async (keyword) => {
  if (!keyword || !keyword.trim()) {
    showToast('请输入搜索关键词');
    return;
  }
  
  try {
    console.log('搜索关键词:', keyword);
    const data = await searchGoodsSafe(keyword);
    
    searchResultList.value = data || [];
    showSearchResult.value = true;
    
    // 刷新历史记录（后端已保存）
    await fetchHistorySearch();
    
    if (!data || data.length === 0) {
      showToast('未找到相关商品');
    }
  } catch (error) {
    console.error('搜索失败:', error);
    showToast('搜索失败，请稍后重试');
  }
};

// 清除历史记录
const clearHistory = async () => {
  try {
    await del('history_search');
    historyList.value = [];
    showToast('已清除历史记录');
  } catch (error) {
    console.error('清除历史记录失败:', error);
    showToast('清除失败，请稍后重试');
  }
};

// 点击标签
const handleTagClick = (tag) => {
  emit('tagClick', tag);
};

// 重置搜索状态（供外部调用）
const resetSearch = () => {
  showSearchResult.value = false;
  searchResultList.value = [];
};

// 暴露方法给父组件
defineExpose({
  performSearch,
  resetSearch
});

onMounted(() => {
  fetchHistorySearch();
  fetchGuessYouWant();
});
</script>

<style lang="scss" scoped>
.search-container {
  min-height: 100vh;
  background: #f7f5f0;
  padding-bottom: 1.6rem;
}

.search-result {
  padding: .2667rem;
  
  .result-wrapper {
    // 两列瀑布流容器
    :deep(.commodity-list) {
      .list {
        column-count: 2;
        column-gap: .2667rem;
      }
    }
  }
  
  .empty-result {
    padding-top: 2.6667rem;
    text-align: center;
  }
}

.search-suggestions {
  .history-section,
  .guess-section {
    padding: .4rem .2667rem;

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: .4rem;

      .section-title {
        font-size: .4267rem;
        font-weight: 600;
        color: #333;
        margin: 0;
      }

      .clear-history-btn {
        display: flex;
        align-items: center;
        gap: .1333rem;
        font-size: .32rem;
        color: #999;
        background: transparent;
        border: none;
        padding: 0;
        cursor: pointer;

        :deep(.van-icon) {
          font-size: .3733rem;
        }
      }
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: .2667rem;

      .tag-item {
        padding: .2133rem .4rem;
        background: #fff;
        border-radius: .5333rem;
        font-size: .3733rem;
        color: #666;
        cursor: pointer;
        transition: all 0.2s;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

        &:active {
          transform: scale(0.95);
          background: #f5f5f5;
        }
      }

      .guess-tag {
        background: linear-gradient(135deg, #fff 0%, #f9f9f9 100%);
      }
    }
  }

  .guess-section {
    .section-title {
      color: #333;
    }
  }
}
</style>

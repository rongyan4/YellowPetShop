<template>
  <div class="browse-history">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      title="浏览记录"
      left-arrow
      @click-left="onClickLeft"
      fixed
      placeholder
    >
      <template #right>
        <van-button
          size="small"
          type="danger"
          plain
          @click="clearHistory"
          v-if="historyList.length > 0"
        >
          清空
        </van-button>
      </template>
    </van-nav-bar>

    <!-- 浏览记录列表 -->
    <div class="history-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <div v-if="historyList.length > 0" class="goods-list">
          <div
            v-for="item in historyList"
            :key="item.id"
            class="goods-item"
            @click="goToDetail(item.commodityId)"
          >
            <van-image
              :src="item.mainPicUrl || '/images/goods/ml.png'"
              fit="cover"
              class="goods-image"
            />
            <div class="goods-info">
              <div class="goods-name">{{ item.name }}</div>
              <div class="goods-meta">
                <span class="browse-time">{{ formatTime(item.browseTime) }}</span>
              </div>
              <div class="goods-bottom">
                <div class="goods-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ item.price }}</span>
                </div>
                <span class="sold-count">已售{{ item.sold || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty
          v-else
          description="暂无浏览记录"
          image="search"
        />
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';
import { getBrowseHistoryListSafe, clearBrowseHistorySafe } from '@/api/browse';

const router = useRouter();

// 浏览记录列表
const historyList = ref([]);
const refreshing = ref(false);

// 返回上一页
const onClickLeft = () => {
  router.back();
};

// 加载浏览记录
const loadHistoryList = async () => {
  try {
    const data = await getBrowseHistoryListSafe(100);
    if (data !== null) {
      historyList.value = data;
    }
  } catch (error) {
    console.error('加载浏览记录失败:', error);
    showToast('加载失败');
  }
};

// 下拉刷新
const onRefresh = async () => {
  await loadHistoryList();
  refreshing.value = false;
  showToast('刷新成功');
};

// 清空浏览记录
const clearHistory = async () => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要清空所有浏览记录吗？',
    });

    const result = await clearBrowseHistorySafe();
    if (result !== null) {
      showToast('已清空浏览记录');
      historyList.value = [];
    }
  } catch (error) {
    // 用户取消操作
  }
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  
  const time = new Date(timeStr);
  const now = new Date();
  const diff = now - time;
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚';
  }
  
  // 小于1小时
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`;
  }
  
  // 小于1天
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`;
  }
  
  // 小于7天
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`;
  }
  
  // 显示日期
  const month = time.getMonth() + 1;
  const day = time.getDate();
  return `${month}月${day}日`;
};

// 跳转到商品详情
const goToDetail = (commodityId) => {
  router.push({
    path: '/good-details',
    query: { id: commodityId }
  });
};

onMounted(() => {
  loadHistoryList();
});
</script>

<style scoped>
.browse-history {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.history-content {
  padding: 12px;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.goods-item:active {
  transform: scale(0.98);
}

.goods-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  flex-shrink: 0;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 15px;
  color: #333;
  line-height: 20px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.goods-meta {
  margin: 4px 0;
}

.browse-time {
  font-size: 12px;
  color: #999;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-price {
  display: flex;
  align-items: baseline;
  color: #ff6034;
}

.price-symbol {
  font-size: 12px;
  font-weight: bold;
}

.price-value {
  font-size: 20px;
  font-weight: bold;
}

.sold-count {
  font-size: 12px;
  color: #999;
}
</style>

<template>
  <div class="my-favorites">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      title="我的收藏"
      left-arrow
      @click-left="onClickLeft"
      fixed
      placeholder
    />

    <!-- 收藏列表 -->
    <div class="favorites-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <div v-if="favoriteList.length > 0" class="goods-list">
          <div
            v-for="item in favoriteList"
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
              <div class="goods-bottom">
                <div class="goods-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ item.price }}</span>
                </div>
                <van-button
                  size="small"
                  type="danger"
                  plain
                  @click.stop="removeFavorite(item.commodityId)"
                >
                  取消收藏
                </van-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty
          v-else
          description="暂无收藏商品"
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
import { getFavoriteListSafe, removeFavoriteSafe } from '@/api/favorite';

const router = useRouter();

// 收藏列表
const favoriteList = ref([]);
const refreshing = ref(false);

// 返回上一页
const onClickLeft = () => {
  router.back();
};

// 加载收藏列表
const loadFavoriteList = async () => {
  try {
    const data = await getFavoriteListSafe();
    if (data !== null) {
      favoriteList.value = data;
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error);
    showToast('加载失败');
  }
};

// 下拉刷新
const onRefresh = async () => {
  await loadFavoriteList();
  refreshing.value = false;
  showToast('刷新成功');
};

// 取消收藏
const removeFavorite = async (commodityId) => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要取消收藏吗？',
    });

    const result = await removeFavoriteSafe(commodityId);
    if (result !== null) {
      showToast('已取消收藏');
      // 重新加载列表
      loadFavoriteList();
    }
  } catch (error) {
    // 用户取消操作
  }
};

// 跳转到商品详情
const goToDetail = (commodityId) => {
  router.push({
    path: '/good/details',
    query: { id: commodityId }
  });
};

onMounted(() => {
  loadFavoriteList();
});
</script>

<style scoped>
.my-favorites {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.favorites-content {
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
  margin-bottom: 8px;
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
</style>

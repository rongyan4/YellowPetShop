<template>
  <div class="list-view">
    <!-- 分类预览模式 -->
    <div v-if="!selectedCategory" class="category-preview-mode">
      <!-- 左侧分类导航 -->
      <div class="category-sidebar">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          @click="scrollToCategory(category.id)"
        >
          <van-icon :name="category.icon || 'bag-o'" size="20" />
          <span>{{ category.name }}</span>
        </div>
      </div>

      <!-- 右侧分类商品预览 -->
      <div class="category-content" ref="categoryContentRef">
        <div
          v-for="category in categories"
          :key="category.id"
          :ref="el => setCategoryRef(category.id, el)"
          class="category-section"
        >
          <div class="category-header">
            <h2 class="category-title">{{ category.name }}</h2>
          </div>
          
          <div class="goods-grid">
            <div
              v-for="(item, index) in categoryGoods[category.id] || []"
              :key="item.id"
              class="goods-item"
              :class="{ 'goods-item--more': index === 5 }"
              @click="index === 5 ? viewMoreGoods(category) : handleGoodsClick(item)"
            >
              <template v-if="index < 5">
                <div class="goods-img">
                  <img :src="item.mainPicUrl || '/images/goods/ml.png'" :alt="item.name" />
                </div>
                <div class="goods-info">
                  <h3 class="goods-title">{{ item.name }}</h3>
                  <div class="goods-price">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ item.price }}</span>
                  </div>
                </div>
              </template>
              <template v-else>
                <div class="more-icon">
                  <van-icon name="plus" size="32" />
                  <span>查看更多</span>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类详情模式（瀑布流） -->
    <div v-else class="category-detail-mode">
      <div class="detail-header">
        <van-icon name="arrow-left" @click="backToPreview" />
        <h2>{{ selectedCategory.name }}</h2>
      </div>
      
      <div class="detail-content">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          loading-text="加载中..."
          @load="onLoad"
        >
          <div class="goods-waterfall">
            <div
              v-for="item in goodsList"
              :key="item.id"
              class="waterfall-item"
              @click="handleGoodsClick(item)"
            >
              <div class="goods-img">
                <img :src="item.mainPicUrl || '/images/goods/ml.png'" :alt="item.name" />
              </div>
              <div class="goods-info">
                <h3 class="goods-title">{{ item.name }}</h3>
                <div class="goods-desc" v-if="item.msg">{{ item.msg }}</div>
                <div class="goods-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ item.price }}</span>
                  <span class="price-unit" v-if="item.unit">/{{ item.unit }}</span>
                </div>
              </div>
            </div>
          </div>
        </van-list>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';
import { getAllCategoriesSafe, getGoodsByCategorySafe } from '@/api/goods';

const router = useRouter();

// 分类列表
const categories = ref([]);

// 分类商品预览数据（每个分类最多6个商品，第6个用于"查看更多"）
const categoryGoods = ref({});

// 分类元素引用
const categoryRefs = ref({});
const categoryContentRef = ref(null);

// 选中的分类（用于详情模式）
const selectedCategory = ref(null);

// 详情模式的商品列表
const goodsList = ref([]);
const loading = ref(false);
const finished = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);

// 设置分类元素引用
const setCategoryRef = (categoryId, el) => {
  if (el) {
    categoryRefs.value[categoryId] = el;
  }
};

// 加载所有分类
const loadCategories = async () => {
  const data = await getAllCategoriesSafe();
  if (data && Array.isArray(data)) {
    categories.value = data;
    // 为每个分类加载商品预览
    for (const category of data) {
      await loadCategoryPreview(category.id);
    }
  }
};

// 加载分类商品预览（6个商品）
const loadCategoryPreview = async (categoryId) => {
  const pageResult = await getGoodsByCategorySafe(categoryId, 1, 6);
  if (pageResult && pageResult.records) {
    categoryGoods.value[categoryId] = pageResult.records;
  }
};

// 滚动到指定分类
const scrollToCategory = (categoryId) => {
  const element = categoryRefs.value[categoryId];
  if (element && categoryContentRef.value) {
    const container = categoryContentRef.value;
    const offsetTop = element.offsetTop - container.offsetTop;
    container.scrollTo({
      top: offsetTop,
      behavior: 'smooth'
    });
  }
};

// 查看更多商品（进入分类详情模式）
const viewMoreGoods = (category) => {
  selectedCategory.value = category;
  goodsList.value = [];
  currentPage.value = 1;
  finished.value = false;
  
  // 触发加载
  nextTick(() => {
    onLoad();
  });
};

// 返回预览模式
const backToPreview = () => {
  selectedCategory.value = null;
  goodsList.value = [];
  currentPage.value = 1;
  finished.value = false;
};

// 加载分类详情商品（分页）
const onLoad = async () => {
  if (!selectedCategory.value) return;
  
  try {
    loading.value = true;
    
    const pageResult = await getGoodsByCategorySafe(
      selectedCategory.value.id,
      currentPage.value,
      pageSize.value
    );
    
    if (pageResult && pageResult.records) {
      goodsList.value = [...goodsList.value, ...pageResult.records];
      
      if (!pageResult.hasNext || pageResult.records.length === 0) {
        finished.value = true;
      } else {
        currentPage.value++;
      }
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

// 点击商品跳转详情
const handleGoodsClick = (item) => {
  router.push({
    path: '/good-details',
    query: { id: item.id }
  });
};

onMounted(() => {
  loadCategories();
});
</script>

<style lang="scss" scoped>
.list-view {
  height: 100%;
  background-color: #f7f7f7;
  overflow: hidden;
}

/* 分类预览模式 */
.category-preview-mode {
  display: flex;
  height: 100%;
}

.category-sidebar {
  width: 2rem;
  background-color: #f8f8f8;
  overflow-y: auto;
  flex-shrink: 0;
}

.category-item {
  padding: 0.4rem 0.2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.1333rem;
  font-size: 0.32rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    background-color: #f0f0f0;
  }

  span {
    text-align: center;
    word-break: keep-all;
  }
}

.category-content {
  flex: 1;
  overflow-y: auto;
  background-color: #fff;
  padding: 0.2667rem;
}

.category-section {
  margin-bottom: 0.5333rem;

  &:last-child {
    margin-bottom: 0;
  }
}

.category-header {
  margin-bottom: 0.2667rem;
}

.category-title {
  font-size: 0.4267rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  padding: 0.2667rem 0;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.2667rem;
}

.goods-item {
  background-color: #fff;
  border-radius: 0.2133rem;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: scale(0.98);
  }

  &--more {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    min-height: 3.2rem;
  }
}

.goods-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  overflow: hidden;
  background-color: #f5f5f5;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.goods-info {
  padding: 0.2667rem;
}

.goods-title {
  font-size: 0.32rem;
  color: #333;
  margin: 0 0 0.1333rem 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-price {
  display: flex;
  align-items: baseline;
  color: #ff4d4f;
  font-weight: 600;

  .price-symbol {
    font-size: 0.2933rem;
  }

  .price-value {
    font-size: 0.3733rem;
  }
}

.more-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.1333rem;
  color: #999;

  span {
    font-size: 0.32rem;
  }
}

/* 分类详情模式 */
.category-detail-mode {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 0.2667rem;
  padding: 0.4rem;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;

  h2 {
    font-size: 0.4267rem;
    font-weight: 600;
    margin: 0;
  }

  .van-icon {
    font-size: 0.5333rem;
    cursor: pointer;
  }
}

.detail-content {
  flex: 1;
  overflow-y: auto;
  background-color: #f7f7f7;
  padding: 0.2667rem;
}

.goods-waterfall {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.2667rem;
}

.waterfall-item {
  background-color: #fff;
  border-radius: 0.2133rem;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: scale(0.98);
  }

  .goods-img {
    width: 100%;
    aspect-ratio: 1 / 1;
  }

  .goods-info {
    padding: 0.2667rem;
  }

  .goods-title {
    font-size: 0.3467rem;
    margin-bottom: 0.1333rem;
  }

  .goods-desc {
    font-size: 0.2933rem;
    color: #999;
    margin-bottom: 0.1333rem;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .goods-price {
    .price-symbol {
      font-size: 0.32rem;
    }

    .price-value {
      font-size: 0.4267rem;
    }

    .price-unit {
      font-size: 0.2933rem;
      color: #999;
      font-weight: normal;
      margin-left: 0.0533rem;
    }
  }
}
</style>

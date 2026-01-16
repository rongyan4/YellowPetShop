<template>
  <div class="list-view">
    <div class="category-container">
      <!-- 左侧分类导航 -->
      <div class="category-sidebar">
        <div
          v-for="(category, index) in categories"
          :key="category.id || index"
          class="category-item"
          :class="{ 'category-item--active': activeCategory === index }"
          @click="onCategoryChange(index)"
        >
          {{ category.name }}
        </div>
      </div>

      <!-- 右侧商品列表 -->
      <div class="goods-container" ref="goodsContainerRef" @scroll="onScroll">
        <div
          v-for="(category, index) in categories"
          :key="category.id || index"
          :id="`category-${index}`"
          class="category-section"
        >
          <div class="category-title">
            <span class="title-text">{{ category.name }}</span>
          </div>
          <div class="goods-list">
            <div
              v-for="(item, itemIndex) in category.goods || []"
              :key="itemIndex"
              class="goods-item"
              @click="handleGoodsClick(item)"
            >
              <div class="goods-img">
                <img :src="item.imgUrl || '/images/goods/ml.png'" alt="商品图片" />
              </div>
              <div class="goods-info">
                <h3 class="goods-title">{{ item.title || '商品名称' }}</h3>
                <div class="goods-price">
                  <span class="price-symbol">￥</span>
                  <span class="price-value">{{ item.price || '0.00' }}</span>
                  <span class="price-unit" v-if="item.unit">/{{ item.unit }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { getCategoryListSafe, getGoodsByCategorySafe } from '@/api/list';

const activeCategory = ref(0);
const goodsContainerRef = ref(null);
const categories = ref([]);
const isScrolling = ref(false);

const generateMockData = () => {
  const mockCategories = [
    { name: '全部分类', pinyin: 'qbfl' },
    { name: '狗粮', pinyin: 'gl' },
    { name: '猫粮', pinyin: 'ml' },
    { name: '宠物玩具', pinyin: 'cwwj' },
    { name: '宠物用品', pinyin: 'cwyp' },
    { name: '宠物零食', pinyin: 'cwls' },
    { name: '宠物医疗', pinyin: 'cwyl' },
    { name: '宠物清洁', pinyin: 'cwqj' },
    { name: '宠物服饰', pinyin: 'cwfs' }
  ];

  return mockCategories.map((item, index) => ({
    id: index,
    name: item.name,
    pinyin: item.pinyin,
    goods: Array.from({ length: 6 }, (_, i) => ({
      id: `${index}-${i}`,
      title: `${item.name}商品${i + 1}`,
      price: (Math.random() * 100 + 10).toFixed(2),
      imgUrl: '/images/goods/ml.png',
      unit: index < 3 ? 'kg' : '件'
    }))
  }));
};

const fetchCategories = async () => {
  const data = await getCategoryListSafe();
  if (data && Array.isArray(data) && data.length > 0) {
    categories.value = data.map(category => ({
      ...category,
      pinyin: category.pinyin || category.name
    }));
  } else {
    categories.value = generateMockData();
  }
  await fetchGoodsForCategories();
};

const fetchGoodsForCategories = async () => {
  const goodsPromises = categories.value.map((category, index) => {
    if (category.pinyin) {
      return getGoodsByCategorySafe(category.pinyin).then(goods => ({
        index,
        goods: goods && Array.isArray(goods) ? goods : []
      }));
    }
    return Promise.resolve({ index, goods: [] });
  });
  
  const results = await Promise.all(goodsPromises);
  results.forEach(({ index, goods }) => {
    if (categories.value[index] && goods.length > 0) {
      categories.value[index].goods = goods;
    }
  });
};

const onCategoryChange = async (index) => {
  if (isScrolling.value) return;
  
  activeCategory.value = index;
  isScrolling.value = true;
  await nextTick();
  
  const targetElement = document.getElementById(`category-${index}`);
  if (targetElement && goodsContainerRef.value) {
    const container = goodsContainerRef.value;
    const targetTop = targetElement.offsetTop - container.offsetTop;
    
    container.scrollTo({
      top: targetTop,
      behavior: 'smooth'
    });
    
    setTimeout(() => {
      isScrolling.value = false;
    }, 500);
  }
};

const onScroll = () => {
  if (isScrolling.value) return;
  
  const container = goodsContainerRef.value;
  if (!container) return;
  
  const scrollTop = container.scrollTop;
  
  const categoryElements = categories.value.map((_, index) => {
    const element = document.getElementById(`category-${index}`);
    if (element) {
      const rect = element.getBoundingClientRect();
      const containerRect = container.getBoundingClientRect();
      return {
        index,
        top: rect.top - containerRect.top + scrollTop,
        bottom: rect.bottom - containerRect.top + scrollTop
      };
    }
    return null;
  }).filter(Boolean);
  
  for (let i = 0; i < categoryElements.length; i++) {
    const current = categoryElements[i];
    const next = categoryElements[i + 1];
    
    if (
      scrollTop >= current.top - 100 &&
      (!next || scrollTop < next.top - 100)
    ) {
      if (activeCategory.value !== current.index) {
        activeCategory.value = current.index;
      }
      break;
    }
  }
};

const handleGoodsClick = (item) => {
  console.log('点击商品:', item);
};

onMounted(() => {
  categories.value = generateMockData();
  fetchCategories();
});
</script>

<style lang="scss" scoped>
.list-view {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f7f7f7;
  overflow: hidden;
}

.category-container {
  flex: 1;
  display: flex;
  height: 100%;
  overflow: hidden;
}

.category-sidebar {
  width: 2.1333rem;
  background-color: #f8f8f8;
  overflow-y: auto;
  flex-shrink: 0;
}

.category-item {
  padding: 0.5333rem 0.2667rem;
  font-size: 0.3733rem;
  color: #666;
  text-align: center;
  border-left: 3px solid transparent;
  transition: all 0.3s;
  cursor: pointer;
  user-select: none;

  &:active {
    background-color: #f0f0f0;
  }

  &--active {
    color: #ff4d4f;
    background-color: #fff;
    border-left-color: #ff4d4f;
    font-weight: 600;
  }
}

.goods-container {
  flex: 1;
  overflow-y: auto;
  background-color: #fff;
  padding: 0 0.2667rem;
}

.category-section {
  margin-bottom: 0.5333rem;
}

.category-title {
  padding: 0.4rem 0.2667rem;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid #f0f0f0;

  .title-text {
    font-size: 0.4267rem;
    font-weight: 600;
    color: #333;
  }
}

.goods-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.2667rem;
  padding-bottom: 0.2667rem;
}

.goods-item {
  width: calc(50% - 0.1333rem);
  background-color: #fff;
  border-radius: 0.16rem;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;

  &:active {
    transform: scale(0.98);
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
    display: block;
  }
}

.goods-info {
  padding: 0.2667rem;
}

.goods-title {
  font-size: 0.3467rem;
  color: #333;
  margin: 0 0 0.16rem 0;
  font-weight: 500;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-word;
}

.goods-price {
  display: flex;
  align-items: baseline;
  font-size: 0.4267rem;
  color: #ff4d4f;
  font-weight: 600;

  .price-symbol {
    font-size: 0.32rem;
    margin-right: 0.0267rem;
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
</style>

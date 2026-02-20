<template>
  <div class="good-details" @scroll="handleScroll" ref="scrollContainer">
    <!-- 顶部搜索栏 -->
    <GoodDetailsTop :opacity="topOpacity" />
    
    <!-- 导航栏 -->
    <GoodDetailsNavi :opacity="naviOpacity" :active-tab="activeTab" @tab-click="handleTabClick" />

    <!-- 商品图片轮播 -->
    <van-swipe class="goods-swipe" :autoplay="3000" indicator-color="#ff6034" ref="swiperRef">
      <van-swipe-item v-for="(image, index) in goodsImages" :key="index">
        <img :src="image" class="swipe-image" />
      </van-swipe-item>
    </van-swipe>

    <!-- 价格和标题区域 -->
    <div class="price-section" ref="goodsRef">
      <div class="price-row">
        <div class="price-info">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ goodsInfo.price }}</span>
          <span class="price-decimal">.{{ goodsInfo.priceDecimal }}</span>
        </div>
      </div>
      <div class="goods-title">{{ goodsInfo.title }}</div>
    </div>

    <!-- 配送信息 -->
    <div class="delivery-section">
      <van-cell-group :border="false">
        <van-cell>
          <template #title>
            <span class="location-text">发货地：{{ goodsInfo.shippingOrigin }}</span>
            <span class="delivery-text">快递：{{ goodsInfo.postage > 0 ? `¥${goodsInfo.postage}` : '免运费' }}</span>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 服务保障 -->
    <div class="service-section">
      <van-cell :border="false">
        <template #icon>
          <van-icon name="like-o" />
        </template>
        <template #title>
          <div class="service-tags">
            <span class="service-tag">退货宝</span>
            <span class="service-tag">极速退款</span>
            <span class="service-tag">7天无理由退货</span>
          </div>
        </template>
        <template #right-icon>
          <van-icon name="arrow" />
        </template>
      </van-cell>
    </div>

    <!-- 评价区域 -->
    <div class="review-section" ref="reviewRef">
      <div class="review-header">
        <span class="review-title">评价({{ reviewCount }}+)</span>
        <button @click="showCommentForm" class="write-comment-btn">
          写评价
        </button>
      </div>
      <div class="review-tags">
        <van-tag plain type="warning">味道好吃 {{ reviewTags.taste }}</van-tag>
        <van-tag plain type="warning">用完还会再回购 {{ reviewTags.repurchase }}</van-tag>
      </div>
      <div class="review-item" v-for="review in reviews" :key="review.id">
        <div class="review-user">
          <van-image
            round
            width="32"
            height="32"
            :src="review.avatar"
          />
          <div class="user-info">
            <span class="username">{{ review.username }}</span>
            <van-rate v-model="review.star" :size="12" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
          </div>
        </div>
        <div class="review-content">{{ review.content }}</div>
        <div class="review-images" v-if="review.images && review.images.length > 0">
          <van-image
            v-for="(img, idx) in review.images"
            :key="idx"
            width="80"
            height="80"
            :src="img"
            fit="cover"
          />
        </div>
        <div class="merchant-reply" v-if="review.merchantReply">
          <div class="reply-label">商家回复：</div>
          <div class="reply-content">{{ review.merchantReply }}</div>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="detail-section" ref="detailRef">
      <div class="detail-header">宝贝详情</div>
      <div class="detail-content">
        <div v-if="goodsInfo.detail" v-html="goodsInfo.detail" class="detail-html"></div>
        <div v-else>
          <img src="/images/goods/ml.png" class="detail-image" />
          <div class="detail-text">
            <h3>优质商品</h3>
            <p>为您的爱宠提供最好的呵护</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <div class="bar-left">
        <div class="bar-icon-btn" @click="goToShop">
          <van-icon name="shop-o" size="20" />
          <span>店铺</span>
        </div>
        <div class="bar-icon-btn" @click="contactService">
          <van-icon name="chat-o" size="20" />
          <span>客服</span>
        </div>
        <div class="bar-icon-btn" @click="toggleFavorite">
          <van-icon :name="isFavorited ? 'star' : 'star-o'" :color="isFavorited ? '#ff6034' : ''" size="20" />
          <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
        </div>
      </div>
      <div class="bar-right">
        <van-button
          class="cart-btn"
          round
          color="#ffa500"
          @click="addToCart"
        >
          加入购物车
        </van-button>
        <van-button
          class="buy-btn"
          round
          color="#ff6034"
          @click="showQuantityPopup"
        >
          立即购买
        </van-button>
      </div>
    </div>

    <!-- 数量选择弹窗 -->
    <van-popup v-model:show="quantityPopupVisible" position="bottom" round>
      <div class="quantity-popup">
        <div class="popup-header">
          <span class="popup-title">选择数量</span>
          <van-icon name="cross" @click="quantityPopupVisible = false" />
        </div>
        
        <div class="popup-goods-info">
          <img :src="goodsImages[0]" class="goods-thumb" />
          <div class="goods-detail">
            <div class="goods-name">{{ goodsInfo.title }}</div>
            <div class="goods-price">¥{{ goodsInfo.price }}.{{ goodsInfo.priceDecimal }}</div>
          </div>
        </div>
        
        <div class="quantity-selector">
          <span class="selector-label">购买数量</span>
          <van-stepper v-model="selectedQuantity" :min="1" :max="999" />
        </div>
        
        <div class="popup-footer">
          <van-button type="primary" block round @click="confirmBuyNow">
            确定
          </van-button>
        </div>
      </div>
    </van-popup>

    <!-- 评论弹窗 -->
    <van-popup v-model:show="showCommentDialog" position="bottom" round :style="{ maxHeight: '80%' }">
      <div class="comment-popup">
        <div class="popup-header">
          <span class="popup-title">写评价</span>
          <van-icon name="cross" @click="showCommentDialog = false" />
        </div>
        
        <div class="comment-form">
          <div class="form-item">
            <label>评分</label>
            <van-rate v-model="commentForm.star" :size="24" color="#ffd21e" void-icon="star" void-color="#eee" />
          </div>
          
          <div class="form-item">
            <label>评价内容</label>
            <van-field
              v-model="commentForm.content"
              rows="4"
              autosize
              type="textarea"
              maxlength="500"
              placeholder="分享你的使用感受吧~"
              show-word-limit
            />
          </div>
          
          <div class="form-item">
            <label>上传图片（可选）</label>
            <van-uploader v-model="commentForm.images" multiple :max-count="6" />
          </div>
        </div>
        
        <div class="popup-footer">
          <van-button type="primary" block round @click="submitComment">
            提交评价
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showDialog } from 'vant';
import GoodDetailsTop from '@/components/good/GoodDetailsTop.vue';
import GoodDetailsNavi from '@/components/good/GoodDetailsNavi.vue';
import { getGoodDetailSafe } from '@/api/goods';
import { getCommentsByPageSafe, getCommentCountSafe, createCommentSafe } from '@/api/comment';
import { addToCartSafe } from '@/api/cart';
import { addFavoriteSafe, removeFavoriteSafe, checkFavoriteSafe } from '@/api/favorite';
import { addBrowseHistorySafe } from '@/api/browse';
import { saveScrollPosition, restoreScrollPosition } from '@/utils/scrollPosition';

const router = useRouter();
const route = useRoute();

// 滚动相关
const scrollContainer = ref(null);
const swiperRef = ref(null);
const goodsRef = ref(null);
const reviewRef = ref(null);
const detailRef = ref(null);

const topOpacity = ref(0);
const naviOpacity = ref(0);
const activeTab = ref(0);

// 轮播图高度
const swiperHeight = 375;

// 商品图片
const goodsImages = ref([
  '/images/goods/ml.png',
  '/images/goods/ml.png',
]);

// 商品信息
const goodsInfo = ref({
  id: null,
  price: '',
  priceDecimal: '',
  title: '',
  name: '',
  unit: '',
  sold: 0,
  msg: '',
  shippingOrigin: '',
  postage: 0,
  detail: ''
});

// 评价数量
const reviewCount = ref('');
const reviewLoading = ref(false);
const reviewFinished = ref(false);
const reviewCurrentPage = ref(1);
const reviewPageSize = ref(10);

// 评价标签
const reviewTags = ref({
  taste: '',
  repurchase: '',
});

// 评价列表
const reviews = ref([]);

// 收藏状态
const isFavorited = ref(false);

// 数量选择弹窗
const quantityPopupVisible = ref(false);
const selectedQuantity = ref(1);

// 评论弹窗
const showCommentDialog = ref(false);
const commentForm = ref({
  star: 5,
  content: '',
  images: []
});

// 显示评论对话框
const showCommentForm = () => {
  // 检查是否登录
  const token = localStorage.getItem('token');
  if (!token) {
    showDialog({
      title: '提示',
      message: '请先登录',
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      showCancelButton: true,
    }).then(() => {
      router.push('/my');
    }).catch(() => {});
    return;
  }
  
  // 重置表单
  commentForm.value = {
    star: 5,
    content: '',
    images: []
  };
  showCommentDialog.value = true;
};

// 提交评论
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    showToast('请输入评论内容');
    return;
  }
  
  try {
    // 处理图片URL
    const imageUrls = commentForm.value.images.map(img => {
      if (typeof img === 'string') return img;
      if (img.url) return img.url;
      if (img.content) return img.content; // base64
      return '';
    }).filter(url => url);
    
    const result = await createCommentSafe({
      commodityId: goodsInfo.value.id,
      orderId: null, // 商品详情页评论不关联订单
      star: Math.round(commentForm.value.star * 2) / 2, // 保留0.5精度
      content: commentForm.value.content,
      images: imageUrls
    });
    
    if (result !== null) {
      showToast({
        message: '评论成功',
        icon: 'success'
      });
      showCommentDialog.value = false;
      // 重新加载评论
      reviews.value = [];
      reviewCurrentPage.value = 1;
      reviewFinished.value = false;
      loadComments(goodsInfo.value.id);
      loadCommentCount(goodsInfo.value.id);
    }
  } catch (error) {
    console.error('提交评论失败:', error);
  }
};

// 底部操作栏功能
const goToShop = () => {
  showToast('店铺功能开发中');
};

const contactService = () => {
  showToast('客服功能开发中');
};

const toggleFavorite = async () => {
  // 检查是否登录
  const token = localStorage.getItem('token');
  if (!token) {
    showDialog({
      title: '提示',
      message: '请先登录',
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      showCancelButton: true,
    }).then(() => {
      router.push('/my');
    }).catch(() => {});
    return;
  }
  
  // 检查商品ID是否存在
  if (!goodsInfo.value.id) {
    showToast('商品信息加载中，请稍后');
    return;
  }
  
  // 切换收藏状态
  if (isFavorited.value) {
    // 取消收藏
    const result = await removeFavoriteSafe(goodsInfo.value.id);
    if (result !== null) {
      isFavorited.value = false;
      showToast('已取消收藏');
    }
  } else {
    // 添加收藏
    const result = await addFavoriteSafe(goodsInfo.value.id);
    if (result !== null) {
      isFavorited.value = true;
      showToast('收藏成功');
    }
  }
};

const addToCart = async () => {
  // 检查是否登录
  const token = localStorage.getItem('token');
  if (!token) {
    showDialog({
      title: '提示',
      message: '请先登录',
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      showCancelButton: true,
    }).then(() => {
      // 跳转到登录页面
      router.push('/my');
    }).catch(() => {
      // 用户取消
    });
    return;
  }
  
  // 检查商品ID是否存在
  if (!goodsInfo.value.id) {
    showToast('商品信息加载中，请稍后');
    return;
  }
  
  // 调用添加购物车接口
  const result = await addToCartSafe(goodsInfo.value.id, 1);
  
  if (result !== null) {
    showToast({
      message: '已加入购物车',
      icon: 'success'
    });
  }
};

// 显示数量选择弹窗
const showQuantityPopup = () => {
  // 检查是否登录
  const token = localStorage.getItem('token');
  if (!token) {
    showDialog({
      title: '提示',
      message: '请先登录',
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      showCancelButton: true,
    }).then(() => {
      router.push('/my');
    }).catch(() => {});
    return;
  }
  
  // 检查商品ID是否存在
  if (!goodsInfo.value.id) {
    showToast('商品信息加载中，请稍后');
    return;
  }
  
  // 重置数量并显示弹窗
  selectedQuantity.value = 1;
  quantityPopupVisible.value = true;
};

// 确认立即购买
const confirmBuyNow = async () => {
  // 关闭弹窗
  quantityPopupVisible.value = false;
  
  console.log('=== 商品详情页 - 立即购买 ===');
  console.log('1. 当前商品信息:', goodsInfo.value);
  console.log('2. 商品ID:', goodsInfo.value.id);
  
  // 检查商品ID是否存在
  if (!goodsInfo.value.id) {
    console.error('错误: 商品ID不存在');
    showToast('商品信息加载中，请稍后');
    return;
  }
  
  // 直接使用商品ID数组
  const cartItemIds = [goodsInfo.value.id];
  console.log('3. 准备跳转的 cartItemIds:', cartItemIds);
  console.log('4. JSON 序列化后:', JSON.stringify(cartItemIds));
  
  // 跳转到订单确认页（不传 from 参数，表示立即购买）
  router.push({
    path: '/order/confirm',
    query: {
      cartItemIds: JSON.stringify(cartItemIds)
      // 不传 from 参数，订单确认页会识别为立即购买
    }
  });
  
  console.log('5. 已触发路由跳转');
  console.log('=== 立即购买流程结束 ===');
};

const buyNow = async () => {
  // 这个函数已被 showQuantityPopup 替代，保留以防其他地方调用
  showQuantityPopup();
};

// 处理滚动事件
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
  
  // 计算透明度：当滚动超过轮播图高度时，透明度为1
  const opacity = Math.min(scrollTop / swiperHeight, 1);
  topOpacity.value = opacity;
  naviOpacity.value = opacity;
  
  // 根据滚动位置更新激活的标签
  updateActiveTab(scrollTop);
};

// 更新激活的标签
const updateActiveTab = (scrollTop) => {
  const goodsTop = goodsRef.value?.offsetTop || 0;
  const reviewTop = reviewRef.value?.offsetTop || 0;
  const detailTop = detailRef.value?.offsetTop || 0;
  
  // 减去固定头部的高度 (56 + 44)
  const offset = 100;
  
  if (scrollTop < reviewTop - offset) {
    activeTab.value = 0; // 宝贝
  } else if (scrollTop < detailTop - offset) {
    activeTab.value = 1; // 评价
  } else {
    activeTab.value = 2; // 详情
  }
};

// 处理标签点击
const handleTabClick = (index) => {
  activeTab.value = index;
  
  let targetRef = null;
  switch (index) {
    case 0:
      targetRef = goodsRef;
      break;
    case 1:
      targetRef = reviewRef;
      break;
    case 2:
      targetRef = detailRef;
      break;
  }
  
  if (targetRef?.value) {
    const targetTop = targetRef.value.offsetTop - 100; // 减去固定头部高度
    window.scrollTo({
      top: targetTop,
      behavior: 'smooth'
    });
  }
};

// 查看全部评价
const viewAllReviews = () => {
  showToast('查看全部评价功能开发中');
};

// 加载商品详情数据
const loadGoodsDetail = async (goodsId) => {
  try {
    // 从后端 API 加载商品详情
    const data = await getGoodDetailSafe(goodsId);
    
    if (data) {
      // 更新商品信息
      const priceStr = data.price.toString();
      const [intPart, decimalPart = '00'] = priceStr.split('.');
      
      goodsInfo.value = {
        id: data.id,
        price: intPart,
        priceDecimal: decimalPart.padEnd(2, '0'),
        title: `${data.name} ${data.unit || ''}`,
        name: data.name,
        unit: data.unit,
        sold: data.sold,
        msg: data.msg,
        shippingOrigin: data.shippingOrigin || '上海',
        postage: data.postage || 0,
        detail: data.detail || ''
      };
      
      // 更新商品图片（使用后端返回的图片列表）
      if (data.images && data.images.length > 0) {
        goodsImages.value = data.images;
      } else {
        goodsImages.value = [
          data.mainPicUrl || '/images/goods/ml.png',
          data.mainPicUrl || '/images/goods/ml.png',
        ];
      }
      
      console.log('加载商品详情:', goodsInfo.value);
      
      // 加载评论数据
      loadComments(goodsId);
      loadCommentCount(goodsId);
      
      // 检查收藏状态
      checkFavoriteStatus(goodsId);
      
      // 添加浏览记录
      addBrowseRecord(goodsId);
    } else {
      showToast('商品不存在');
    }
  } catch (error) {
    console.error('加载商品详情失败:', error);
    showToast('加载商品详情失败');
  }
};

// 检查收藏状态
const checkFavoriteStatus = async (goodsId) => {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  try {
    const result = await checkFavoriteSafe(goodsId);
    if (result !== null) {
      isFavorited.value = result;
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error);
  }
};

// 添加浏览记录
const addBrowseRecord = async (goodsId) => {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  try {
    await addBrowseHistorySafe(goodsId);
  } catch (error) {
    console.error('添加浏览记录失败:', error);
  }
};

// 加载评论列表
const loadComments = async (goodsId) => {
  if (reviewLoading.value || reviewFinished.value) return;
  
  try {
    reviewLoading.value = true;
    const pageResult = await getCommentsByPageSafe(goodsId, reviewCurrentPage.value, reviewPageSize.value);
    
    if (pageResult && pageResult.records) {
      // 追加评论数据
      const newComments = pageResult.records.map(comment => ({
        id: comment.id,
        username: comment.nickname || comment.username || '匿名用户',
        avatar: comment.avatar || '/images/default_avatar.png',
        star: comment.star || 5,
        content: comment.content,
        images: comment.images || [],
        merchantReply: comment.merchantReply,
        merchantReplyTime: comment.merchantReplyTime
      }));
      
      reviews.value = [...reviews.value, ...newComments];
      
      // 判断是否还有更多
      if (!pageResult.hasNext) {
        reviewFinished.value = true;
      } else {
        reviewCurrentPage.value++;
      }
    } else {
      reviewFinished.value = true;
    }
  } catch (error) {
    console.error('加载评论失败:', error);
  } finally {
    reviewLoading.value = false;
  }
};

// 加载评论总数
const loadCommentCount = async (goodsId) => {
  try {
    const count = await getCommentCountSafe(goodsId);
    if (count !== null) {
      reviewCount.value = count >= 1000 ? '1000+' : count.toString();
    }
  } catch (error) {
    console.error('加载评论总数失败:', error);
  }
};

// 页面加载
onMounted(() => {
  // 可以根据路由参数加载商品详情
  const goodsId = route.query.id;
  if (goodsId) {
    console.log('商品ID:', goodsId);
    loadGoodsDetail(goodsId);
  }
  
  // 监听滚动事件
  window.addEventListener('scroll', handleScroll);
  
  // 恢复滚动位置
  restoreScrollPosition(route.path);
});

// 页面卸载
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

// 页面卸载前保存滚动位置
onBeforeUnmount(() => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
});
</script>

<style scoped>
.good-details {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-top: 0;
  padding-bottom: 60px;
}

/* 商品轮播 */
.goods-swipe {
  width: 100%;
  height: 375px;
  background-color: #fff;
  margin-top: 0;
}

.swipe-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 价格区域 */
.price-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 8px;
}

.price-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.price-info {
  display: flex;
  align-items: baseline;
  color: #ff6034;
}

.price-symbol {
  font-size: 16px;
  font-weight: bold;
}

.price-value {
  font-size: 32px;
  font-weight: bold;
}

.price-decimal {
  font-size: 20px;
  font-weight: bold;
}

.goods-title {
  font-size: 16px;
  color: #333;
  line-height: 22px;
  font-weight: 500;
}

/* 配送信息 */
.delivery-section {
  background-color: #fff;
  margin-bottom: 8px;
}

.delivery-text {
  font-size: 14px;
  color: #666;
  margin-left: 12px;
}

.location-text {
  font-size: 14px;
  color: #333;
}

/* 服务保障 */
.service-section {
  background-color: #fff;
  margin-bottom: 8px;
}

.service-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.service-tag {
  font-size: 13px;
  color: #666;
}

/* 评价区域 */
.review-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.review-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.write-comment-btn {
  padding: 6px 16px;
  background: linear-gradient(135deg, #ff6034 0%, #ff8f6b 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.write-comment-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 96, 52, 0.3);
}

.review-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.review-item {
  padding: 12px 0;
  border-top: 1px solid #f5f5f5;
}

.review-user {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.user-info {
  margin-left: 8px;
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  color: #333;
}

.review-content {
  font-size: 14px;
  color: #333;
  line-height: 20px;
  margin-bottom: 8px;
}

.review-images {
  display: flex;
  gap: 8px;
}

.merchant-reply {
  margin-top: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #ff6034;
}

.reply-label {
  font-size: 12px;
  color: #ff6034;
  font-weight: 600;
  margin-bottom: 4px;
}

.reply-content {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

/* 商品详情 */
.detail-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 8px;
}

.detail-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
  text-align: center;
}

.detail-content {
  text-align: center;
}

.detail-html {
  width: 100%;
  text-align: left;
  
  :deep(img) {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 0 auto;
  }
  
  :deep(p) {
    margin: 8px 0;
    line-height: 1.6;
  }
  
  :deep(h3) {
    font-size: 16px;
    margin: 12px 0;
  }
}

.detail-image {
  width: 100%;
  max-width: 400px;
  margin-bottom: 16px;
}

.detail-text h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.detail-text p {
  font-size: 14px;
  color: #666;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  gap: 12px;
}

.bar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.bar-icon-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  gap: 2px;
}

.bar-icon-btn span {
  font-size: 11px;
  color: #666;
}

.bar-right {
  flex: 1;
  display: flex;
  gap: 8px;
}

.cart-btn,
.buy-btn {
  flex: 1;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
}

/* 数量选择弹窗 */
.quantity-popup {
  padding: 20px;
  background-color: #fff;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.popup-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.popup-goods-info {
  display: flex;
  gap: 12px;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.goods-thumb {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.goods-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-price {
  font-size: 18px;
  color: #ff6034;
  font-weight: 600;
}

.quantity-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.selector-label {
  font-size: 14px;
  color: #333;
}

.popup-footer {
  margin-top: 20px;
}

/* 评论弹窗样式 */
.comment-popup {
  padding: 20px;
  background-color: #fff;
}

.comment-form {
  margin: 20px 0;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}


.bar-icon-btn span {
  font-size: 11px;
  color: #666;
}

.bar-right {
  flex: 1;
  display: flex;
  gap: 8px;
}

.cart-btn {
  flex: 1;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
}

.buy-btn {
  flex: 1;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 375px) {
  .goods-swipe {
    height: 320px;
  }
  
  .price-value {
    font-size: 28px;
  }
}
</style>

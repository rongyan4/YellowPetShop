<template>
  <div class="order-comment-view">
    <!-- 头部 -->
    <div class="header">
      <van-icon name="arrow-left" class="back-icon" @click="goBack" />
      <h1 class="title">评价订单</h1>
    </div>

    <div class="comment-content" v-if="orderDetail">
      <!-- 订单信息 -->
      <div class="order-info">
        <div class="order-sn">订单号: {{ orderDetail.orderSn }}</div>
      </div>

      <!-- 商品评价列表 -->
      <div class="goods-comment-list">
        <div 
          v-for="(item, index) in commentItems" 
          :key="item.id"
          class="comment-item"
          :class="{ 'commented': item.commented }"
        >
          <!-- 商品信息 - 可点击跳转 -->
          <div class="goods-info" @click="goToGoodDetail(item.commodityId)">
            <img :src="item.commodityPic" :alt="item.commodityName" class="goods-image">
            <div class="goods-detail">
              <div class="goods-name">{{ item.commodityName }}</div>
              <div class="goods-price">¥{{ item.commodityPrice }}</div>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <!-- 已评论状态 -->
          <div v-if="item.commented" class="commented-section">
            <!-- 评分（只读） -->
            <div class="rating-section readonly">
              <label class="section-label">商品评分</label>
              <div class="star-rating">
                <div class="stars">
                  <span 
                    v-for="i in 5" 
                    :key="i"
                    class="star-wrapper"
                  >
                    <svg 
                      v-if="getStarType(item.star, i) === 'full'"
                      class="star full" 
                      viewBox="0 0 24 24"
                    >
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" fill="#ffd21e"/>
                    </svg>
                    <svg 
                      v-else-if="getStarType(item.star, i) === 'half'"
                      class="star half" 
                      viewBox="0 0 24 24"
                    >
                      <defs>
                        <linearGradient :id="'half-' + index + '-' + i">
                          <stop offset="50%" stop-color="#ffd21e"/>
                          <stop offset="50%" stop-color="#eee"/>
                        </linearGradient>
                      </defs>
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" :fill="'url(#half-' + index + '-' + i + ')'"/>
                    </svg>
                    <svg 
                      v-else
                      class="star empty" 
                      viewBox="0 0 24 24"
                    >
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" fill="#eee"/>
                    </svg>
                  </span>
                </div>
                <span class="rating-text">{{ getRatingText(item.star) }}</span>
              </div>
            </div>

            <!-- 评价内容（只读） -->
            <div class="content-section readonly">
              <label class="section-label">评价内容</label>
              <div class="content-display">{{ item.content }}</div>
            </div>

            <!-- 评价图片（只读） -->
            <div v-if="item.images && item.images.length > 0" class="image-section readonly">
              <label class="section-label">评价图片</label>
              <div class="image-preview-list">
                <img 
                  v-for="(img, imgIndex) in item.images" 
                  :key="imgIndex"
                  :src="img" 
                  class="preview-image"
                  @click="previewImage(item.images, imgIndex)"
                />
              </div>
            </div>

            <!-- 查看评论按钮 -->
            <div class="view-comment-section">
              <van-button 
                type="primary" 
                block 
                round
                @click="viewCommentDetail(item.commodityId)"
              >
                查看评论
              </van-button>
            </div>
          </div>

          <!-- 未评论状态 -->
          <div v-else class="uncommented-section">
            <!-- 评分 -->
            <div class="rating-section">
              <label class="section-label">商品评分</label>
              <div class="star-rating">
                <div class="stars" @mouseleave="handleMouseLeave(index)">
                  <span 
                    v-for="i in 5" 
                    :key="i"
                    class="star-wrapper"
                    @mouseenter="handleStarHover(index, i)"
                    @click="handleStarClick(index, i)"
                  >
                    <svg 
                      v-if="getStarType(item.star, i) === 'full'"
                      class="star full" 
                      viewBox="0 0 24 24"
                    >
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" fill="#ffd21e"/>
                    </svg>
                    <svg 
                      v-else-if="getStarType(item.star, i) === 'half'"
                      class="star half" 
                      viewBox="0 0 24 24"
                    >
                      <defs>
                        <linearGradient :id="'half-edit-' + index + '-' + i">
                          <stop offset="50%" stop-color="#ffd21e"/>
                          <stop offset="50%" stop-color="#eee"/>
                        </linearGradient>
                      </defs>
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" :fill="'url(#half-edit-' + index + '-' + i + ')'"/>
                    </svg>
                    <svg 
                      v-else
                      class="star empty" 
                      viewBox="0 0 24 24"
                    >
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" fill="#eee"/>
                    </svg>
                  </span>
                </div>
                <span class="rating-text">{{ getRatingText(item.star) }}</span>
              </div>
            </div>

            <!-- 评价内容 -->
            <div class="content-section">
              <label class="section-label">评价内容</label>
              <van-field
                v-model="item.content"
                rows="4"
                autosize
                type="textarea"
                maxlength="500"
                placeholder="分享你的使用感受吧~"
                show-word-limit
              />
            </div>

            <!-- 上传图片 -->
            <div class="image-section">
              <label class="section-label">上传图片（可选）</label>
              <van-uploader 
                v-model="item.fileList" 
                multiple 
                :max-count="6"
                :after-read="(file) => handleImageUpload(file, index)"
                :before-delete="() => handleImageDelete(index)"
              >
                <template #preview-cover="{ file }">
                  <div class="preview-cover">
                    <van-icon name="eye-o" @click.stop="previewSingleImage(file)" />
                  </div>
                </template>
              </van-uploader>
            </div>

            <!-- 提交按钮 -->
            <div class="submit-section">
              <van-button 
                type="primary" 
                block 
                round
                :loading="item.submitting"
                @click="submitComment(index)"
              >
                确认评价
              </van-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 全部评价完成提示 -->
      <div v-if="allSubmitted" class="all-done">
        <div class="done-icon">
          <van-icon name="checked" size="64" color="#4caf50" />
        </div>
        <div class="done-text">所有商品已评价完成</div>
        <van-button type="primary" round @click="goBack">返回订单列表</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showImagePreview } from 'vant';
import { getOrderDetailSafe } from '@/api/order';
import { createCommentSafe, uploadCommentImageSafe, getOrderCommentStatusSafe } from '@/api/comment';
import { saveScrollPosition } from '@/utils/scrollPosition';

const router = useRouter();
const route = useRoute();

// 订单详情
const orderDetail = ref(null);

// 评价项列表
const commentItems = ref([]);

// 是否全部提交
const allSubmitted = computed(() => {
  return commentItems.value.length > 0 && commentItems.value.every(item => item.commented);
});

// 获取星星类型
const getStarType = (rating, position) => {
  const diff = rating - (position - 1);
  if (diff >= 1) return 'full';
  if (diff >= 0.5) return 'half';
  return 'empty';
};

// 获取评分文本
const getRatingText = (rating) => {
  if (rating >= 4.5) return '非常满意';
  if (rating >= 3.5) return '满意';
  if (rating >= 2.5) return '一般';
  if (rating >= 1.5) return '不满意';
  return '非常不满意';
};

// 处理星星悬停
const handleStarHover = (itemIndex, starPosition) => {
  const item = commentItems.value[itemIndex];
  if (!item.commented) {
    item.hoverStar = starPosition;
  }
};

// 处理鼠标离开
const handleMouseLeave = (itemIndex) => {
  const item = commentItems.value[itemIndex];
  if (!item.commented) {
    item.hoverStar = 0;
  }
};

// 处理星星点击
const handleStarClick = (itemIndex, starPosition) => {
  const item = commentItems.value[itemIndex];
  if (item.commented) return;
  
  if (Math.ceil(item.star) === starPosition && item.star % 1 === 0) {
    item.star = starPosition - 0.5;
  } else {
    item.star = starPosition;
  }
  
  if (item.star < 0.5) {
    item.star = 0.5;
  }
};

// 处理图片上传
const handleImageUpload = async (file, itemIndex) => {
  const item = commentItems.value[itemIndex];
  
  try {
    // 创建FormData
    const formData = new FormData();
    formData.append('file', file.file);
    
    // 上传图片
    const result = await uploadCommentImageSafe(formData);
    
    if (result && result.imageUrl) {
      // 保存图片URL
      if (!item.uploadedImages) {
        item.uploadedImages = [];
      }
      item.uploadedImages.push(result.imageUrl);
      
      console.log('图片上传成功:', result.imageUrl);
    } else {
      showToast('图片上传失败');
      // 移除上传失败的文件
      const fileIndex = item.fileList.findIndex(f => f.file === file.file);
      if (fileIndex > -1) {
        item.fileList.splice(fileIndex, 1);
      }
    }
  } catch (error) {
    console.error('图片上传失败:', error);
    showToast('图片上传失败');
    // 移除上传失败的文件
    const fileIndex = item.fileList.findIndex(f => f.file === file.file);
    if (fileIndex > -1) {
      item.fileList.splice(fileIndex, 1);
    }
  }
};

// 处理图片删除
const handleImageDelete = (itemIndex) => {
  const item = commentItems.value[itemIndex];
  // 清空已上传的图片URL列表
  if (item.uploadedImages) {
    item.uploadedImages = [];
  }
  return true;
};

// 预览单张图片
const previewSingleImage = (file) => {
  showImagePreview({
    images: [file.content || file.url],
    startPosition: 0,
  });
};

// 预览图片列表
const previewImage = (images, startIndex) => {
  showImagePreview({
    images: images,
    startPosition: startIndex,
  });
};

// 提交评价
const submitComment = async (itemIndex) => {
  const item = commentItems.value[itemIndex];
  
  // 验证
  if (!item.content.trim()) {
    showToast('请输入评价内容');
    return;
  }
  
  if (item.star < 0.5) {
    showToast('请选择评分');
    return;
  }
  
  try {
    item.submitting = true;
    
    // 使用已上传的图片URL
    const imageUrls = item.uploadedImages || [];
    
    const result = await createCommentSafe({
      commodityId: item.commodityId,
      orderId: orderDetail.value.id,
      star: Math.round(item.star * 2) / 2,
      content: item.content,
      images: imageUrls
    });
    
    if (result !== null) {
      showToast({
        message: '评价成功',
        icon: 'success'
      });
      
      // 标记为已评论
      item.commented = true;
      item.images = imageUrls;
    }
  } catch (error) {
    console.error('提交评价失败:', error);
  } finally {
    item.submitting = false;
  }
};

// 查看评论详情
const viewCommentDetail = (commodityId) => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/good-details?id=${commodityId}`);
};

// 跳转到商品详情
const goToGoodDetail = (commodityId) => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/good-details?id=${commodityId}`);
};

// 加载订单详情
const loadOrderDetail = async () => {
  const orderId = route.params.id || route.query.orderId;
  if (!orderId) {
    showToast('订单ID不存在');
    router.back();
    return;
  }
  
  const data = await getOrderDetailSafe(orderId);
  if (data) {
    orderDetail.value = data;
    
    // 获取评论状态
    const statusList = await getOrderCommentStatusSafe(orderId);
    
    // 初始化评价项
    commentItems.value = data.items.map(item => {
      // 查找该商品的评论状态
      const status = statusList?.find(s => s.commodityId === item.commodityId);
      
      if (status && status.commented) {
        // 已评论
        return {
          id: item.id,
          commodityId: item.commodityId,
          commodityName: item.commodityName,
          commodityPic: item.commodityPic,
          commodityPrice: item.commodityPrice,
          commented: true,
          star: status.star || 5,
          content: status.content || '',
          images: status.images || [],
          createTime: status.createTime
        };
      } else {
        // 未评论
        return {
          id: item.id,
          commodityId: item.commodityId,
          commodityName: item.commodityName,
          commodityPic: item.commodityPic,
          commodityPrice: item.commodityPrice,
          commented: false,
          star: 5,
          hoverStar: 0,
          content: '',
          fileList: [],
          uploadedImages: [],
          submitting: false
        };
      }
    });
  } else {
    showToast('加载订单详情失败');
    router.back();
  }
};

// 返回
const goBack = () => {
  router.back();
};

onMounted(() => {
  loadOrderDetail();
});
</script>

<style scoped>
.order-comment-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

/* 头部 */
.header {
  position: sticky;
  top: 0;
  background-color: #fff;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.back-icon {
  font-size: 20px;
  color: #333;
  cursor: pointer;
  margin-right: 12px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 评价内容 */
.comment-content {
  padding: 12px;
}

/* 订单信息 */
.order-info {
  background-color: #fff;
  padding: 12px 16px;
  border-radius: 12px;
  margin-bottom: 12px;
}

.order-sn {
  font-size: 13px;
  color: #666;
}

/* 商品评价列表 */
.goods-comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 评价区域 */
.comment-item {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.comment-item.commented {
  background-color: #f0f9ff;
  border: 1px solid #e0f2fe;
}

/* 商品信息 */
.goods-info {
  display: flex;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
  margin-bottom: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  padding: 8px;
  margin: -8px -8px 16px -8px;
  border-radius: 8px;
}

.goods-info:hover {
  background-color: rgba(0, 0, 0, 0.02);
}

.goods-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  background-color: #f5f5f5;
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
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 600;
}

.arrow-icon {
  align-self: center;
  color: #999;
  font-size: 16px;
}

/* 评分区域 */
.rating-section {
  margin-bottom: 16px;
}

.rating-section.readonly .stars {
  pointer-events: none;
}

.section-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.star-rating {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stars {
  display: flex;
  gap: 4px;
}

.star-wrapper {
  cursor: pointer;
  display: inline-block;
  transition: transform 0.2s;
}

.star-wrapper:hover {
  transform: scale(1.1);
}

.rating-section.readonly .star-wrapper {
  cursor: default;
}

.rating-section.readonly .star-wrapper:hover {
  transform: none;
}

.star {
  width: 32px;
  height: 32px;
  transition: all 0.2s;
}

.star.full {
  filter: drop-shadow(0 2px 4px rgba(255, 210, 30, 0.3));
}

.rating-text {
  font-size: 14px;
  color: #ffd21e;
  font-weight: 500;
}

/* 评价内容 */
.content-section {
  margin-bottom: 16px;
}

.content-section.readonly {
  margin-bottom: 16px;
}

.content-display {
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  min-height: 80px;
}

/* 图片上传 */
.image-section {
  margin-bottom: 16px;
}

.image-section.readonly {
  margin-bottom: 16px;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.preview-image:hover {
  transform: scale(1.05);
}

.preview-cover {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.3);
  color: #fff;
  font-size: 20px;
}

/* 提交按钮 */
.submit-section {
  margin-top: 20px;
}

/* 查看评论按钮 */
.view-comment-section {
  margin-top: 20px;
}

/* 全部完成 */
.all-done {
  background-color: #fff;
  border-radius: 12px;
  padding: 48px 24px;
  text-align: center;
}

.done-icon {
  margin-bottom: 24px;
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.done-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 32px;
}
</style>

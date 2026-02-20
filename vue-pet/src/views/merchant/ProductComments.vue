<template>
  <div class="product-comments">
    <!-- 返回按钮和标题 -->
    <div class="header">
      <button @click="goBack" class="back-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        返回
      </button>
      <h2>商品评论管理</h2>
    </div>

    <!-- 评论列表 -->
    <div class="comments-wrapper">
      <div v-if="commentList.length > 0" class="comments-list">
        <div v-for="comment in commentList" :key="comment.id" :class="['comment-card', { 'topped': comment.isTop }]">
          <div class="comment-header">
            <div class="user-info">
              <img :src="comment.avatar || '/images/default-avatar.png'" :alt="comment.nickname" class="avatar" />
              <div class="user-details">
                <span class="nickname">{{ comment.nickname || comment.username }}</span>
                <div class="rating">
                  <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" :class="i <= comment.star ? 'star-filled' : 'star-empty'">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                </div>
              </div>
            </div>
            <div class="comment-actions">
              <button 
                @click="handleTop(comment)" 
                :class="comment.isTop ? 'btn-topped' : 'btn-top'"
              >
                {{ comment.isTop ? '已置顶' : '置顶' }}
              </button>
              <button @click="handleReply(comment)" class="btn-reply">回复</button>
              <button @click="handleDelete(comment.id)" class="btn-delete">删除</button>
            </div>
          </div>
          
          <div class="comment-content">
            <p>{{ comment.content }}</p>
            <div v-if="comment.images && comment.images.length > 0" class="comment-images">
              <img v-for="(img, idx) in comment.images" :key="idx" :src="img" :alt="`评论图片${idx + 1}`" />
            </div>
          </div>
          
          <div class="comment-footer">
            <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
            <div class="comment-stats">
              <span>👍 {{ comment.likeCount || 0 }}</span>
              <span>💬 {{ comment.replyCount || 0 }}</span>
            </div>
          </div>
          
          <!-- 商家回复 -->
          <div v-if="comment.merchantReply" class="merchant-reply">
            <div class="reply-label">商家回复：</div>
            <div class="reply-content">{{ comment.merchantReply }}</div>
            <div class="reply-time">{{ formatDate(comment.merchantReplyTime) }}</div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        </svg>
        <p>暂无评论</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage === 1" class="page-btn">
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage === totalPages" class="page-btn">
        下一页
      </button>
    </div>

    <!-- 回复对话框 -->
    <van-popup v-model:show="showReplyDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>回复评论</h3>
          <button @click="showReplyDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="original-comment">
            <div class="original-label">原评论：</div>
            <div class="original-content">{{ currentComment?.content }}</div>
          </div>
          <div class="form-group">
            <label>回复内容</label>
            <textarea v-model="replyContent" placeholder="请输入回复内容..." rows="4"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showReplyDialog = false" class="btn-cancel">取消</button>
          <button @click="confirmReply" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getProductComments, replyComment, deleteComment, topComment } from '@/api/merchant';
import { showConfirmDialog, showSuccessToast, showFailToast } from 'vant';

const router = useRouter();
const route = useRoute();

const productId = ref(route.params.id);
const commentList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(0);

const showReplyDialog = ref(false);
const currentComment = ref(null);
const replyContent = ref('');

const loadComments = async () => {
  try {
    const result = await getProductComments(productId.value, {
      page: currentPage.value,
      size: pageSize.value
    });

    if (result && result.code === 200) {
      commentList.value = result.data.records || [];
      total.value = result.data.total || 0;
      totalPages.value = result.data.pages || 0;
    } else {
      showFailToast(result.msg || '加载失败');
    }
  } catch (error) {
    console.error('加载评论失败:', error);
    showFailToast('加载失败');
  }
};

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    loadComments();
  }
};

const handleReply = (comment) => {
  currentComment.value = comment;
  replyContent.value = '';
  showReplyDialog.value = true;
};

const confirmReply = async () => {
  if (!replyContent.value.trim()) {
    showFailToast('请输入回复内容');
    return;
  }

  try {
    const result = await replyComment({
      commentId: currentComment.value.id,
      reply: replyContent.value
    });

    if (result && result.code === 200) {
      showSuccessToast('回复成功');
      showReplyDialog.value = false;
      loadComments();
    } else {
      showFailToast(result.msg || '回复失败');
    }
  } catch (error) {
    showFailToast('回复失败');
  }
};

const handleDelete = (commentId) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这条评论吗？',
    confirmButtonColor: '#ff6b6b'
  }).then(async () => {
    try {
      const result = await deleteComment(commentId);
      if (result && result.code === 200) {
        showSuccessToast('删除成功');
        loadComments();
      } else {
        showFailToast(result.msg || '删除失败');
      }
    } catch (error) {
      showFailToast('删除失败');
    }
  }).catch(() => {});
};

const handleTop = async (comment) => {
  const newTopStatus = !comment.isTop;
  const action = newTopStatus ? '置顶' : '取消置顶';
  
  try {
    const result = await topComment(comment.id, newTopStatus);
    if (result && result.code === 200) {
      showSuccessToast(`${action}成功`);
      loadComments();
    } else {
      showFailToast(result.msg || `${action}失败`);
    }
  } catch (error) {
    showFailToast(`${action}失败`);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  loadComments();
});
</script>

<style scoped>
.product-comments {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  color: #636e72;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  border-color: #98D8C8;
  color: #98D8C8;
}

.back-btn svg {
  width: 18px;
  height: 18px;
  stroke-width: 2;
}

.header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.comments-wrapper {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-card {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.2s;
}

.comment-card.topped {
  background: #fffacd;
}

.comment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  gap: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nickname {
  font-size: 15px;
  font-weight: 600;
  color: #2d3436;
}

.rating {
  display: flex;
  gap: 4px;
}

.rating svg {
  width: 16px;
  height: 16px;
}

.star-filled {
  fill: #ffd700;
  stroke: #ffd700;
}

.star-empty {
  fill: none;
  stroke: #dfe6e9;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.btn-top,
.btn-topped,
.btn-reply,
.btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-top {
  background: #fff3cd;
  color: #856404;
}

.btn-top:hover {
  background: #ffeaa7;
}

.btn-topped {
  background: #d4edda;
  color: #155724;
}

.btn-topped:hover {
  background: #c3e6cb;
}

.btn-reply {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-reply:hover {
  background: #bbdefb;
}

.btn-delete {
  background: #ffebee;
  color: #c62828;
}

.btn-delete:hover {
  background: #ffcdd2;
}

.comment-content {
  margin-bottom: 12px;
}

.comment-content p {
  font-size: 14px;
  line-height: 1.6;
  color: #2d3436;
  margin: 0 0 12px 0;
}

.comment-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.comment-images img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  transition: all 0.2s;
}

.comment-images img:hover {
  transform: scale(1.05);
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e8e8e8;
}

.comment-time {
  font-size: 13px;
  color: #95a5a6;
}

.comment-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #636e72;
}

.merchant-reply {
  margin-top: 12px;
  padding: 12px;
  background: #e8f5e9;
  border-left: 3px solid #4caf50;
  border-radius: 6px;
}

.reply-label {
  font-size: 13px;
  font-weight: 600;
  color: #2e7d32;
  margin-bottom: 6px;
}

.reply-content {
  font-size: 14px;
  color: #2d3436;
  line-height: 1.5;
  margin-bottom: 6px;
}

.reply-time {
  font-size: 12px;
  color: #66bb6a;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #95a5a6;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  stroke-width: 1.5;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 20px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  border-color: #98D8C8;
  color: #98D8C8;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #636e72;
}

/* 对话框样式 */
.dialog {
  padding: 24px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dialog-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  font-size: 24px;
  color: #636e72;
  cursor: pointer;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #e9ecef;
}

.dialog-body {
  margin-bottom: 24px;
}

.original-comment {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.original-label {
  font-size: 13px;
  font-weight: 600;
  color: #636e72;
  margin-bottom: 6px;
}

.original-content {
  font-size: 14px;
  color: #2d3436;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
  margin-bottom: 8px;
}

.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  resize: vertical;
  font-family: inherit;
}

.form-group textarea:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel,
.btn-confirm {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f8f9fa;
  color: #636e72;
}

.btn-cancel:hover {
  background: #e9ecef;
}

.btn-confirm {
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  color: white;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(152, 216, 200, 0.3);
}
</style>

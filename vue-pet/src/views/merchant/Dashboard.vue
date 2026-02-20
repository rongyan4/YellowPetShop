<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.totalProductCount || 0 }}</div>
          <div class="stat-title">商品总数</div>
          <div class="stat-detail">上架: {{ dashboard.onSaleProductCount || 0 }} | 下架: {{ dashboard.offSaleProductCount || 0 }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.todayOrderCount || 0 }}</div>
          <div class="stat-title">今日订单</div>
          <div class="stat-detail">销售额: ¥{{ dashboard.todaySales || 0 }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M9 11H3v10h6V11z M21 11h-6v10h6V11z M15 3H9v6h6V3z"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.totalOrderCount || 0 }}</div>
          <div class="stat-title">订单总数</div>
          <div class="stat-detail">总销售额: ¥{{ dashboard.totalSales || 0 }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M23 21v-2a4 4 0 0 0-3-3.87 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.totalCommentCount || 0 }}</div>
          <div class="stat-title">评论总数</div>
          <div class="stat-detail">待回复: {{ dashboard.toReplyCommentCount || 0 }}</div>
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <div class="action-card">
        <div class="action-header">
          <h3>待处理事项</h3>
        </div>
        <div class="action-list">
          <div class="action-item" @click="goToOrders('pending')">
            <div class="action-icon" style="background: #fff3cd;">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" style="color: #856404;">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
            </div>
            <div class="action-info">
              <div class="action-title">待支付订单</div>
              <div class="action-count">{{ dashboard.pendingOrderCount || 0 }} 个</div>
            </div>
          </div>

          <div class="action-item" @click="goToOrders('paid')">
            <div class="action-icon" style="background: #d4edda;">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" style="color: #155724;">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
              </svg>
            </div>
            <div class="action-info">
              <div class="action-title">待发货订单</div>
              <div class="action-count">{{ dashboard.toShipOrderCount || 0 }} 个</div>
            </div>
          </div>

          <div class="action-item">
            <div class="action-icon" style="background: #cce5ff;">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" style="color: #004085;">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
            </div>
            <div class="action-info">
              <div class="action-title">待回复评论</div>
              <div class="action-count">{{ dashboard.toReplyCommentCount || 0 }} 条</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="welcome-section">
      <div class="welcome-card">
        <h2 class="welcome-title">欢迎使用商家管理系统</h2>
        <p class="welcome-text">这是一个功能完善的商家后台管理系统，您可以在这里管理商品、会员和订单。</p>
        <div class="feature-list">
          <div class="feature-item">
            <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
            <span>商品上下架管理</span>
          </div>
          <div class="feature-item">
            <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
            <span>订单改价与发货</span>
          </div>
          <div class="feature-item">
            <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
            <span>评论管理与回复</span>
          </div>
          <div class="feature-item">
            <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
            <span>实时数据统计</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getDashboard } from '@/api/merchant';
import { showToast } from 'vant';

const router = useRouter();

const dashboard = ref({
  todayOrderCount: 0,
  todaySales: 0,
  pendingOrderCount: 0,
  toShipOrderCount: 0,
  totalProductCount: 0,
  onSaleProductCount: 0,
  offSaleProductCount: 0,
  totalSales: 0,
  totalOrderCount: 0,
  toCommentOrderCount: 0,
  totalCommentCount: 0,
  toReplyCommentCount: 0
});

const loadDashboard = async () => {
  try {
    const response = await getDashboard();
    if (response && response.code === 200) {
      dashboard.value = response.data;
    }
  } catch (error) {
    console.error('加载数据概览失败:', error);
    showToast('加载数据失败');
  }
};

const goToOrders = (status) => {
  router.push({
    path: '/merchant/orders',
    query: { status }
  });
};

onMounted(() => {
  loadDashboard();
});
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
  stroke-width: 2;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #2d3436;
  margin-bottom: 4px;
}

.stat-title {
  font-size: 14px;
  color: #636e72;
  margin-bottom: 4px;
}

.stat-detail {
  font-size: 12px;
  color: #95a5a6;
}

.quick-actions {
  margin-bottom: 24px;
}

.action-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.action-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2d3436;
  margin: 0 0 20px 0;
}

.action-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon svg {
  width: 24px;
  height: 24px;
  stroke-width: 2;
}

.action-info {
  flex: 1;
}

.action-title {
  font-size: 14px;
  color: #636e72;
  margin-bottom: 4px;
}

.action-count {
  font-size: 20px;
  font-weight: 700;
  color: #2d3436;
}

.welcome-section {
  margin-top: 24px;
}

.welcome-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3436;
  margin: 0 0 16px 0;
}

.welcome-text {
  font-size: 16px;
  color: #636e72;
  line-height: 1.6;
  margin: 0 0 32px 0;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  font-size: 15px;
  color: #2d3436;
}

.feature-icon {
  width: 20px;
  height: 20px;
  stroke-width: 2.5;
  color: #98D8C8;
  flex-shrink: 0;
}
</style>

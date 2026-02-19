<template>
  <div class="account-manage-view">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <div class="nav-left" @click="goBack">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" fill="none"/>
        </svg>
      </div>
      <div class="nav-title">账号管理</div>
      <div class="nav-right">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <circle cx="12" cy="6" r="1.5" fill="currentColor"/>
          <circle cx="12" cy="12" r="1.5" fill="currentColor"/>
          <circle cx="12" cy="18" r="1.5" fill="currentColor"/>
        </svg>
      </div>
    </div>

    <!-- 账号信息列表 -->
    <div class="account-list">
      <div class="account-item">
        <span class="account-label">邮箱</span>
        <div class="account-value-wrapper">
          <span class="account-value">{{ maskedEmail }}</span>
          <button class="change-btn" @click="changeEmail">更换邮箱</button>
        </div>
      </div>

      <div class="account-item" @click="cancelAccount">
        <span class="account-label">注销账号</span>
        <div class="account-value-wrapper">
          <span class="account-warning">注销后无法恢复，请谨慎操作</span>
          <i class="arrow-right">›</i>
        </div>
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <div class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>

    <!-- 确认退出弹窗 -->
    <div class="modal-overlay" v-if="showLogoutModal" @click="closeLogoutModal">
      <div class="modal-content" @click.stop>
        <div class="modal-title">确认退出登录？</div>
        <div class="modal-buttons">
          <button class="modal-btn cancel-btn" @click="closeLogoutModal">取消</button>
          <button class="modal-btn confirm-btn" @click="confirmLogout">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

// 退出登录弹窗控制
const showLogoutModal = ref(false);

// 用户邮箱（脱敏显示）
const maskedEmail = computed(() => {
  const email = userStore.userInfo?.email || userStore.userInfo?.username || '180****5451';
  
  // 如果是邮箱格式，进行脱敏处理
  if (email.includes('@')) {
    const [localPart, domain] = email.split('@');
    if (localPart.length <= 3) {
      return `${localPart[0]}***@${domain}`;
    }
    const visibleStart = localPart.slice(0, 2);
    const visibleEnd = localPart.slice(-1);
    return `${visibleStart}***${visibleEnd}@${domain}`;
  }
  
  // 如果是手机号格式，进行脱敏处理
  if (/^\d+$/.test(email) && email.length === 11) {
    return `${email.slice(0, 3)}****${email.slice(-4)}`;
  }
  
  return email;
});

// 返回上一页
const goBack = () => {
  router.back();
};

// 更换邮箱
const changeEmail = () => {
  console.log('更换邮箱');
  // TODO: 实现更换邮箱功能
};

// 注销账号
const cancelAccount = () => {
  console.log('注销账号');
  // TODO: 实现注销账号功能
};

// 点击退出登录按钮
const handleLogout = () => {
  showLogoutModal.value = true;
};

// 关闭退出登录弹窗
const closeLogoutModal = () => {
  showLogoutModal.value = false;
};

// 确认退出登录
const confirmLogout = () => {
  // 清除用户登录信息
  userStore.logout();
  
  // 关闭弹窗
  showLogoutModal.value = false;
  
  // 跳转到"我的"页面
  router.replace('/my');
};
</script>

<style scoped>
.account-manage-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 顶部导航栏 */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 100;
  border-bottom: 1px solid #f0f0f0;
}

.nav-left,
.nav-right {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #333;
}

.nav-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

/* 账号信息列表 */
.account-list {
  margin-top: 56px;
  background-color: #fff;
}

.account-item {
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.account-item:active {
  background-color: #f8f8f8;
}

.account-item:last-child {
  border-bottom: none;
}

.account-label {
  font-size: 16px;
  color: #333;
  font-weight: 400;
}

.account-value-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.account-value {
  font-size: 16px;
  color: #666;
}

.change-btn {
  padding: 6px 16px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 20px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.change-btn:hover {
  background-color: #f8f8f8;
}

.change-btn:active {
  transform: scale(0.95);
}

.account-warning {
  font-size: 14px;
  color: #999;
}

.arrow-right {
  font-size: 20px;
  color: #ccc;
  font-style: normal;
  font-weight: 300;
}

/* 退出登录按钮 */
.logout-section {
  padding: 40px 20px;
}

.logout-btn {
  width: 100%;
  height: 50px;
  background-color: #000;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background-color: #333;
}

.logout-btn:active {
  transform: scale(0.98);
}

/* 确认退出弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  width: 80%;
  max-width: 320px;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  text-align: center;
  margin-bottom: 24px;
}

.modal-buttons {
  display: flex;
  gap: 12px;
}

.modal-btn {
  flex: 1;
  height: 44px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background-color: #e5e5e5;
}

.confirm-btn {
  background-color: #000;
  color: #fff;
}

.confirm-btn:hover {
  background-color: #333;
}

.modal-btn:active {
  transform: scale(0.95);
}

/* 响应式设计 */
@media (max-width: 375px) {
  .modal-content {
    width: 85%;
  }
}
</style>

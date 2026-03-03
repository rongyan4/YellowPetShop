<template>
  <div class="profile-view">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <div class="nav-left" @click="goBack">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" fill="none"/>
        </svg>
      </div>
      <div class="nav-title">个人资料</div>
      <div class="nav-right"></div>
    </div>

    <!-- 头像区域 - 使用 Uploader 自定义预览样式 -->
    <div class="avatar-section">
      <van-uploader
        :after-read="handleAvatarUpload"
        :max-size="2 * 1024 * 1024"
        @oversize="onOversize"
        accept="image/*"
        :max-count="1"
        :deletable="false"
      >
        <template #default>
          <div class="avatar-container">
            <img :src="userAvatar" alt="头像" class="avatar-image">
            <div class="camera-icon">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <circle cx="12" cy="13" r="3" fill="white"/>
                <path d="M9 3L7.5 5H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2h-3.5L15 3H9z" fill="white"/>
              </svg>
            </div>
          </div>
        </template>
      </van-uploader>
    </div>

    <!-- 个人信息列表 -->
    <div class="info-list">
      <div class="info-item" @click="editNickname">
        <span class="info-label">昵称</span>
        <div class="info-value-wrapper">
          <span class="info-value">{{ userInfo.nickname || '未设置' }}</span>
          <i class="arrow-right">›</i>
        </div>
      </div>

      <div class="info-item" @click="editGender">
        <span class="info-label">性别</span>
        <div class="info-value-wrapper">
          <span class="info-value">{{ userInfo.gender || '未设置' }}</span>
          <i class="arrow-right">›</i>
        </div>
      </div>

      <div class="info-item" @click="editBirthday">
        <span class="info-label">生日</span>
        <div class="info-value-wrapper">
          <span class="info-value" :class="{ placeholder: !userInfo.birthday }">
            {{ userInfo.birthday || '填写生日，尊享生日权益' }}
          </span>
          <i class="arrow-right">›</i>
        </div>
      </div>

      <div class="info-item">
        <span class="info-label">加入时间</span>
        <div class="info-value-wrapper">
          <span class="info-value">
            {{ joinDateText }}
          </span>
        </div>
      </div>

      <div class="info-item" @click="editAddress">
        <span class="info-label">收货地址</span>
        <div class="info-value-wrapper">
          <i class="arrow-right">›</i>
        </div>
      </div>

      <div class="info-item" @click="goToAccountManage">
        <span class="info-label">账号管理</span>
        <div class="info-value-wrapper">
          <i class="arrow-right">›</i>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <EditNicknameModal 
      v-model="showNicknameModal"
      :initial-value="userInfo.nickname || ''"
      @confirm="handleNicknameConfirm"
    />
    <EditGenderModal 
      v-model="showGenderModal"
      :initial-value="userInfo.gender || '男'"
      @confirm="handleGenderConfirm"
    />
    <EditBirthdayModal 
      v-model="showBirthdayModal"
      :initial-value="userInfo.birthday || ''"
      @confirm="handleBirthdayConfirm"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { getCurrentUserInfo, updateUserInfo, uploadAvatar } from '@/api/user';
import { showLoadingToast, closeToast, showSuccessToast, showFailToast } from 'vant';
import EditNicknameModal from '@/components/profile/EditNicknameModal.vue';
import EditGenderModal from '@/components/profile/EditGenderModal.vue';
import EditBirthdayModal from '@/components/profile/EditBirthdayModal.vue';

const router = useRouter();
const userStore = useUserStore();

// 用户信息
const userInfo = computed(() => userStore.userInfo || {});

// 加入时间展示（仅展示日期）
const joinDateText = computed(() => {
  const ct = userInfo.value.createTime;
  if (!ct) {
    return '未知';
  }
  const d = new Date(ct);
  if (Number.isNaN(d.getTime())) {
    return ct;
  }
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
});

// 用户头像
const userAvatar = computed(() => {
  // 如果有头像URL则使用，否则使用默认头像（前端 public 目录）
  const avatar = userInfo.value.avatar || '/images/default_avatar.png';
  // 添加时间戳避免浏览器缓存
  if (avatar.startsWith('/api/images/')) {
    return `${avatar}?t=${Date.now()}`;
  }
  return avatar;
});

// 弹窗显示状态
const showNicknameModal = ref(false);
const showGenderModal = ref(false);
const showBirthdayModal = ref(false);

// 加载用户信息
const loadUserInfo = async () => {
  try {
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    });
    
    const response = await getCurrentUserInfo();
    closeToast();
    
    if (response.code === 200 && response.data) {
      // 更新 store 中的用户信息
      userStore.setUserInfo(response.data);
    } else {
      showFailToast(response.message || '获取用户信息失败');
    }
  } catch (error) {
    closeToast();
    console.error('加载用户信息失败:', error);
    showFailToast('获取用户信息失败');
  }
};

// 页面加载时获取用户信息
onMounted(() => {
  loadUserInfo();
});

// 返回上一页
const goBack = () => {
  router.back();
};

// 跳转到账号管理
const goToAccountManage = () => {
  router.push('/account-manage');
};

// 编辑昵称
const editNickname = () => {
  showNicknameModal.value = true;
};

// 编辑性别
const editGender = () => {
  showGenderModal.value = true;
};

// 编辑生日
const editBirthday = () => {
  showBirthdayModal.value = true;
};

// 编辑收货地址
const editAddress = () => {
  router.push('/address/list');
};

// 处理头像上传
const handleAvatarUpload = async (file) => {
  try {
    showLoadingToast({
      message: '上传中...',
      forbidClick: true,
      duration: 0
    });

    // 创建 FormData
    const formData = new FormData();
    formData.append('file', file.file);

    // 调用上传接口
    const response = await uploadAvatar(formData);
    closeToast();

    if (response.code === 200 && response.data) {
      showSuccessToast('头像上传成功');
      // 重新加载用户信息以刷新头像
      await loadUserInfo();
    } else {
      showFailToast(response.message || '上传失败');
    }
  } catch (error) {
    closeToast();
    console.error('头像上传失败:', error);
    showFailToast('上传失败');
  }
};

// 文件大小超出限制
const onOversize = () => {
  showFailToast('图片大小不能超过 2MB');
};

// 处理昵称确认
const handleNicknameConfirm = async (nickname) => {
  try {
    showLoadingToast({
      message: '保存中...',
      forbidClick: true,
      duration: 0
    });
    
    const response = await updateUserInfo({
      ...userInfo.value,
      nickname: nickname
    });
    
    closeToast();
    
    if (response.code === 200) {
      showSuccessToast('保存成功');
      // 重新加载用户信息
      await loadUserInfo();
    } else {
      showFailToast(response.message || '保存失败');
    }
  } catch (error) {
    closeToast();
    console.error('更新昵称失败:', error);
    showFailToast('保存失败');
  }
};

// 处理性别确认
const handleGenderConfirm = async (gender) => {
  try {
    showLoadingToast({
      message: '保存中...',
      forbidClick: true,
      duration: 0
    });
    
    const response = await updateUserInfo({
      ...userInfo.value,
      gender: gender
    });
    
    closeToast();
    
    if (response.code === 200) {
      showSuccessToast('保存成功');
      // 重新加载用户信息
      await loadUserInfo();
    } else {
      showFailToast(response.message || '保存失败');
    }
  } catch (error) {
    closeToast();
    console.error('更新性别失败:', error);
    showFailToast('保存失败');
  }
};

// 处理生日确认
const handleBirthdayConfirm = async (birthday) => {
  try {
    showLoadingToast({
      message: '保存中...',
      forbidClick: true,
      duration: 0
    });
    
    const response = await updateUserInfo({
      ...userInfo.value,
      birthday: birthday
    });
    
    closeToast();
    
    if (response.code === 200) {
      showSuccessToast('保存成功');
      // 重新加载用户信息
      await loadUserInfo();
    } else {
      showFailToast(response.message || '保存失败');
    }
  } catch (error) {
    closeToast();
    console.error('更新生日失败:', error);
    showFailToast('保存失败');
  }
};
</script>

<style scoped>
.profile-view {
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

/* 头像区域 */
.avatar-section {
  margin-top: 56px;
  background-color: #fff;
  padding: 60px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 隐藏 Uploader 默认样式，使用自定义预览 */
.avatar-section :deep(.van-uploader__wrapper) {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-section :deep(.van-uploader__upload) {
  margin: 0;
  padding: 0;
  width: auto;
  height: auto;
  background: transparent;
  border: none;
}

.avatar-section :deep(.van-uploader__upload-icon) {
  display: none;
}

.avatar-section :deep(.van-uploader__preview) {
  display: none;
}

.avatar-container {
  position: relative;
  width: 120px;
  height: 120px;
  cursor: pointer;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.camera-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background-color: #333;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid #fff;
  cursor: pointer;
}

/* 个人信息列表 */
.info-list {
  background-color: #fff;
  margin-top: 12px;
}

.info-item {
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.info-item:active {
  background-color: #f8f8f8;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 16px;
  color: #333;
  font-weight: 400;
}

.info-value-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-value {
  font-size: 16px;
  color: #666;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-value.placeholder {
  color: #999;
  font-size: 14px;
}

.arrow-right {
  font-size: 20px;
  color: #ccc;
  font-style: normal;
  font-weight: 300;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .avatar-container {
    width: 100px;
    height: 100px;
  }
  
  .camera-icon {
    width: 32px;
    height: 32px;
  }
}
</style>

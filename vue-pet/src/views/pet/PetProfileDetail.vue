<template>
  <div class="pet-profile-detail">
    <!-- 顶部导航栏 -->
    <div class="header">
      <van-icon name="arrow-left" size="24" @click="handleBack" />
      <div class="title">档案详情</div>
      <div class="edit-btn" @click="handleEdit">编辑</div>
    </div>

    <!-- 加载状态 -->
    <van-loading v-if="loading" class="loading" size="24px" vertical>加载中...</van-loading>

    <!-- 档案内容 -->
    <div class="content" v-else-if="profile">
      <!-- 头像和名称 -->
      <div class="profile-header">
        <div class="avatar">
          <img 
            :src="profile.avatarUrl || getDefaultAvatar(profile.petType)" 
            alt="宠物头像"
          />
        </div>
        <div class="pet-name">{{ profile.petName }}</div>
      </div>

      <!-- 基本信息卡片 -->
      <div class="card">
        <div class="card-title">基本信息</div>
        <div class="info-row">
          <span class="label">宠物类型</span>
          <span class="value">{{ profile.petTypeText }}</span>
        </div>
        <div class="info-row">
          <span class="label">年龄段</span>
          <span class="value">{{ profile.ageStageText }}</span>
        </div>
        <div class="info-row" v-if="profile.bodySize">
          <span class="label">体型</span>
          <span class="value">{{ profile.bodySizeText }}</span>
        </div>
        <div class="info-row" v-if="profile.gender">
          <span class="label">性别</span>
          <span class="value">{{ profile.genderText }}</span>
        </div>
      </div>

      <!-- 体质特征卡片 -->
      <div class="card" v-if="profile.healthTags && profile.healthTags.length > 0">
        <div class="card-title">
          体质特征
          <span class="subtitle">（为您推荐相关商品）</span>
        </div>
        <div class="health-tags">
          <div class="health-tag" v-for="(tag, index) in profile.healthTags" :key="index">
            <van-icon name="success" color="#FF9800" />
            <span>{{ tag }}</span>
          </div>
        </div>
        <van-button 
          type="primary" 
          round 
          block 
          class="recommend-btn"
          @click="handleRecommend"
        >
          查看推荐商品
        </van-button>
      </div>

      <!-- 生活习惯卡片（可折叠） -->
      <van-collapse v-model="activeNames" class="habit-collapse">
        <van-collapse-item title="生活习惯" name="1">
          <div class="info-row" v-if="profile.activityLevel">
            <span class="label">活动量</span>
            <span class="value">{{ profile.activityLevelText }}</span>
          </div>
          <div class="info-row" v-if="profile.foodPreference">
            <span class="label">饮食偏好</span>
            <span class="value">{{ profile.foodPreferenceText }}</span>
          </div>
          <div class="info-row" v-if="profile.remark">
            <span class="label">备注</span>
            <span class="value remark">{{ profile.remark }}</span>
          </div>
          <div class="empty-habit" v-if="!profile.activityLevel && !profile.foodPreference && !profile.remark">
            暂无生活习惯信息
          </div>
        </van-collapse-item>
      </van-collapse>

      <!-- 底部编辑按钮 -->
      <div class="bottom-btn">
        <van-button 
          type="primary" 
          round 
          block 
          @click="handleEdit"
        >
          编辑档案
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getPetProfileDetail } from '@/api/petProfile';
import { showToast } from 'vant';

const router = useRouter();
const route = useRoute();
const loading = ref(true);
const profile = ref(null);
const activeNames = ref([]);

// 获取档案详情
const fetchDetail = async () => {
  try {
    loading.value = true;
    const id = route.params.id;
    const res = await getPetProfileDetail(id);
    if (res.code === 200) {
      profile.value = res.data;
    } else {
      showToast(res.message || '获取档案详情失败');
      router.back();
    }
  } catch (error) {
    console.error('获取档案详情失败:', error);
    showToast('获取档案详情失败');
    router.back();
  } finally {
    loading.value = false;
  }
};

// 获取默认头像
const getDefaultAvatar = (petType) => {
  if (petType === 'cat') {
    return '/images/default_cat.png';
  } else if (petType === 'dog') {
    return '/images/default_dog.png';
  }
  return '/images/default_pet.png';
};

// 返回
const handleBack = () => {
  router.back();
};

// 编辑
const handleEdit = () => {
  router.push(`/pet-profile/edit/${route.params.id}`);
};

// 查看推荐商品
const handleRecommend = () => {
  // 跳转到推荐商品页面，携带宠物ID
  router.push({
    path: '/shopping',
    query: { petId: route.params.id }
  });
};

onMounted(() => {
  fetchDetail();
});
</script>

<style scoped>
.pet-profile-detail {
  min-height: 100vh;
  background-color: #f7f5f0;
  padding-bottom: 80px;
}

.header {
  background-color: #fff;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.title {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.edit-btn {
  color: #FF9800;
  font-size: 16px;
  cursor: pointer;
}

.loading {
  padding: 100px 0;
  text-align: center;
}

.content {
  padding: 20px;
}

.profile-header {
  background-color: #fff;
  border-radius: 12px;
  padding: 32px 20px;
  text-align: center;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 16px;
  background-color: #f5f5f5;
  border: 4px solid #FFF3E0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pet-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.subtitle {
  font-size: 12px;
  font-weight: 400;
  color: #999;
  margin-left: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.value {
  font-size: 14px;
  color: #333;
  text-align: right;
  flex: 1;
  margin-left: 16px;
}

.value.remark {
  word-break: break-all;
}

.health-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.health-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background-color: #FFF3E0;
  color: #FF9800;
  font-size: 14px;
  border-radius: 20px;
}

.recommend-btn {
  background: linear-gradient(135deg, #FFB74D 0%, #FF9800 100%);
  border: none;
}

.habit-collapse {
  margin-bottom: 16px;
}

.habit-collapse :deep(.van-collapse-item__content) {
  padding: 0 20px 16px;
}

.empty-habit {
  padding: 20px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.bottom-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 20px;
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

.bottom-btn .van-button {
  background: linear-gradient(135deg, #FFB74D 0%, #FF9800 100%);
  border: none;
}
</style>

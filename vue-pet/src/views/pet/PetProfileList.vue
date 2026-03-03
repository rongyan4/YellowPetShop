<template>
  <div class="pet-profile-list">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="title">宠物档案</div>
      <div class="add-btn" @click="handleAdd">
        <van-icon name="plus" size="24" />
      </div>
    </div>

    <!-- 档案列表 -->
    <div class="profile-list" v-if="profileList.length > 0">
      <van-swipe-cell v-for="profile in profileList" :key="profile.id">
        <div class="profile-card" @click="handleDetail(profile.id)">
          <div class="avatar">
            <img 
              :src="profile.avatarUrl || getDefaultAvatar(profile.petType)" 
              alt="宠物头像"
            />
          </div>
          <div class="info">
            <div class="name">{{ profile.petName }}</div>
            <div class="basic">
              {{ profile.petTypeText }} · {{ profile.ageStageText }}
            </div>
            <div class="tags" v-if="profile.healthTags && profile.healthTags.length > 0">
              <span 
                class="tag" 
                v-for="(tag, index) in profile.healthTags.slice(0, 3)" 
                :key="index"
              >
                {{ tag }}
              </span>
            </div>
          </div>
          <van-icon name="arrow" class="arrow" />
        </div>
        <template #right>
          <van-button 
            square 
            type="danger" 
            text="删除" 
            class="delete-btn"
            @click="handleDelete(profile.id)"
          />
        </template>
      </van-swipe-cell>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <div class="empty-icon">🐾</div>
      <div class="empty-text">还没有宠物档案</div>
      <div class="empty-desc">添加宠物档案，获得专属推荐</div>
      <van-button 
        type="primary" 
        round 
        class="empty-btn"
        @click="handleAdd"
      >
        添加第一个宠物档案
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getPetProfileList, deletePetProfile } from '@/api/petProfile';
import { showToast, showConfirmDialog } from 'vant';

const router = useRouter();
const profileList = ref([]);

// 获取档案列表
const fetchList = async () => {
  try {
    const res = await getPetProfileList();
    if (res.code === 200) {
      profileList.value = res.data || [];
    } else {
      showToast(res.message || '获取档案列表失败');
    }
  } catch (error) {
    console.error('获取档案列表失败:', error);
    showToast('获取档案列表失败');
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

// 添加档案
const handleAdd = () => {
  router.push('/pet-profile/add');
};

// 查看详情
const handleDetail = (id) => {
  router.push(`/pet-profile/detail/${id}`);
};

// 删除档案
const handleDelete = async (id) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这个宠物档案吗？',
    });
    
    const res = await deletePetProfile(id);
    if (res.code === 200) {
      showToast('删除成功');
      fetchList();
    } else {
      showToast(res.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      showToast('删除失败');
    }
  }
};

onMounted(() => {
  fetchList();
});
</script>

<style scoped>
.pet-profile-list {
  min-height: 100vh;
  background-color: #f7f5f0;
  padding-bottom: 20px;
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
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.add-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #FF9800;
  transition: transform 0.2s;
}

.add-btn:active {
  transform: scale(0.9);
}

.profile-list {
  padding: 16px 20px;
}

.profile-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s;
}

.profile-card:active {
  transform: scale(0.98);
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
  background-color: #f5f5f5;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info {
  flex: 1;
  min-width: 0;
}

.name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.basic {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  background-color: #FFF3E0;
  color: #FF9800;
  font-size: 12px;
  border-radius: 4px;
}

.arrow {
  color: #ccc;
  margin-left: 12px;
}

.delete-btn {
  height: 100%;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 32px;
}

.empty-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #FFB74D 0%, #FF9800 100%);
  border: none;
}
</style>

<template>
  <div class="user-info-bar">
    <div class="user-avatar">
      <img :src="userInfo.avatar || '/images/default-avatar.png'" alt="用户头像" />
      <span class="level-badge" v-if="userInfo.level">{{ userInfo.level }}</span>
    </div>
    <div class="user-details">
      <div class="username">{{ userInfo.username || '未登录' }}</div>
      <div class="level-progress">
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: levelProgress + '%' }"></div>
        </div>
        <span class="progress-text">{{ userInfo.currentPoints || 0 }}/{{ userInfo.nextLevelPoints || 0 }}葫芦 冲{{ userInfo.nextLevel || 'S2' }}</span>
      </div>
    </div>
    <div class="user-stats">
      <div class="stat-item">
        <span class="stat-value">{{ userInfo.points || 0 }}</span>
        <span class="stat-label">葫芦</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ userInfo.coupons || 0 }}</span>
        <span class="stat-label">优惠券</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({
      avatar: '',
      username: '用户',
      level: 'S1',
      currentPoints: 0,
      nextLevelPoints: 3,
      nextLevel: 'S2',
      points: 0,
      coupons: 0
    })
  }
});

const levelProgress = computed(() => {
  if (!props.userInfo.nextLevelPoints || props.userInfo.nextLevelPoints === 0) {
    return 0;
  }
  return Math.min((props.userInfo.currentPoints / props.userInfo.nextLevelPoints) * 100, 100);
});
</script>

<style lang="scss" scoped>
  .user-info-bar {
  display: flex;
  align-items: center;
  padding: .4rem .5333rem;
  background: rgba(255, 255, 255, 0.95);
  margin: 0;
  border-radius: .2667rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;

  .user-avatar {
    position: relative;
    width: 1.3333rem;
    height: 1.3333rem;
    margin-right: .2667rem;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #fff;
    }

    .level-badge {
      position: absolute;
      bottom: -0.1333rem;
      right: -0.1333rem;
      background: #4CAF50;
      color: #fff;
      font-size: .24rem;
      padding: 0.04rem 0.1333rem;
      border-radius: 0.1333rem;
      border: 2px solid #fff;
      font-weight: 600;
    }
  }

  .user-details {
    flex: 1;
    min-width: 0;

    .username {
      font-size: .3733rem;
      font-weight: 600;
      color: #333;
      margin-bottom: .1333rem;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .level-progress {
      display: flex;
      align-items: center;
      gap: .1333rem;

      .progress-bar {
        flex: 1;
        height: .2667rem;
        background: #f0f0f0;
        border-radius: .1333rem;
        overflow: hidden;

        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #FFD700, #FFA500);
          border-radius: .1333rem;
          transition: width 0.3s ease;
        }
      }

      .progress-text {
        font-size: .2667rem;
        color: #666;
        white-space: nowrap;
      }
    }
  }

  .user-stats {
    display: flex;
    gap: .4rem;
    margin-left: .2667rem;
    flex-shrink: 0;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: .0533rem;

      .stat-value {
        font-size: .4267rem;
        font-weight: 600;
        color: #333;
      }

      .stat-label {
        font-size: .2667rem;
        color: #999;
      }
    }
  }
}
</style>


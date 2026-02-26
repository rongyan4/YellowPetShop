<template>
  <div class="user-info-bar">
    <div class="user-avatar">
      <img :src="userInfo.avatar || '/images/default_avatar.png'" alt="用户头像" />
      <span
        class="level-badge"
        v-if="userInfo.level"
        :style="{ background: levelColor }"
      >
        {{ userInfo.level }}
      </span>
    </div>
    <div class="user-details">
      <div class="username">{{ userInfo.username || '未登录' }}</div>
      <div class="level-progress" v-if="userInfo.level && userInfo.nextLevelPoints > 0">
        <div class="progress-bar">
          <div
            class="progress-fill"
            :style="{ width: levelProgress + '%', background: levelGradient }"
          ></div>
        </div>
        <span class="progress-text">
          {{ userInfo.currentPoints || 0 }}/{{ userInfo.nextLevelPoints || 0 }} 积分
          冲 {{ userInfo.nextLevel || 'S2' }}
        </span>
      </div>
    </div>
    <div class="user-stats">
      <div class="stat-item">
        <span class="stat-value">{{ userInfo.points }}</span>
        <span class="stat-label">积分</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ userInfo.coupons }}</span>
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
      avatar: '/images/default_avatar.png',
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

// 解析等级数字（S1、S2...）
const levelNumber = computed(() => {
  const level = props.userInfo.level || '';
  const match = String(level).match(/S(\d+)/i);
  return match ? parseInt(match[1], 10) : 1;
});

// 不同等级对应不同颜色
const levelColor = computed(() => {
  const num = levelNumber.value;
  if (num >= 5) return '#E91E63'; // S5 及以上：玫红
  if (num === 4) return '#FF9800'; // S4：橙色
  if (num === 3) return '#2196F3'; // S3：蓝色
  if (num === 2) return '#4CAF50'; // S2：绿色
  return '#9E9E9E'; // S1：灰色
});

const levelGradient = computed(() => {
  const color = levelColor.value;
  // 使用单色渐变，兼容当前样式
  return `linear-gradient(90deg, ${color}, ${color})`;
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
    width: 1.7067rem;  /* 64px -> rem */
    height: 1.7067rem; /* 64px -> rem */
    min-width: 1.7067rem;
    min-height: 1.7067rem;
    padding: 0;
    margin-right: .4rem;
    flex-shrink: 0;
    box-sizing: border-box; 

    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
      border: .0533rem solid #fff; /* 2px -> rem */
      min-width: 100%;
      min-height: 100%;
      box-sizing: border-box;
      display: block;
    }

    .level-badge {
      position: absolute;
      bottom: -.1067rem; /* -4px -> rem */
      right: -.1067rem;  /* -4px -> rem */
      background: #4CAF50;
      color: #fff;
      font-size: .32rem;
      padding: .0533rem .16rem;
      border-radius: .1333rem;
      border: .0533rem solid #fff; /* 2px -> rem */
      font-weight: 600;
      line-height: 1;
    }
  }

  .user-details {
    flex: 1;
    min-width: 0;

    .username {
      font-size: .4267rem;
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
        height: .32rem;
        background: #f0f0f0;
        border-radius: .16rem;
        overflow: hidden;
        min-width: 2rem;

        .progress-fill {
          height: 100%;
          border-radius: .16rem;
          transition: width 0.3s ease;
        }
      }

      .progress-text {
        font-size: .2933rem;
        color: #666;
        white-space: nowrap;
        flex-shrink: 0;
      }
    }
  }

  .user-stats {
    display: flex;
    gap: .5333rem;
    margin-left: .4rem;
    flex-shrink: 0;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: .0533rem;

      .stat-value {
        font-size: .48rem;
        font-weight: 600;
        color: #333;
        line-height: 1.2;
      }

      .stat-label {
        font-size: .2933rem;
        color: #999;
        line-height: 1;
      }
    }
  }
}
</style>


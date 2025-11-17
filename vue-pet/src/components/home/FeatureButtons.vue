<template>
  <div class="feature-buttons">
    <div 
      class="feature-btn" 
      v-for="(item, index) in features" 
      :key="index"
      @click="handleFeatureClick(item.type)"
    >
      <div class="feature-icon">
        <i :class="item.icon" v-if="item.icon"></i>
        <img :src="item.image" v-else-if="item.image" :alt="item.title" />
      </div>
      <div class="feature-title">{{ item.title }}</div>
      <div class="feature-badge" v-if="item.badge">{{ item.badge }}</div>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['feature-click']);

const props = defineProps({
  features: {
    type: Array,
    default: () => [
      { type: 'coupon-exchange', title: '券码兑换', icon: 'iconfont icon-piao' },
      { type: 'member-exchange', title: '会员兑换', icon: 'iconfont icon-gift' },
      { type: 'points-lottery', title: '积分抽奖', icon: 'iconfont icon-lottery', badge: '周三特惠' },
      { type: 'promotions', title: '优惠活动', icon: 'iconfont icon-promotion' }
    ]
  }
});

const handleFeatureClick = (type) => {
  emit('feature-click', type);
};
</script>

<style lang="scss" scoped>
.feature-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: .2667rem;
  padding: 0 .2667rem;
  margin: .2667rem 0 .5333rem 0;

  .feature-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: .4rem .1333rem;
    background: #fff;
    border-radius: .2667rem;
    position: relative;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: transform 0.2s, box-shadow 0.2s;

    &:active {
      transform: scale(0.95);
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    }

    .feature-icon {
      width: .8rem;
      height: .8rem;
      margin-bottom: .1333rem;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        font-size: .8rem;
        color: #666;
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }
    }

    .feature-title {
      font-size: .32rem;
      color: #333;
      text-align: center;
    }

    .feature-badge {
      position: absolute;
      top: .1333rem;
      right: .1333rem;
      background: #FF6B6B;
      color: #fff;
      font-size: .2133rem;
      padding: 0.0267rem 0.1067rem;
      border-radius: 0.1067rem;
      white-space: nowrap;
    }
  }
}
</style>


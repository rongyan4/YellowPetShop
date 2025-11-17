<template>
  <div class="service-buttons">
    <div class="service-btn self-pickup" @click="handleServiceClick('self-pickup')">
      <div class="btn-content">
        <div class="btn-title">门店自取</div>
        <div class="btn-desc">下单享优惠</div>
      </div>
      <div class="btn-badge" v-if="selfPickupBadge">
        <img :src="selfPickupBadge" alt="badge" />
      </div>
    </div>
    <div class="service-btn delivery" @click="handleServiceClick('delivery')">
      <div class="delivery-banner" v-if="deliveryBanner">最高享免配!</div>
      <div class="btn-content">
        <div class="btn-title">快递配送</div>
        <div class="btn-desc">新鲜送上门</div>
      </div>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['service-click']);

const props = defineProps({
  selfPickupBadge: {
    type: String,
    default: ''
  },
  deliveryBanner: {
    type: String,
    default: '最高享免配!'
  }
});

const handleServiceClick = (type) => {
  emit('service-click', type);
};
</script>

<style lang="scss" scoped>
.service-buttons {
  display: flex;
  gap: .2667rem;
  padding: 0 .2667rem;
  margin: .5333rem 0;

  .service-btn {
    flex: 1;
    background: #fff;
    border-radius: .2667rem;
    padding: .4rem .2667rem;
    position: relative;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: transform 0.2s, box-shadow 0.2s;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    }

    .btn-content {
      .btn-title {
        font-size: .4267rem;
        font-weight: 600;
        color: #333;
        margin-bottom: .1333rem;
      }

      .btn-desc {
        font-size: .32rem;
        color: #999;
      }
    }

    .btn-badge {
      position: absolute;
      bottom: .1333rem;
      left: .1333rem;
      width: .8rem;
      height: .8rem;

      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }
    }

    &.delivery {
      .delivery-banner {
        position: absolute;
        top: -.1333rem;
        right: .1333rem;
        background: #FFD700;
        color: #333;
        font-size: .24rem;
        padding: 0.0533rem 0.1333rem;
        border-radius: 0.1333rem;
        font-weight: 600;
        white-space: nowrap;
      }
    }
  }
}
</style>


<template>
  <div class="container">
    <div class="title">
      <i class="iconfont icon-chongwugou"></i>
      <h1>精品推荐</h1>
    </div>
    <div class="content">
      <div class="list">
        <li v-for="(item, index) in recommendGoods" :key="index">
          <div class="img-box">
            <img :src="item.imgUrl" alt="商品图片" />
            <p class="msg">{{ item.msg }}</p>
          </div>
          <h3 class="title-text">{{ item.title }}</h3>
          <div class="price">
            <span>￥</span>{{ item.price }}
            <span class="unit">/{{ item.unit }}</span>
          </div>
          <div class="saled">已售 {{ item.saled }} 件</div>
        </li>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
    const list = ref([
      {imgUrl: '/images/goods/ml.png', title:"乳鸽猫粮", msg:"蔓越莓+鱼油", price:89.9, unit: '250g', saled:1},
      {imgUrl: '/images/goods/ml.png', title:"乳鸽猫粮", msg:"蔓越莓+鱼油", price:89.9, unit: '250g', saled:1},
      {imgUrl: '/images/goods/ml.png', title:"乳鸽猫粮", msg:"蔓越莓+鱼油", price:89.9, unit: '250g', saled:1},
      {imgUrl: '/images/goods/ml.png', title:"乳鸽猫粮", msg:"蔓越莓+鱼油", price:89.9, unit: '250g', saled:1},
      {imgUrl: '/images/goods/ml.png', title:"乳鸽猫粮", msg:"蔓越莓+鱼油", price:89.9, unit: '250g', saled:1},
      {imgUrl: '/images/goods/ml.png', title:"乳鸽猫粮", msg:"蔓越莓+鱼油", price:89.9, unit: '250g', saled:1},
    ]);
    const recommendGoods = ref([]);
    const fetchRecommendGoods = () => {
      recommendGoods.value = list.value;
    }
    
    // const fetchRecommendGoods = async () => {
    //     const response = await axios.get('/api/home/recommend')
    //     const result = response.data;
        
    //     RecommendGoods.value = result.data;

    
    onMounted(fetchRecommendGoods)
</script>

<style lang="scss" scoped>
.container {
  background-color: #f7f7f7;
  padding: 0 .2667rem .5333rem;
  font-size: .3733rem;

  .title {
    display: flex;
    align-items: center;
    padding: .4rem .2667rem;
    margin-bottom: .2667rem;

    .iconfont {
      color: #a0e5ff;
      font-size: .5867rem;
    }

    h1 {
      font-size: .48rem;
      margin-left: .2667rem;
      font-weight: 600;
    }
  }

  .content {
    .list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: .2667rem;
      padding: 0;
      margin: 0;
      list-style: none;

      li {
        background: #fff;
        border-radius: .16rem;
        padding: .2667rem;
        display: flex;
        flex-direction: column;
        // 高度随内容自适应
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);

        .img-box {
          position: relative;
          width: 100%;
          aspect-ratio: 1/1;
          margin-bottom: .2133rem;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: .1067rem;
          }

          .msg {
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            text-align: center;
            background: rgba(0,0,0,0.6);
            color: #fff;
            font-size: .32rem;
            padding: .1067rem 0;
            margin: 0;
          }
        }

        .title-text {
          font-size: .3733rem;
          margin: 0 0 .16rem 0;
          font-weight: 500;
          color: #333;
          line-height: 1.3;
        }

        .price {
          font-size: .4267rem;
          color: #ff4d4f;
          margin: 0 0 .1333rem 0;
          font-weight: 600;

          span:first-child {
            font-size: .3467rem;
          }

          .unit {
            font-size: .32rem;
            color: #666;
            font-weight: normal;
            margin-left: .0533rem;
          }
        }

        .saled {
          font-size: .32rem;
          color: #999;
          margin: 0;
          margin-top: auto; // 推到容器底部
        }
      }
    }
  }
}
</style>
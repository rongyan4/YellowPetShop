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
            <p class="msg" v-if="item.msg">{{ item.msg }}</p>
          </div>
          <h3 class="title-text">{{ item.title }}</h3>
          <div class="price">
            <span>￥</span>{{ item.price }}
            <span class="unit" v-if="item.unit">/{{ item.unit }}</span>
          </div>
          <div class="saled" v-if="item.saled">已售 {{ item.saled }} 件</div>
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
      column-count: 2; // 瀑布流：两列布局
      column-gap: .2667rem; // 列间距
      padding: 0;
      margin: 0;
      list-style: none;

      li {
        background: #fff;
        border-radius: .16rem;
        padding: .2667rem;
        display: inline-flex; // 瀑布流必须使用 inline-flex 或 inline-block
        flex-direction: column;
        width: 100%; // 确保占满列宽
        break-inside: avoid; // 防止商品被分割到两列
        page-break-inside: avoid; // 兼容性
        margin-bottom: .2667rem; // 商品之间的间距
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        height: auto; // 高度随内容自适应
        box-sizing: border-box; // 确保 padding 包含在宽度内

        .img-box {
          position: relative;
          width: 100%;
          aspect-ratio: 1/1;
          margin-bottom: .2133rem;
          flex-shrink: 0; // 图片区域不收缩

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: .1067rem;
            display: block;
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
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          word-break: break-word;
          flex-shrink: 0;
        }

        .price {
          display: flex;
          align-items: baseline;
          font-size: .4267rem;
          color: #ff4d4f;
          margin: 0 0 .1333rem 0;
          font-weight: 600;
          flex-shrink: 0;

          span:first-child {
            font-size: .3467rem;
            margin-right: .0267rem;
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
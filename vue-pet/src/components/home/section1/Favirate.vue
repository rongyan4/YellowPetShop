<template>
  <div class="container">
    <div class="title" >
      <i class="iconfont icon-chongwugou"></i>
      <h1>精品推荐</h1>
    </div>
  </div>
</template>
<script setup>
    import {  ref,reactive } from 'vue';
    const list = ref([
      {imgUrl: '@assets/images/goods'}
    ])
</script>
<style lang="scss" scoped>
.container{
  background-color: #f7f7f7;
  font-size: .3733rem;
  .title{
      display: flex;
      align-items: center;
      margin-bottom: .2667rem;
      padding: 0 .2667rem;
      .iconfont{
          color: #a0e5ff;
          font-size: .5867rem;
      }
      h1{
          font-size: .48rem;
          margin-left: .2667rem;
      }
  }
}
</style>
<template>
  <div class="top-bar-main">
    <div class="top-bar-input">
      2025新品
    <i class="iconfont icon-sousuo1"></i>
    </div>
    <div class="duihua">
      <i class="iconfont icon-duihua"/>
    </div>
  </div>
</template>

<script setup>
  
</script>

<style lang="scss" scoped>
.top-bar-main{
  display: flex;
  align-items: center;
  background-color: #f6f6f6;
  display: flex;
  justify-content: space-between;
  left: 0;
  padding: .4rem .6rem;
  position: fixed;
  top: 0;
  width: calc(100% - 1.2rem);
  z-index: 999;
  color: gray;
  font-size: 0.5rem;
  .top-bar-input{
    align-items: center;
    background-color: #fff;
    border-radius: 500px;
    display: flex;
    height: 1rem;
    padding: 0 .4rem;
    flex: 1;
    margin-right: .2133rem;
    .iconfont{
      font-size: .48rem;
    }
    .icon-sousuo1{
      margin-left: auto;
    }
  }
  .duihua{
    position: relative;
    display: flex;
    align-items: center;
    .iconfont{
      font-size: .8533rem;
    }
  }
}
</style>
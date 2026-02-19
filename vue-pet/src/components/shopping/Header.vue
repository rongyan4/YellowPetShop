<template>
  <div class="top-bar-main">
    <div class="top-bar-input" @click="handleSearchClick">
      <input 
        v-model="searchKeyword" 
        type="text" 
        placeholder="2025新品"
        class="search-input"
        @keyup.enter="handleSearch"
        @click.stop
      />
      <i class="iconfont icon-sousuo1" @click.stop="handleSearch"></i>
    </div>
    <div class="duihua">
      <i class="iconfont icon-duihua"/>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits, defineExpose } from 'vue';

const emit = defineEmits(['switchToSearch', 'search']);

const searchKeyword = ref('');

// 点击搜索框区域，切换到搜索标签
const handleSearchClick = () => {
  emit('switchToSearch');
  // 如果搜索框为空，不执行搜索，只切换标签
};

// 执行搜索（点击搜索按钮或按回车）
const handleSearch = () => {
  console.log('点击搜索按钮，关键词:', searchKeyword.value);
  
  // 先切换到搜索页
  emit('switchToSearch');
  
  // 如果输入框有文字，执行搜索
  if (searchKeyword.value.trim()) {
    console.log('执行搜索:', searchKeyword.value.trim());
    emit('search', searchKeyword.value.trim());
  } else {
    console.log('搜索框为空，不执行搜索');
  }
};

// 设置搜索关键词（供外部调用）
const setSearchKeyword = (keyword) => {
  searchKeyword.value = keyword;
};

// 暴露方法给父组件
defineExpose({
  setSearchKeyword,
  searchKeyword
});
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
    border-radius: 13.3333rem;
    display: flex;
    height: 1rem;
    padding: 0 .4rem;
    flex: 1;
    margin-right: .2133rem;
    cursor: pointer;
    
    .search-input {
      flex: 1;
      border: none;
      background: transparent;
      font-size: .3733rem;
      color: #333;
      outline: none;
      cursor: text;
      
      &::placeholder {
        color: #999;
      }
    }
    
    .iconfont{
      font-size: .48rem;
      cursor: pointer;
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
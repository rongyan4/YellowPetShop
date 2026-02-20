<template>
  <div class="top-bar-main">
    <div class="top-bar-input" @click="handleSearchClick">
      <!-- 清空按钮（输入框不为空时显示） -->
      <i 
        v-if="searchKeyword.trim()" 
        class="iconfont icon-guanbi clear-btn" 
        @click.stop="handleClear"
      ></i>
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

const emit = defineEmits(['switchToSearch', 'search', 'clear', 'backToHome']);

const searchKeyword = ref('');

// 点击搜索框区域，切换到搜索标签
const handleSearchClick = () => {
  emit('switchToSearch');
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
    // 搜索框为空时，直接退回原界面
    console.log('搜索框为空，退回原界面');
    emit('backToHome');
  }
};

// 清空搜索框
const handleClear = () => {
  searchKeyword.value = '';
  emit('clear');
};

// 设置搜索关键词（供外部调用）
const setSearchKeyword = (keyword) => {
  searchKeyword.value = keyword;
};

// 暴露方法和属性给父组件
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
    position: relative;
    
    .clear-btn {
      font-size: .4rem;
      color: #999;
      cursor: pointer;
      margin-right: .2rem;
      transition: color 0.2s;
      
      &:hover {
        color: #666;
      }
      
      &:active {
        transform: scale(0.9);
      }
    }
    
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

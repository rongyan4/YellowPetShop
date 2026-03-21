<template>
  <div class="shopping">
    <Header 
      ref="headerRef"
      @switchToSearch="handleSwitchToSearch"
      @search="handleSearch"
    ></Header>
    <div class="content">
      <tabs v-model:active="active">
        <tab title="推荐">
          <section1></section1>
        </tab>
        <tab title="分类">
          <List></List>
        </tab>
        <tab title="搜索">
          <Search 
            ref="searchRef"
            @tagClick="handleTagClick"
          ></Search>
        </tab>
      </tabs>
    <Tabbar></Tabbar>
    </div>
  </div>
</template>

<script setup>
import Tabbar from "@/components/TabBar.vue";
import Header from "@/components/shopping/Header.vue"
import section1 from "@/components/shopping/section1/section1.vue"
import List from "@/components/shopping/list/List.vue"
import Search from "@/components/shopping/search/Search.vue"
import { onBeforeMount, onMounted, onBeforeUnmount, ref, nextTick } from 'vue';
import { Tab,Tabs } from 'vant';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { saveScrollPosition, restoreScrollPosition } from '@/utils/scrollPosition';

const route = useRoute();

onBeforeMount(async() => {
  let res = await({
    url:'/api/home'
  })
})

onMounted(() => {
  // 恢复滚动位置和标签页
  restoreScrollPosition(route.path);
});

// 页面卸载前保存滚动位置
onBeforeUnmount(() => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
});

const active = ref(0);

const headerRef = ref(null);
const searchRef = ref(null);

// 切换到搜索标签
const handleSwitchToSearch = async () => {
  console.log('切换到搜索标签');
  active.value = 2;
  // 切换到搜索标签时，如果搜索框为空，重置搜索状态显示历史记录
  await nextTick();
  if (searchRef.value && headerRef.value) {
    const keyword = headerRef.value.searchKeyword;
    console.log('当前搜索关键词:', keyword);
    if (!keyword || !keyword.trim()) {
      // 安全调用 resetSearch
      if (typeof searchRef.value.resetSearch === 'function') {
        console.log('重置搜索状态');
        searchRef.value.resetSearch();
      }
    }
  }
};

// 执行搜索
const handleSearch = async (keyword) => {
  console.log('ShoppingView 收到搜索请求:', keyword);
  // 确保在搜索标签页
  if (active.value !== 2) {
    console.log('切换到搜索标签页');
    active.value = 2;
    // 等待 DOM 更新，确保 Search 组件已渲染
    await nextTick();
  }
  
  // 调用搜索组件的搜索方法
  if (searchRef.value && typeof searchRef.value.performSearch === 'function') {
    console.log('调用 Search 组件的 performSearch 方法');
    searchRef.value.performSearch(keyword);
  } else {
    console.error('Search 组件未就绪, searchRef.value:', searchRef.value);
  }
};

// 点击搜索标签
const handleTagClick = (tag) => {
  // 将标签内容设置到搜索框
  if (headerRef.value) {
    headerRef.value.setSearchKeyword(tag);
  }
  // 执行搜索
  handleSearch(tag);
};

</script>

<style scoped>
.content{
  margin-top: 2rem;
}
.shopping{
  height: calc(100vh - 1.6rem);
  overflow-y: auto;
  padding-bottom: .2667rem;
}

:deep(.van-tab__text) {
  font-size: .3733rem;  /* 固定文字大小 */
}

:deep(.van-tabs__line) {
  width: 1.0667rem;     /* 固定下划线宽度 */
  height: .0533rem;     /* 固定下划线高度 */
  background-color: #2c3e50;
  border-radius: .0267rem;
}

:deep(.van-tabs__wrap) {
  height: 1.1733rem;    /* 固定 Tab 栏高度 */
}
</style>

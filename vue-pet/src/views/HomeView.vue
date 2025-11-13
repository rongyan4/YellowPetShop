<template>
  <div class="home">
    <Header></Header>
    <div class="content">
      <tabs v-model:active="active">
        <tab 
        v-for="(item, index) in tabList" 
        :title="item.title"
        :key="item.type">
          <div v-if="active === 0">
            <section1></section1>
          </div>
          <div v-if="active === 1">
            内容2
          </div>
          <div v-if="active === 2">
            内容3
          </div>
        </tab>
      </tabs>
    <Tabbar></Tabbar>
    </div>
  </div>
</template>

<script setup>
import Tabbar from "@/components/TabBar.vue";
import Header from "@/components/home/Header.vue"
import section1 from "@/components/home/section1/section1.vue"
import { ref } from 'vue';
import { Tab,Tabs } from 'vant';
const active = ref(0);
const tabList = ref([
  { type:1 , title:"推荐"},
  { type:2 , title:"推荐"},
  { type:3 , title:"推荐"},
])

</script>

<style>
.content{
  margin-top: 2rem;
}
</style>

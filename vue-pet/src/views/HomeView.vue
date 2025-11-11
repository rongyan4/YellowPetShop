<template>
  <div class="home">
    <Header></Header>
  </div>
  <div class="content">
    <tabs v-model:active="active">
      <tab v-for="index in 8" :title="'标签 ' + index">
        内容 {{ index }}
      </tab>
    </tabs>
  </div>
  <Tabbar></Tabbar>
</template>

<script setup>
import Tabbar from "@/components/TabBar.vue";
import Header from "@/components/home/Header.vue"
import { ref } from 'vue';
import { Tab,Tabs } from 'vant';
const active = ref(0);

</script>

<style>
.content{
  margin-top: 2rem;
}
</style>

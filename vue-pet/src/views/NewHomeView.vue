<template>
  <div class="new-home">
    <div class="home-content">
      <sectionNew></sectionNew>
    </div>
  </div>
</template>

<script setup>
import sectionNew from "@/components/home/section1/sectionNew.vue";
</script>

<style lang="scss" scoped>
.new-home {
  background: #f7f5f0;
  min-height: 100vh;
  padding-bottom: 1.6rem;

  .home-content {
    width: 100%;
  }
}
</style>


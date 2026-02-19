<template>
  <div class="address-list-view">
    <!-- 头部 -->
    <van-nav-bar
      title="选择收货地址"
      left-arrow
      @click-left="goBack"
    />

    <!-- 地址列表 -->
    <div class="address-content">
      <van-address-list
        v-model="selectedAddressId"
        :list="addressList"
        default-tag-text="默认"
        @add="onAddAddress"
        @edit="onEditAddress"
        @select="onSelectAddress"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast } from 'vant';
import { getAddressListSafe } from '@/api/address';

const router = useRouter();
const route = useRoute();

// 选中的地址ID
const selectedAddressId = ref('');

// 地址列表
const addressList = ref([]);

// 加载地址列表
const loadAddressList = async () => {
  const data = await getAddressListSafe();
  if (data) {
    addressList.value = data.map(item => ({
      id: item.id.toString(),
      name: item.receiverName,
      tel: item.receiverPhone,
      address: `${item.province} ${item.city} ${item.district} ${item.detailAddress}`,
      isDefault: item.isDefault
    }));
    
    // 设置默认选中的地址
    const defaultAddress = addressList.value.find(item => item.isDefault);
    if (defaultAddress) {
      selectedAddressId.value = defaultAddress.id;
    } else if (addressList.value.length > 0) {
      selectedAddressId.value = addressList.value[0].id;
    }
  }
};

// 新增地址
const onAddAddress = () => {
  router.push('/address/edit');
};

// 编辑地址
const onEditAddress = (item) => {
  router.push(`/address/edit?id=${item.id}`);
};

// 选择地址
const onSelectAddress = (item) => {
  // 返回到订单确认页面，并传递选中的地址信息
  const address = addressList.value.find(addr => addr.id === item.id);
  if (address) {
    console.log('=== 地址列表 - 选择地址 ===');
    console.log('选中的地址:', address);
    
    // 通过 localStorage 临时存储
    const addressData = {
      name: address.name,
      tel: address.tel,
      address: address.address
    };
    
    console.log('保存到 localStorage 的数据:', addressData);
    localStorage.setItem('selectedAddress', JSON.stringify(addressData));
    
    // 触发自定义事件通知订单确认页面
    window.dispatchEvent(new Event('addressSelected'));
    
    console.log('=== 返回上一页 ===');
    router.back();
  }
};

// 返回
const goBack = () => {
  router.back();
};

onMounted(() => {
  loadAddressList();
});
</script>

<style scoped>
.address-list-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.address-content {
  padding-top: 46px;
}
</style>

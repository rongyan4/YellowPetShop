<template>
  <div class="address-edit-view">
    <!-- 头部 -->
    <van-nav-bar
      :title="isEdit ? '编辑地址' : '新增地址'"
      left-arrow
      @click-left="goBack"
    />

    <!-- 地址表单 -->
    <div class="address-form">
      <van-address-edit
        :area-list="areaList"
        :address-info="addressInfo"
        :show-delete="isEdit"
        :show-set-default="true"
        :show-search-result="false"
        save-button-text="保存"
        delete-button-text="删除"
        @save="onSave"
        @delete="onDelete"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';
import { addAddressSafe, setDefaultAddressSafe } from '@/api/address';

// 如果安装了 @vant/area-data，使用以下导入
// import { areaList } from '@vant/area-data';

// 临时使用简化的地区数据
const areaList = {
  province_list: {
    110000: '北京市',
    120000: '天津市',
    130000: '河北省',
    310000: '上海市',
    320000: '江苏省',
    330000: '浙江省',
    440000: '广东省',
  },
  city_list: {
    110100: '北京市',
    120100: '天津市',
    130100: '石家庄市',
    310100: '上海市',
    320100: '南京市',
    330100: '杭州市',
    440100: '广州市',
  },
  county_list: {
    110101: '东城区',
    110102: '西城区',
    120101: '和平区',
    130102: '长安区',
    310101: '黄浦区',
    320102: '玄武区',
    330102: '上城区',
    440103: '荔湾区',
  }
};

const router = useRouter();
const route = useRoute();

// 是否编辑模式
const isEdit = ref(false);

// 地址信息
const addressInfo = ref({
  name: '',
  tel: '',
  province: '',
  city: '',
  county: '',
  addressDetail: '',
  areaCode: '',
  isDefault: false
});

// 保存地址
const onSave = async (content) => {
  // 验证表单
  if (!content.name) {
    showToast('请输入收货人姓名');
    return;
  }
  if (!content.tel) {
    showToast('请输入手机号');
    return;
  }
  if (!content.addressDetail) {
    showToast('请输入详细地址');
    return;
  }
  
  // 构造地址对象
  const address = {
    receiverName: content.name,
    receiverPhone: content.tel,
    province: content.province,
    city: content.city,
    district: content.county,
    detailAddress: content.addressDetail,
    isDefault: content.isDefault
  };
  
  // 调用API保存
  const result = await addAddressSafe(address);
  
  if (result !== null) {
    showToast({
      message: '保存成功',
      icon: 'success'
    });
    
    setTimeout(() => {
      router.back();
    }, 1000);
  }
};

// 删除地址
const onDelete = () => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这个地址吗？',
  }).then(() => {
    // TODO: 调用删除地址API
    showToast('删除功能开发中');
  }).catch(() => {});
};

// 返回
const goBack = () => {
  router.back();
};

onMounted(() => {
  // 如果有地址ID，则为编辑模式
  const addressId = route.query.id;
  if (addressId) {
    isEdit.value = true;
    // 从路由参数中加载地址详情
    addressInfo.value = {
      name: route.query.receiverName || '',
      tel: route.query.receiverPhone || '',
      province: route.query.province || '',
      city: route.query.city || '',
      county: route.query.district || '',
      addressDetail: route.query.detailAddress || '',
      areaCode: '',
      isDefault: route.query.isDefault === 'true' || route.query.isDefault === true
    };
  }
});
</script>

<style scoped>
.address-edit-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.address-form {
  padding-top: 46px;
}
</style>

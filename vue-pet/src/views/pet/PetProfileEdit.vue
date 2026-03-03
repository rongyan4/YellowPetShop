<template>
  <div class="pet-profile-edit">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="left-btn" @click="handleBack">
        <van-icon name="arrow-left" size="20" />
      </div>
      <div class="title">{{ isAddMode ? '添加档案' : '编辑档案' }}</div>
      <div class="save-btn" @click="handleSave">保存</div>
    </div>

    <!-- 表单内容 -->
    <div class="content">
      <!-- 头像上传 -->
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="handleUploadAvatar">
          <img 
            :src="formData.avatarUrl || getDefaultAvatar(formData.petType)" 
            alt="宠物头像"
          />
          <div class="avatar-mask">
            <van-icon name="photograph" size="24" color="#fff" />
          </div>
        </div>
        <div class="avatar-tip">点击更换头像</div>
        <input 
          ref="fileInput" 
          type="file" 
          accept="image/*" 
          style="display: none" 
          @change="handleFileChange"
        />
      </div>

      <!-- 基本信息 -->
      <div class="form-section">
        <div class="section-title">基本信息</div>
        
        <van-field
          v-model="formData.petName"
          label="宠物名称"
          placeholder="请输入宠物名称"
          required
          :rules="[{ required: true, message: '请输入宠物名称' }]"
        />
        
        <van-field
          v-model="formData.petType"
          label="宠物类型"
          placeholder="请选择宠物类型"
          required
          readonly
          is-link
          @click="showPicker('petType')"
        />
        
        <van-field
          v-model="formData.ageStage"
          label="年龄段"
          placeholder="请选择年龄段"
          required
          readonly
          is-link
          @click="showPicker('ageStage')"
        />
        
        <van-field
          v-model="formData.bodySize"
          label="体型"
          placeholder="请选择体型（可选）"
          readonly
          is-link
          @click="showPicker('bodySize')"
        />
        
        <van-field
          v-model="formData.gender"
          label="性别"
          placeholder="请选择性别（可选）"
          readonly
          is-link
          @click="showPicker('gender')"
        />
      </div>

      <!-- 体质特征 -->
      <div class="form-section">
        <div class="section-title">
          体质特征
          <span class="section-subtitle">（选择后我们将为您推荐合适的商品）</span>
        </div>
        
        <div class="health-options">
          <div 
            class="health-option" 
            v-for="option in healthOptions" 
            :key="option.key"
            @click="toggleHealth(option.key)"
          >
            <van-checkbox 
              :model-value="formData[option.key]"
              @click.stop="toggleHealth(option.key)"
            >
              {{ option.label }}
            </van-checkbox>
            <div class="option-tip">推荐：{{ option.recommend }}</div>
          </div>
        </div>
      </div>

      <!-- 生活习惯 -->
      <van-collapse v-model="activeNames" class="form-section">
        <van-collapse-item title="生活习惯（可选）" name="1">
          <van-field
            v-model="formData.activityLevel"
            label="活动量"
            placeholder="请选择活动量"
            readonly
            is-link
            @click="showPicker('activityLevel')"
          />
          
          <van-field
            v-model="formData.foodPreference"
            label="饮食偏好"
            placeholder="请选择饮食偏好"
            readonly
            is-link
            @click="showPicker('foodPreference')"
          />
          
          <van-field
            v-model="formData.remark"
            type="textarea"
            label="备注"
            placeholder="请输入备注信息（可选）"
            rows="3"
            maxlength="500"
            show-word-limit
          />
        </van-collapse-item>
      </van-collapse>

      <!-- 底部保存按钮 -->
      <div class="bottom-btn">
        <van-button 
          type="primary" 
          round 
          block 
          @click="handleSave"
          :loading="saving"
        >
          保存档案
        </van-button>
      </div>
    </div>

    <!-- 选择器 -->
    <van-action-sheet v-model:show="pickerVisible" title="请选择">
      <div class="picker-wrapper">
        <van-picker
          :columns="currentPickerColumns"
          @confirm="onPickerConfirm"
          @cancel="pickerVisible = false"
        />
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getPetProfileDetail, addPetProfile, updatePetProfile, uploadPetAvatar } from '@/api/petProfile';
import { showToast, showConfirmDialog } from 'vant';

const router = useRouter();
const route = useRoute();
const fileInput = ref(null);
const saving = ref(false);
const activeNames = ref([]);

// 选择器相关
const pickerVisible = ref(false);
const currentPickerType = ref('');
const currentPickerColumns = ref([]);

// 判断是添加还是编辑模式
const isAddMode = computed(() => route.path.includes('/add'));

// 表单数据
const formData = reactive({
  id: null,
  petName: '',
  petType: '',
  ageStage: '',
  bodySize: '',
  gender: '',
  avatarUrl: '',
  isShedding: false,
  isSkinSensitive: false,
  isStomachSensitive: false,
  hasDentalIssue: false,
  hasJointIssue: false,
  hasTearStain: false,
  isOverweight: false,
  isPickyEater: false,
  activityLevel: '',
  foodPreference: '',
  remark: ''
});

// 选择器选项配置
const pickerOptions = {
  petType: [
    { text: '狗', value: '狗' },
    { text: '猫', value: '猫' },
    { text: '兔子', value: '兔子' },
    { text: '仓鼠', value: '仓鼠' },
    { text: '龙猫', value: '龙猫' },
    { text: '鸟类', value: '鸟类' },
    { text: '其他', value: '其他' }
  ],
  ageStage: [
    { text: '幼年期（0-1岁）', value: '幼年期（0-1岁）' },
    { text: '成年期（1-7岁）', value: '成年期（1-7岁）' },
    { text: '老年期（7岁以上）', value: '老年期（7岁以上）' }
  ],
  bodySize: [
    { text: '迷你型（<5kg）', value: '迷你型（<5kg）' },
    { text: '小型（5-10kg）', value: '小型（5-10kg）' },
    { text: '中型（10-25kg）', value: '中型（10-25kg）' },
    { text: '大型（25-40kg）', value: '大型（25-40kg）' },
    { text: '巨型（>40kg）', value: '巨型（>40kg）' }
  ],
  gender: [
    { text: '公', value: '公' },
    { text: '母', value: '母' },
    { text: '未知', value: '未知' }
  ],
  activityLevel: [
    { text: '低（很少运动）', value: '低（很少运动）' },
    { text: '中（适量运动）', value: '中（适量运动）' },
    { text: '高（经常运动）', value: '高（经常运动）' },
    { text: '极高（运动员级别）', value: '极高（运动员级别）' }
  ],
  foodPreference: [
    { text: '干粮为主', value: '干粮为主' },
    { text: '湿粮为主', value: '湿粮为主' },
    { text: '生骨肉', value: '生骨肉' },
    { text: '自制鲜食', value: '自制鲜食' },
    { text: '混合喂养', value: '混合喂养' }
  ]
};

// 体质特征选项
const healthOptions = [
  { key: 'isShedding', label: '易掉毛体质', recommend: '美毛粮、化毛膏、除毛梳' },
  { key: 'isSkinSensitive', label: '皮肤敏感', recommend: '低敏粮、皮肤护理用品' },
  { key: 'isStomachSensitive', label: '肠胃敏感', recommend: '益生菌、易消化粮' },
  { key: 'hasDentalIssue', label: '口腔问题', recommend: '洁齿棒、口腔护理' },
  { key: 'hasJointIssue', label: '关节问题', recommend: '关节保健品、软骨素' },
  { key: 'hasTearStain', label: '泪痕问题', recommend: '去泪痕粮、眼部护理' },
  { key: 'isOverweight', label: '肥胖倾向', recommend: '减肥粮、控制零食' },
  { key: 'isPickyEater', label: '挑食', recommend: '适口性好的粮食、营养补充' }
];

// 显示选择器
const showPicker = (type) => {
  currentPickerType.value = type;
  currentPickerColumns.value = pickerOptions[type];
  pickerVisible.value = true;
};

// 选择器确认
const onPickerConfirm = ({ selectedOptions }) => {
  formData[currentPickerType.value] = selectedOptions[0].value;
  pickerVisible.value = false;
};

// 获取档案详情（编辑模式）
const fetchDetail = async () => {
  try {
    const id = route.params.id;
    const res = await getPetProfileDetail(id);
    if (res.code === 200) {
      Object.assign(formData, res.data);
    } else {
      showToast(res.message || '获取档案详情失败');
      router.back();
    }
  } catch (error) {
    console.error('获取档案详情失败:', error);
    showToast('获取档案详情失败');
    router.back();
  }
};

// 获取默认头像
const getDefaultAvatar = (petType) => {
  if (petType === 'cat') {
    return '/images/default_cat.png';
  } else if (petType === 'dog') {
    return '/images/default_dog.png';
  }
  return '/images/default_pet.png';
};

// 切换体质特征
const toggleHealth = (key) => {
  formData[key] = !formData[key];
};

// 上传头像
const handleUploadAvatar = () => {
  fileInput.value.click();
};

const handleFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  try {
    showToast({ message: '上传中...', duration: 0 });
    const res = await uploadPetAvatar(file);
    if (res.code === 200) {
      formData.avatarUrl = res.data;
      showToast('上传成功');
    } else {
      showToast(res.message || '上传失败');
    }
  } catch (error) {
    console.error('上传失败:', error);
    showToast('上传失败');
  }
};

// 取消
const handleCancel = async () => {
  try {
    await showConfirmDialog({
      title: '确认取消',
      message: '确定要取消编辑吗？未保存的内容将丢失。',
    });
    router.back();
  } catch (error) {
    // 用户点击了取消
  }
};

// 返回
const handleBack = () => {
  router.back();
};

// 保存
const handleSave = async () => {
  // 验证必填项
  if (!formData.petName || !formData.petName.trim()) {
    showToast('请输入宠物名称');
    return;
  }
  if (!formData.petType) {
    showToast('请选择宠物类型');
    return;
  }
  if (!formData.ageStage) {
    showToast('请选择年龄段');
    return;
  }

  try {
    saving.value = true;
    
    if (isAddMode.value) {
      // 添加模式
      const res = await addPetProfile(formData);
      if (res.code === 200) {
        showToast('添加成功');
        router.replace('/pet-profile');
      } else {
        showToast(res.message || '添加失败');
      }
    } else {
      // 编辑模式
      const res = await updatePetProfile(formData);
      if (res.code === 200) {
        showToast('保存成功');
        router.back();
      } else {
        showToast(res.message || '保存失败');
      }
    }
  } catch (error) {
    console.error('保存失败:', error);
    showToast('保存失败');
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  if (!isAddMode.value) {
    fetchDetail();
  }
});
</script>

<style scoped>
.pet-profile-edit {
  min-height: 100vh;
  background-color: #f7f5f0;
  padding-bottom: 80px;
}

.header {
  background-color: #fff;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.title {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.left-btn {
  display: flex;
  align-items: center;
  color: #333;
  cursor: pointer;
  padding: 4px;
}

.save-btn {
  font-size: 16px;
  cursor: pointer;
  color: #FF9800;
}

.content {
  padding: 20px;
}

.avatar-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 32px 20px;
  text-align: center;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.avatar-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 12px;
  position: relative;
  cursor: pointer;
  background-color: #f5f5f5;
  border: 4px solid #FFF3E0;
}

.avatar-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}

.avatar-tip {
  font-size: 14px;
  color: #999;
}

.form-section {
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding: 16px 20px 8px;
}

.section-subtitle {
  font-size: 12px;
  font-weight: 400;
  color: #999;
  margin-left: 8px;
}

.health-options {
  padding: 0 20px 16px;
}

.health-option {
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.health-option:last-child {
  border-bottom: none;
}

.health-option :deep(.van-checkbox) {
  width: 100%;
}

.health-option :deep(.van-checkbox__label) {
  font-size: 15px;
  line-height: 1.5;
}

.option-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  margin-left: 28px;
}

.bottom-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 20px;
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

.bottom-btn .van-button {
  background: linear-gradient(135deg, #FFB74D 0%, #FF9800 100%);
  border: none;
}

.picker-wrapper {
  padding: 20px 0;
}
</style>

# 宠物档案页面 API 文档

## 概述
本文档记录了宠物档案编辑页面（PetProfileEdit.vue）中使用的所有 API 接口。

---

## API 列表

### 1. 获取宠物档案详情
**接口名称：** `getPetProfileDetail`

**请求方式：** GET

**功能描述：** 获取指定宠物档案的详细信息（编辑模式使用）

**请求参数：**
- `id` (String/Number) - 宠物档案ID

**返回数据结构：**
```javascript
{
  code: 200,
  message: "成功",
  data: {
    id: Number,              // 档案ID
    petName: String,         // 宠物名称
    petType: String,         // 宠物类型
    ageStage: String,        // 年龄段
    bodySize: String,        // 体型
    gender: String,          // 性别
    avatarUrl: String,       // 头像URL
    isShedding: Boolean,     // 易掉毛体质
    isSkinSensitive: Boolean,    // 皮肤敏感
    isStomachSensitive: Boolean, // 肠胃敏感
    hasDentalIssue: Boolean,     // 口腔问题
    hasJointIssue: Boolean,      // 关节问题
    hasTearStain: Boolean,       // 泪痕问题
    isOverweight: Boolean,       // 肥胖倾向
    isPickyEater: Boolean,       // 挑食
    activityLevel: String,       // 活动量
    foodPreference: String,      // 饮食偏好
    remark: String              // 备注
  }
}
```

**使用场景：** 页面加载时，如果是编辑模式，调用此接口获取档案数据

---

### 2. 添加宠物档案
**接口名称：** `addPetProfile`

**请求方式：** POST

**功能描述：** 创建新的宠物档案

**请求参数：**
```javascript
{
  petName: String,         // 宠物名称（必填）
  petType: String,         // 宠物类型（必填）
  ageStage: String,        // 年龄段（必填）
  bodySize: String,        // 体型（可选）
  gender: String,          // 性别（可选）
  avatarUrl: String,       // 头像URL（可选）
  isShedding: Boolean,     // 易掉毛体质
  isSkinSensitive: Boolean,    // 皮肤敏感
  isStomachSensitive: Boolean, // 肠胃敏感
  hasDentalIssue: Boolean,     // 口腔问题
  hasJointIssue: Boolean,      // 关节问题
  hasTearStain: Boolean,       // 泪痕问题
  isOverweight: Boolean,       // 肥胖倾向
  isPickyEater: Boolean,       // 挑食
  activityLevel: String,       // 活动量（可选）
  foodPreference: String,      // 饮食偏好（可选）
  remark: String              // 备注（可选）
}
```

**返回数据结构：**
```javascript
{
  code: 200,
  message: "添加成功",
  data: {
    id: Number  // 新创建的档案ID
  }
}
```

**使用场景：** 用户在添加模式下点击保存按钮时调用

---

### 3. 更新宠物档案
**接口名称：** `updatePetProfile`

**请求方式：** PUT/POST

**功能描述：** 更新已有的宠物档案信息

**请求参数：**
```javascript
{
  id: Number,              // 档案ID（必填）
  petName: String,         // 宠物名称（必填）
  petType: String,         // 宠物类型（必填）
  ageStage: String,        // 年龄段（必填）
  bodySize: String,        // 体型（可选）
  gender: String,          // 性别（可选）
  avatarUrl: String,       // 头像URL（可选）
  isShedding: Boolean,     // 易掉毛体质
  isSkinSensitive: Boolean,    // 皮肤敏感
  isStomachSensitive: Boolean, // 肠胃敏感
  hasDentalIssue: Boolean,     // 口腔问题
  hasJointIssue: Boolean,      // 关节问题
  hasTearStain: Boolean,       // 泪痕问题
  isOverweight: Boolean,       // 肥胖倾向
  isPickyEater: Boolean,       // 挑食
  activityLevel: String,       // 活动量（可选）
  foodPreference: String,      // 饮食偏好（可选）
  remark: String              // 备注（可选）
}
```

**返回数据结构：**
```javascript
{
  code: 200,
  message: "保存成功",
  data: null
}
```

**使用场景：** 用户在编辑模式下点击保存按钮时调用

---

### 4. 上传宠物头像
**接口名称：** `uploadPetAvatar`

**请求方式：** POST (multipart/form-data)

**功能描述：** 上传宠物头像图片

**请求参数：**
- `file` (File) - 图片文件对象

**返回数据结构：**
```javascript
{
  code: 200,
  message: "上传成功",
  data: String  // 图片URL地址
}
```

**使用场景：** 用户点击头像区域选择图片后调用

---

## 字段说明

### 宠物类型 (petType)
- 可输入任意文本

### 年龄段 (ageStage)
- 可输入任意文本

### 体型 (bodySize)
- 可输入任意文本（可选）

### 性别 (gender)
- 可输入任意文本（可选）

### 活动量 (activityLevel)
- 可输入任意文本（可选）

### 饮食偏好 (foodPreference)
- 可输入任意文本（可选）

### 体质特征（布尔值字段）
- `isShedding` - 易掉毛体质
- `isSkinSensitive` - 皮肤敏感
- `isStomachSensitive` - 肠胃敏感
- `hasDentalIssue` - 口腔问题
- `hasJointIssue` - 关节问题
- `hasTearStain` - 泪痕问题
- `isOverweight` - 肥胖倾向
- `isPickyEater` - 挑食

---

## 错误处理

所有 API 调用失败时，会通过 `showToast` 显示错误提示信息。

常见错误码：
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未授权
- `404` - 资源不存在
- `500` - 服务器错误

---

## 导入路径

```javascript
import { 
  getPetProfileDetail, 
  addPetProfile, 
  updatePetProfile, 
  uploadPetAvatar 
} from '@/api/petProfile';
```

---

## 注意事项

1. 必填字段：`petName`（宠物名称）、`petType`（宠物类型）、`ageStage`（年龄段）
2. 头像上传支持的图片格式：image/*
3. 备注字段最大长度：500字符
4. 所有布尔类型字段默认值为 `false`
5. 编辑模式需要通过路由参数 `id` 获取档案详情
6. 添加模式通过路由路径判断（包含 `/add`）

---

## 更新日志

**2026-02-27**
- 移除所有弹出框选择器
- 将所有选择字段改为文本输入框
- 简化用户交互流程

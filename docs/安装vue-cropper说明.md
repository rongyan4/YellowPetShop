npmnpm# 安装 vue-cropper 依赖

## 方法1：使用 npm

```bash
cd vue-pet
npm install vue-cropper
```

## 方法2：使用 yarn

```bash
cd vue-pet
yarn add vue-cropper
```

## 安装完成后

重启前端开发服务器：

```bash
npm run serve
```

## 验证安装

安装完成后，可以通过以下命令验证：

```bash
npm list vue-cropper
```

应该看到类似输出：
```
vue-pet@0.1.0
└── vue-cropper@1.x.x
```

## 如果遇到问题

### 问题1：npm 安装失败

尝试清除缓存后重新安装：
```bash
npm cache clean --force
npm install vue-cropper
```

### 问题2：网络问题

使用淘宝镜像：
```bash
npm install vue-cropper --registry=https://registry.npmmirror.com
```

### 问题3：权限问题

使用管理员权限运行命令提示符或 PowerShell。

## 使用说明

安装完成后，图片裁剪功能将自动生效：

1. 进入商家端 → 商品管理
2. 点击"添加商品"或"编辑"
3. 在商品主图区域上传图片
4. 使用裁剪器调整图片为1:1比例
5. 确认裁剪后保存

## 注意事项

- vue-cropper 需要 Vue 3.x 版本
- 确保项目中已安装 vant 组件库
- 图片以 base64 格式存储，适合小图片
- 建议图片大小不超过 5MB

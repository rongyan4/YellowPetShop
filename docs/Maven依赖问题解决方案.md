# Maven 依赖问题排查和解决方案

## 问题描述
找不到 `PaginationInnerInterceptor` 类，说明 MyBatis-Plus 的扩展包没有正确下载。

## 解决步骤

### 方法 1：在 IDEA 中手动刷新（推荐）

1. **打开 Maven 工具窗口**
   - 点击 IDEA 右侧的 "Maven" 标签
   - 或者按 `Ctrl + Shift + A`，搜索 "Maven"

2. **清理并重新下载**
   - 在 Maven 窗口中，找到你的项目 `petserver`
   - 点击工具栏的 "Clean" 按钮（扫帚图标）
   - 然后点击 "Reload All Maven Projects" 按钮（刷新图标）

3. **等待下载完成**
   - 在 IDEA 底部可以看到下载进度
   - 等待所有依赖下载完成

### 方法 2：删除本地缓存重新下载

1. **关闭 IDEA**

2. **删除 MyBatis-Plus 的本地缓存**
   - 打开文件资源管理器
   - 导航到：`C:\Users\rongyan\.m2\repository\com\baomidou`
   - 删除整个 `baomidou` 文件夹

3. **重新打开 IDEA**
   - 打开项目
   - IDEA 会自动重新下载所有 MyBatis-Plus 依赖

### 方法 3：检查网络和 Maven 配置

1. **检查 Maven settings.xml**
   - 位置：`C:\Users\rongyan\.m2\settings.xml`
   - 确保配置了国内镜像（如阿里云）

2. **如果没有 settings.xml，创建一个**
   - 参考下面的配置文件

### 方法 4：使用命令行强制更新（如果有 Maven 命令）

在项目根目录执行：
```bash
mvn clean install -U
```

`-U` 参数会强制更新所有依赖。

## Maven settings.xml 配置示例

如果你的 Maven 下载很慢，可以配置阿里云镜像：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <mirrors>
        <mirror>
            <id>aliyunmaven</id>
            <mirrorOf>*</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

## 验证依赖是否下载成功

下载完成后，检查以下路径是否存在：
```
C:\Users\rongyan\.m2\repository\com\baomidou\mybatis-plus-extension\3.5.9\
```

应该包含：
- mybatis-plus-extension-3.5.9.jar
- mybatis-plus-extension-3.5.9.pom

## 如果以上方法都不行

可以尝试临时禁用分页插件，先让项目运行起来，然后再逐步解决依赖问题。

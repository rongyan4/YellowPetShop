# YellowPetShop 商品模拟数据生成提示词

## 使用说明

将下方提示词复制给 AI（如 DeepSeek、GPT-4 等），即可生成符合本项目数据库规范的商品模拟数据 SQL 脚本。

---

## 提示词正文

```
你是一个专业的数据库测试数据生成专家。请为一个宠物电商平台（YellowPetShop）生成商品（commodity）表的模拟数据，并以 MySQL INSERT SQL 语句的格式输出。

### 数据库说明

数据库名：petshop
字符集：utf8mb4

### 分类参考（category 表已有数据，外键约束）

| id | name   |
|----|--------|
| 1  | 狗粮   |
| 2  | 猫粮   |
| 3  | 零食   |
| 4  | 玩具   |
| 5  | 用品   |
| 6  | 保健品 |

### commodity 表字段说明

| 字段名           | 类型             | 说明                          | 约束/默认值                  |
|------------------|------------------|-------------------------------|------------------------------|
| id               | bigint           | 主键自增     | NOT NULL AUTO_INCREMENT      |
| category_id      | bigint           | 分类ID，引用上方 category 表  | 可为 NULL                    |
| name             | varchar(255)     | 商品名称，中文，真实宠物商品名 | 可为 NULL                   |
| price            | decimal(10,2)    | 商品价格，单位元              | 可为 NULL                    |
| unit             | varchar(255)     | 规格单位，如 500g/1kg/1件    | 可为 NULL                    |
| sold             | int              | 已售数量                      | 可为 NULL                    |
| stock            | int              | 库存数量                      | NOT NULL DEFAULT 0           |
| main_pic_url     | varchar(255)     | 主图URL（固定填写占位符）     | 见下方说明                   |
| msg              | varchar(255)     | 商品简短卖点描述，中文        | 可为 NULL                    |
| detail           | text             | 商品详情（HTML富文本）        | 可为 NULL                    |
| is_valid         | tinyint(1)       | 是否上架：1-上架 0-下架       | DEFAULT 1                    |
| shipping_origin  | varchar(255)     | 发货地，填中国省份或城市名    | DEFAULT '上海'               |
| postage          | decimal(10,2)    | 邮费，0.00 表示包邮           | DEFAULT 0.00                 |

### main_pic_url 字段说明（图片字段）

图片字段统一使用占位符格式：`/api/images/goods/mock_good.jpg`，无需生成真实图片路径。

### 数据生成要求

1. **数量**：共生成 30 条商品数据，主键自增。
2. **分类分布**：各分类平均随机波动分布
3. **name 字段**：使用真实的宠物商品名称风格，包含品牌、规格、卖点关键词，例如：
   - "皇家成猫猫粮2kg室内猫去毛球控制体重"
   - "比瑞吉无谷全价幼猫粮鸡肉蔓越莓1.5kg"
   - "狗狗磨牙棒洁齿骨宠物零食鸡肉味200g"
4. **price 字段**：范围在 5.00 到 899.00 之间，分布合理，零食/玩具偏低，主粮偏中高。
5. **unit 字段**：填写真实规格，如 `500g`、`1.5kg`、`2kg`、`5kg`、`10kg`、`1件`、`6支/盒` 等。
6. **sold 字段**：范围 0 到 50000，热销商品可高一些。
7. **stock 字段**：范围 0 到 9999，部分商品可设为 0（缺货）。
8. **msg 字段**：一句话中文卖点，20字以内，如 "高蛋白低脂肪，美毛增肌" 。
9. **detail 字段**：生成简单的 HTML 片段，包含商品介绍段落，约 100-200 字，用 `<p>` 标签包裹，内容真实贴合商品。
10. **is_valid 字段**：28 条为 1（上架），2 条为 0（下架）。
11. **shipping_origin 字段**：从以下城市随机选择：上海、北京、广州、深圳、成都、武汉、杭州、河北、山东、广东。
12. **postage 字段**：20 条为 0.00（包邮），10 条为 6.00（6元邮费）。

### 输出格式要求

1. 输出纯 MySQL INSERT 语句，不要输出任何说明文字或注释。
2. 每条数据一个 INSERT 语句，格式如下：
   ```sql
   INSERT INTO `commodity` VALUES (id, category_id, 'name', price, 'unit', sold, stock, 'main_pic_url', 'msg', 'detail_html', is_valid, 'shipping_origin', postage);
   ```
3. 字符串类型字段用单引号包裹，特殊字符（单引号）需转义为 `\'`。
4. NULL 值直接写 NULL，不用引号。
5. 在所有 INSERT 语句前加上：
   ```sql
   SET NAMES utf8mb4;
   -- YellowPetShop 商品模拟数据
   -- 生成时间：[当前日期]
   ```
6. 语句末尾加分号。

### 输出示例（参考格式，非实际数据）

```sql
INSERT INTO `commodity` VALUES (100, 2, '皇家成猫猫粮室内猫去毛球2kg', 89.00, '2kg', 3200, 500, '/api/images/goods/mock_100.jpg', '室内猫专用，有效控制毛球', '<p>皇家室内成猫粮，专为室内活动较少的猫咪设计，含有独特纤维配方，有效控制毛球形成，维持理想体重，富含优质蛋白质和必需脂肪酸，让猫咪毛发亮丽健康。</p>', 1, '上海', 0.00);
```

请严格按照上述要求生成 30 条 INSERT 语句。
```

---

## 附：快速使用步骤

1. 复制上方「提示词正文」中的内容（去掉外层代码块标记）。
2. 粘贴给 AI 助手（DeepSeek / GPT-4 / Claude 等）。
3. AI 会输出 30 条 SQL INSERT 语句。
4. 将输出的 SQL 保存为 `.sql` 文件，在 MySQL 中执行即可导入模拟数据。
5. 执行前请确保 `category` 表已有 id 1-6 的分类数据（petshop.sql 中已有）。

## 附：commodity 表字段完整速查

```
id               bigint          主键自增
category_id      bigint          分类ID（1狗粮 2猫粮 3零食 4玩具 5用品 6保健品）
name             varchar(255)    商品名称
price            decimal(10,2)   价格
unit             varchar(255)    规格单位
sold             int             已售数量
stock            int             库存数量
main_pic_url     varchar(255)    主图URL（图片字段，提示词中要求填占位符）
msg              varchar(255)    简短卖点
detail           text            详情富文本HTML
is_valid         tinyint(1)      是否上架 1/0
shipping_origin  varchar(255)    发货地
postage          decimal(10,2)   邮费
```

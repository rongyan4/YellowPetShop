模块拓扑图
```
HomeView.vue (主入口)
├── Header.vue (顶部导航栏)
├── Tabs (标签页切换)
│   └── Tab[0] - 推荐
│       └── section1.vue
│           ├── Swipe (轮播图组件)
│           │   └── SwipeItem (轮播图项)
│           └── Recommend.vue (推荐商品)
│               └── CommodityList.vue (商品列表)
│                   └── 商品卡片 (瀑布流布局)
└── Tabbar.vue (底部导航栏)
```
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/home",
    name: "home",
    component: () => import("../views/ShoppingView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path:"/",
    redirect: '/home'
  },
  {
    path: "/home",
    name: "home",
    component: () => import("../views/HomeView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/shopping",
    name: "shopping",
    component: () => import("../views/ShoppingView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/car",
    name: "car",
    component: () =>
      import("../views/CarView.vue"),
    meta: { requiresAuth: false } // 购物车页面自己处理登录弹窗
  },
  {
    path: "/my",
    name: "my",
    component: () =>
      import("../views/MyView.vue"),
    meta: { requiresAuth: false } // 个人中心页面自己处理登录弹窗
  },
  {
    path: "/profile",
    name: "profile",
    component: () =>
      import("../views/ProfileView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/account-manage",
    name: "account-manage",
    component: () =>
      import("../views/AccountManageView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/good",
    name: "good",
    component: () =>
      import("../views/GoodDetailsView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/good-details",
    name: "good-details",
    component: () =>
      import("../views/GoodDetailsView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/order/confirm",
    name: "order-confirm",
    component: () =>
      import("../views/OrderConfirmView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/order/list",
    name: "order-list",
    component: () =>
      import("../views/OrderListView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/my-orders",
    name: "my-orders",
    component: () =>
      import("../views/MyOrdersView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/my-favorites",
    name: "my-favorites",
    component: () =>
      import("../views/MyFavoritesView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/browse-history",
    name: "browse-history",
    component: () =>
      import("../views/BrowseHistoryView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/order/detail/:id",
    name: "order-detail",
    component: () =>
      import("../views/OrderDetailView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/address/list",
    name: "address-list",
    component: () =>
      import("../views/AddressListView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/address/edit",
    name: "address-edit",
    component: () =>
      import("../views/AddressEditView.vue"),
    meta: { requiresAuth: true }
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 路由守卫 - 已移除强制跳转登录页的逻辑
// 购物车和个人中心页面会自己处理登录弹窗
router.beforeEach((to, from, next) => {
  next();
});

export default router;

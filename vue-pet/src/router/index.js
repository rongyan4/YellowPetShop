import { createRouter, createWebHistory } from "vue-router";
import { getMerchantToken } from "@/utils/merchantAuth";

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
    path: "/order/comment/:id",
    name: "order-comment",
    component: () =>
      import("../views/OrderCommentView.vue"),
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
  // ==================== 商家端路由 ====================
  {
    path: "/merchant/login",
    name: "merchant-login",
    component: () => import("../views/merchant/MerchantLogin.vue"),
    meta: { requiresMerchantAuth: false }
  },
  {
    path: "/merchant",
    component: () => import("../views/merchant/MerchantLayout.vue"),
    meta: { requiresMerchantAuth: true },
    children: [
      {
        path: "",
        redirect: "/merchant/dashboard"
      },
      {
        path: "dashboard",
        name: "merchant-dashboard",
        component: () => import("../views/merchant/Dashboard.vue"),
        meta: { requiresMerchantAuth: true }
      },
      {
        path: "goods",
        name: "merchant-goods",
        component: () => import("../views/merchant/GoodsManagement.vue"),
        meta: { requiresMerchantAuth: true }
      },
      {
        path: "member",
        name: "merchant-member",
        component: () => import("../views/merchant/MemberManagement.vue"),
        meta: { requiresMerchantAuth: true }
      },
      {
        path: "order",
        name: "merchant-order",
        component: () => import("../views/merchant/OrderManagement.vue"),
        meta: { requiresMerchantAuth: true }
      },
      {
        path: "orders/:id",
        name: "merchant-order-detail",
        component: () => import("../views/merchant/OrderDetail.vue"),
        meta: { requiresMerchantAuth: true }
      },
      {
        path: "products/:id/comments",
        name: "merchant-product-comments",
        component: () => import("../views/merchant/ProductComments.vue"),
        meta: { requiresMerchantAuth: true }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 商家端路由守卫
  if (to.meta.requiresMerchantAuth) {
    const merchantToken = getMerchantToken();
    if (!merchantToken) {
      next('/merchant/login');
      return;
    }
  }
  
  next();
});

export default router;

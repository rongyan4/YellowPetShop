import { createRouter, createWebHistory } from "vue-router";
import { isAuthenticated } from "@/utils/auth";
import { showToast } from "vant";

const routes = [
  {
    path: "/home",
    name: "home",
    component: () => import("../views/ShoppingView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path:"/",
    redirect: '/newhome'
  },
  {
    path: "/newhome",
    name: "newhome",
    component: () => import("../views/HomeView.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/car",
    name: "car",
    component: () =>
      import("../views/CarView.vue"),
    meta: { requiresAuth: true } // 购物车需要登录
  },
  {
    path: "/my",
    name: "my",
    component: () =>
      import("../views/MyView.vue"),
    meta: { requiresAuth: true } // 个人中心需要登录
  },
  {
    path: "/login",
    name: "login",
    component: () =>
      import( "../views/LoginView.vue"),
    meta: { requiresAuth: false }
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 路由守卫 - JWT认证拦截
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.meta.requiresAuth) {
    // 检查用户是否已登录
    if (isAuthenticated()) {
      // 已登录，允许访问
      next();
    } else {
      // 未登录，跳转到登录页
      showToast({
        message: '请先登录',
        type: 'fail'
      });
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存目标路由，登录后可以跳转回来
      });
    }
  } else {
    // 不需要认证的路由，直接放行
    next();
  }
});

export default router;

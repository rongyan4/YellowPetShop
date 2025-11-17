import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";

const routes = [
  {
    path: "/home",
    name: "home",
    component: HomeView,
  },
  {
    path:"/",
    redirect: '/home'
  },
  {
    path: "/list",
    name: "list",

    component: () =>
      import( "../views/ListView.vue"),
  },
  {
    path: "/car",
    name: "car",

    component: () =>
      import("../views/CarView.vue"),
  },
  {
    path: "/my",
    name: "my",

    component: () =>
      import("../views/MyView.vue"),
  },
  {
    path: "/newhome",
    name: "newhome",
    component: () =>
      import( "../views/NewHomeView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;

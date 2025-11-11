import { createApp } from "vue";
import App from "@/App.vue";
import router from "@/router";
import store from "@/store";
import "@/assets/css/global.css";
import "@/assets/js/flexible.js";
import "@/assets/css/iconfont.css"
import "vant/lib/index.css"



const app = createApp(App)
app.use(store).use(router).mount("#app");

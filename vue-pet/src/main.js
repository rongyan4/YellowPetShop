import { createApp } from "vue";
import App from "@/App.vue";
import router from "@/router";
import pinia from "@/stores";
import "@/assets/css/global.css";
import "@/assets/js/flexible.js";
import "@/assets/css/iconfont.css"
import "vant/lib/index.css"
import { Button, NavBar, Icon, Checkbox, Overlay, Form, Field, CellGroup, Progress, Toast } from 'vant';



const app = createApp(App)
app.use(pinia)
   .use(router)
   .use(Button)
   .use(NavBar)
   .use(Icon)
   .use(Checkbox)
   .use(Overlay)
   .use(Form)
   .use(Field)
   .use(CellGroup)
   .use(Progress)
   .use(Toast)
   .mount("#app");

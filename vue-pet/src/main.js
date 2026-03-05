import { createApp } from "vue";
import App from "@/App.vue";
import router from "@/router";
import pinia from "@/stores";
import "@/assets/css/global.css";
import "@/assets/js/flexible.js";
import "@/assets/css/iconfont.css"
import "vant/lib/index.css"
import { 
  Button, 
  NavBar, 
  Icon, 
  Checkbox, 
  Overlay, 
  Form, 
  Field, 
  CellGroup, 
  Progress, 
  Toast, 
  DatePicker, 
  Popup, 
  Uploader,
  Swipe,
  SwipeItem,
  Cell,
  Image as VanImage,
  Tag,
  GoodsAction,
  GoodsActionIcon,
  GoodsActionButton,
  Badge,
  AddressList,
  AddressEdit,
  Area,
  Radio,
  RadioGroup,
  Stepper,
  Dialog,
  actionSheet
} from 'vant';



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
   .use(DatePicker)
   .use(Popup)
   .use(Uploader)
   .use(Swipe)
   .use(SwipeItem)
   .use(Cell)
   .use(VanImage)
   .use(Tag)
   .use(GoodsAction)
   .use(GoodsActionIcon)
   .use(GoodsActionButton)
   .use(Badge)
   .use(AddressList)
   .use(AddressEdit)
   .use(Area)
   .use(Radio)
   .use(RadioGroup)
   .use(Stepper)
   .use(Dialog)
   .use(actionSheet)
   .mount("#app");

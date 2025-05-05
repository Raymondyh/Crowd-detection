import { createApp } from 'vue';
import pinia from '/@/stores/index';
import App from './App.vue';
import router from './router';
import { i18n } from '/@/i18n/index';
import other from '/@/utils/other';
import 'animate.css/animate.min.css';

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import '/@/theme/index.scss';
import VueGridLayout from 'vue-grid-layout';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import '/@/theme/fonts/iconfont.css'

// ---------------------------------------------------
//                 应用初始化流程
// ---------------------------------------------------

// 创建 Vue 应用实例
const app = createApp(App);
// ================= 注册 Element Plus 图标 =================
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}

other.elSvg(app);

//app.use(pinia).use(router).use(ElementPlus, { i18n: i18n.global.t }).use(i18n).use(VueGridLayout).mount('#app');

// ================= 挂载插件/库 =================
app
	.use(pinia)// Pinia 状态管理
	.use(router)// Vue Router 路由
	.use(ElementPlus, { i18n: i18n.global.t })// Element Plus 组件库
	.use(i18n)// 国际化
    .use(VueGridLayout)// 网格布局系统
	.mount('#app');//挂载根组件


	// 创建应用实例 → 注册图标 → 挂载插件 → 挂载根组件
    //    │            │           │          │
    //    │            │           ├─ Pinia 状态管理
    //    │            │           ├─ Router 路由系统
    //    │            │           ├─ i18n 国际化
    //    │            │           ├─ ElementPlus UI
    //    │            │           └─ 自定义全局工具
    //    │            └─ 500+ Element 图标全局可用
    //    └─ 加载全局样式(动画库、主题覆盖、字体图标)
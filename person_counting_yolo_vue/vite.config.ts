import vue from '@vitejs/plugin-vue';//核心 Vue 插件，支持 .vue 文件解析
import { resolve } from 'path';
import { defineConfig, loadEnv, ConfigEnv } from 'vite';
import vueSetupExtend from 'vite-plugin-vue-setup-extend';//允许在 <script setup> 中扩展组件名（用于调试和自动注册）

const pathResolve = (dir: string) => {
	return resolve(__dirname, '.', dir);
};

const alias: Record<string, string> = {
	'/@': pathResolve('./src/'),
	'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js',
};

const viteConfig = defineConfig((mode: ConfigEnv) => {
	const env = loadEnv(mode.mode, process.cwd());
	return {
		plugins: [vue(), vueSetupExtend()],
		root: process.cwd(),
		//alias（路径别名）
		resolve: { alias },
		//环境变量和 base 路径配置
		base: mode.command === 'serve' ? './' : env.VITE_PUBLIC_PATH,
		//加速依赖预构建，避免 dev 时首次编译卡顿
		optimizeDeps: {
			include: ['element-plus/lib/locale/lang/zh-cn', 'element-plus/lib/locale/lang/en', 'element-plus/lib/locale/lang/zh-tw'],
		},
		//实现 前端请求跨域代理
		server: {
			host: '0.0.0.0',
			port: env.VITE_PORT as unknown as number,
			open: env.VITE_OPEN,
			hmr: true,
			proxy: {
				'/api': {
					//设置拦截器  拦截器格式   斜杠+拦截器名字，名字可以自己定
					target: 'http://localhost:9999/', //代理的目标地址
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/api/, ''),
				},
				'/flask': {
					//设置拦截器  拦截器格式   斜杠+拦截器名字，名字可以自己定
					target: 'http://localhost:5000/', //代理的目标地址
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/flask/, ''),
				},
			},
		},
		//build 构建优化
		build: {
			outDir: 'dist',
			chunkSizeWarningLimit: 1500,
			rollupOptions: {
				output: {
					entryFileNames: `assets/[name].[hash].js`,
					chunkFileNames: `assets/[name].[hash].js`,
					assetFileNames: `assets/[name].[hash].[ext]`,
					compact: true,
					manualChunks: {
						vue: ['vue', 'vue-router', 'pinia'],
						echarts: ['echarts'],
					},
				},
			},
		},
		css: { preprocessorOptions: { css: { charset: false } } },
		//define 定义全局变量
		define: {
			__VUE_I18N_LEGACY_API__: JSON.stringify(false),
			__VUE_I18N_FULL_INSTALL__: JSON.stringify(false),
			__INTLIFY_PROD_DEVTOOLS__: JSON.stringify(false),
			__VERSION__: JSON.stringify(process.env.npm_package_version),
		},
	};
});

export default viteConfig;

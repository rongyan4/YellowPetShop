const { defineConfig } = require("@vue/cli-service");

// 根据环境变量确定后端地址
const API_TARGET = process.env.VUE_APP_API_TARGET || 'http://0.0.0.0:3000';

module.exports = defineConfig({
	transpileDependencies: [],
	lintOnSave: false,
	publicPath: '/',
	devServer:{
		host: '0.0.0.0', // 允许外部访问
		port: 8080,
		allowedHosts: 'all', // 允许所有主机访问，解决内网IP访问403问题
		proxy: {
			'/api':{
				target: API_TARGET,
				changeOrigin: true,
				pathRewrite: {
				'^/api': '/api'
				},
				// 将后端 Set-Cookie 的域名改写为 localhost，确保浏览器能正确存储 Cookie
				cookieDomainRewrite: { '*': 'localhost' },
				onProxyReq: function(proxyReq, req, res) {
					// 禁用缓存
					proxyReq.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
					proxyReq.setHeader('Pragma', 'no-cache');
					proxyReq.setHeader('Expires', '0');
				},
				onProxyRes: function(proxyRes, req, res) {
					// 禁用响应缓存
					proxyRes.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate';
					proxyRes.headers['Pragma'] = 'no-cache';
					proxyRes.headers['Expires'] = '0';
					// 将 Set-Cookie 中的 Secure 属性去除，确保 http://localhost 下也能写入 Cookie
					if (proxyRes.headers['set-cookie']) {
						proxyRes.headers['set-cookie'] = proxyRes.headers['set-cookie'].map(
							cookie => cookie.replace(/;\s*Secure/gi, '')
						);
					}
				}
			},
			'/comment_image': {
				target: API_TARGET,
				changeOrigin: true,
				pathRewrite: {
					'^/comment_image': '/comment_image'
				}
			}
		},
	}
});
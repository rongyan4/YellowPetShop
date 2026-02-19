const { defineConfig } = require("@vue/cli-service");
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
				target: 'http://0.0.0.0:3000',
				changeOrigin: true,
				pathRewrite: {
				'^/api': '/api'
				},
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
				}
			},
			'/comment_image': {
				target: 'http://0.0.0.0:3000',
				changeOrigin: true,
				pathRewrite: {
					'^/comment_image': '/comment_image'
				}
			}
		},
	}
});
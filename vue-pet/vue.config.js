const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
	transpileDependencies: [],
	lintOnSave: false,
	devServer:{
		proxy: {
			'/api':{
				target: 'http://localhost:3000',
				changeOrigin: true,
				pathRewrite: {
				'^/api': '/api'
				}
			}
		},
	}
});
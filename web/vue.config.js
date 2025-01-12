const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  publicPath: './',

  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:18888/",
        changeOrigin: true,
        pathRewrite: {
          "^/api": ""
        }
      }
    }
  }
})

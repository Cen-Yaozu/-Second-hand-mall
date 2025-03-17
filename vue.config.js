const webpack = require('webpack')

module.exports = {
    publicPath: './',
    assetsDir: 'static',
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                "windows.jQuery": "jquery"
            })
        ]
    },
    devServer: {
        port: 8087,
        proxy: {
            '/idle': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost',
                onProxyReq: function(proxyReq, req, res) {
                    console.log('原始请求头:', req.headers);
                    console.log('代理请求头:', proxyReq.getHeaders());
                }
            },
            '/user': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/favorite': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/order': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/order-address': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/address': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/donation': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/message': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/file': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/image': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/alipay': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/admin': {
                target: 'http://localhost:8082',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            }
        }
    }
};

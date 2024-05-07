const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        '/api',// Проксировать только запросы, начинающиеся с /api
        createProxyMiddleware({
            target: 'http://localhost:5000', // Указать адрес вашего локального сервера
            changeOrigin: true,
        })
    );
};

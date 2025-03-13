import axios from 'axios';

/* axios功能封装  */

const service = axios.create({
    timeout: 5000,
    // 不需要baseURL，因为我们现在直接代理各个路径
    baseURL: '',
    withCredentials: true
});

// request interceptor(请求拦截器)
service.interceptors.request.use(
    config => {
        // 调试: 记录所有请求信息
        console.log('发送请求:', config.url);
        console.log('请求方法:', config.method);
        console.log('请求头:', config.headers);
        console.log('请求参数:', config.params || config.data);
        
        // 确保所有请求都带有正确的Content-Type
        if (config.method === 'post' && !config.headers['Content-Type']) {
            // 如果是提交表单数据，需要设置为application/x-www-form-urlencoded
            // 如果是提交JSON数据，需要设置为application/json
            config.headers['Content-Type'] = 'application/json';
        }
        
        return config;
    },
    error => {
        console.log('请求错误:', error);
        return Promise.reject(error);
    }
);

// response interceptor（接收拦截器）
service.interceptors.response.use(
    response => {
        // 调试: 记录响应信息
        console.log('响应状态:', response.status);
        console.log('响应头:', response.headers);
        console.log('响应数据:', response.data);
        
        if (response.status === 200) {
            return response.data;
        } else {
            console.error('响应错误:', response);
            return Promise.reject(response);
        }
    },
    error => {
        // 调试: 记录详细错误信息
        console.error('响应出错:', error);
        if (error.response) {
            console.error('错误状态码:', error.response.status);
            console.error('错误头信息:', error.response.headers);
            console.error('错误数据:', error.response.data);
        } else if (error.request) {
            console.error('未收到响应:', error.request);
        } else {
            console.error('请求配置错误:', error.message);
        }
        return Promise.reject(error);
    }
);

export default service;

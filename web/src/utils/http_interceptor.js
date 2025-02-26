import axios from 'axios';

// 创建axios实例
const http = axios.create({
    baseURL: '/api', // 你的API基地址
    timeout: 50000, // 请求超时时间
});

http.interceptors.response.use(
    response => {
        // 对响应数据进行处理
        // ...

        return response.data;
    },
    error => {
        // 对响应错误做些什么
        console.log(error);
        return Promise.reject(error);
    }
);

http.interceptors.response.use();
export default http;

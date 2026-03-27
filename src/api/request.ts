import axios from "axios";

const baseURL = import.meta.env.DEV 
  ? 'http://localhost:8080/api' 
  : '/api'   // 改成相对路径

//私人定制版request
//创建axios实例
const request=axios.create({
    baseURL:baseURL,
    timeout:5000
})

//请求拦截器：自动添加Token
request.interceptors.request.use(
    config=>{
        //根据请求路径判断用哪个token
        let token=null
        if(config.url?.startsWith('/admin')){
            token=localStorage.getItem('adminToken')
        }else{
            token=localStorage.getItem('userToken')
        }

        if(token){
            config.headers.Authorization=token
        }
        return config
    },error=>{
        return Promise.reject(error)
    }
)

//响应拦截器：统一处理401未登录
request.interceptors.response.use(
    response=>{
        return response
    },error=>{
        if(error.response?.status===401){
            //token过期无效，清除本地缓存并跳转到登录页
            localStorage.removeItem('adminToken')
            localStorage.removeItem('adminUser')
            window.location.href='/admin/login'
        }
        return Promise.reject(error)
    }
)

export default request






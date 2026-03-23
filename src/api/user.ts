import request from "./request";

//用户登录
export const userLogin=(data:{username:string;password:string})=>{
    return request.post('/user/login',data)
}

//用户注册
export const userRegister=(data:{username:string;password:string;phone:string})=>{
    return request.post('/user/register',data)
}
import request from "./request"

//员工数据类型
export interface Employee{
    id?:number //?可传可不传
    username: string
    name: string
    phone: string
    role: string
    status: number
    createTime?: string
    updateTime?: string
}

//基础url
const BASE_URL='http://localhost:8080/api/admin/employee'

//获取员工列表
export const getEmployeeList=()=>{
    return request.get(`${BASE_URL}/list`)
}

//根据id获取员工
export const getEmployeeById=(id:number)=>{
    return request.get(`${BASE_URL}/${id}`)
}

//新增员工
export const addEmployee=(data:Employee)=>{
    return request.post(BASE_URL,data)
}

//更新员工
export const updateEmployee=(data:Employee)=>{
    return request.put(BASE_URL,data)
}

//更新状态
export const updateEmployeeStatus=(id:number,status:number)=>{
    return request.put(`${BASE_URL}/status`,{id,status})
}

//重置密码
export const resetPassword=(id:number)=>{
    return request.put(`${BASE_URL}/reset-password`,{id})
}

//删除员工
export const deleteEmployee =(id:number)=>{
    return request.delete(`${BASE_URL}/${id}`)
}
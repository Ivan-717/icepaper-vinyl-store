import request from "./request";

//地址类型
export interface Address{
    id?:number
    receiver:string
    phone:string
    province:string
    city:string
    district:string
    detail:string
    isDefault:number
}


//获取地址列表
export const getAddressList=()=>{
    return request.get('user/address')
}

//新增地址
export const addAddress=(data:Address)=>{
    return request.post('user/address',data)
}

//更新地址
export const updateAddress=(data:Address)=>{
    return request.put('user/address',data)
}

//删除地址
export const deleteAddress=(id:number)=>{
    return request.delete(`user/address/${id}`)
}

//设为默认地址
export const setDefaultAddress=(id:number)=>{
    return request.put(`/user/address/default/${id}`)
}
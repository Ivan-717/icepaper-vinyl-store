import request from "./request";

//获取购物车列表
export const getCart=()=>{
    console.log('请求购物车,token:', localStorage.getItem('userToken'))
    return request.get('/user/cart')
}

//添加商品到购物车
export const addToCart=async(productId:number,quantity:number=1)=>{
    return request.post('/user/cart',{productId,quantity})
    
}

//修改购物车商品数量
export const updateCartQuantity=(id:number,quantity:number)=>{
    return request.put(`/user/cart/${id}`,{quantity})
}

//删除购物车商品
export const deleteCartItem=(id:number)=>{
    return request.delete(`/user/cart/${id}`)
}

//同步本地购物车
export const syncCart=(localCart:any[])=>{
    return request.post('/user/cart/sync',localCart)
}

//
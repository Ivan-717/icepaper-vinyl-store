import axios from 'axios'

//种类
export interface Category{
    id: number
    name: string
    sort: number
    createTime: string
    updateTime: string
}

//后端接口地址
const API_BASE = 'http://localhost:8080/api'
//创建axios实例
const request=axios.create({
    //自动给某个请求加上这个前缀
    baseURL:API_BASE,
    //等5秒没回应就放弃
    timeout:5000
})
//获取所有分类
//函数返回值类型():Promise<{data:Category[]}>
//promise:异步操作
export const getCategories=():Promise<{data:Category[]}>=>{
    //发get请求到http://localhost:8080/api/categories
    return request.get('/categories')
}

//商品信息
export interface Product{
    id: number
    categoryId: number
    name: string
    artist: string
    price: number
    stock: number
    image: string
    description: string
    status: number
    createTime: string
    updateTime: string
}

//按分类获取商品
export const getProductByCategory=(categoryId:number):Promise<{data:Product[]}> =>{
    return request.get(`/products/category/${categoryId}`)
}

//根据商品id获得详情
export const getProductById=(id:number):Promise<{data:Product}>=>{
    return request.get(`products/${id}`)
}

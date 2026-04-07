import { defineStore } from "pinia";
import { ref,computed } from "vue";
import { getCart, addToCart, updateCartQuantity, deleteCartItem, syncCart  } from "@/api/cart";
import request from "@/api/request";
import {nanoid} from 'nanoid'



export const useCartStore=defineStore('cart',()=>{
    const items=ref<any[]>([])
    const loading=ref(false)

    //加载购物车数据
    const loadCart=async()=>{
        const token=localStorage.getItem('userToken')

        //未登录,从localStorage加载
        if(!token){
            const localCart=JSON.parse(localStorage.getItem('cart')||'[]')
            items.value=localCart
            return
        }
        //已登录：后端加载
        loading.value=true
        try{
            const res=await getCart()
            console.log('res.data:', res.data)
            items.value=res.data
            console.log('items.value 赋值后:', items.value)
        }catch(error){
            console.error('加载购物车失败：',error)
        }finally{
            loading.value=false
        }
    }

    //添加商品
    const addItem =async(productId:number,quantity:number=1)=>{
        const token=localStorage.getItem('userToken')

        //未登录,从localStorage加载
        if(!token){
            const localCart=JSON.parse(localStorage.getItem('cart')||'[]')
            const existing=localCart.find((item:any)=>item.productId===productId)
            if(existing){
                existing.quantity+=quantity
            }else{
                localCart.push({
                    id: nanoid(),  // 临时 id
                    productId: productId,
                    quantity: quantity,
                    productName: '',
                    productArtist: '',
                    productPrice: 0,
                    productImage: ''
                })
            }
            localStorage.setItem('cart',JSON.stringify(localCart))
            items.value=localCart
            return
        }

        try {
              const token = localStorage.getItem('userToken')
              console.log('请求时带的token:', token)  // 加这行
              await addToCart(productId, quantity)
            } catch (error) {
              console.error('添加失败:', error)
              throw error
            }
    }

    //添加套餐
    const addCombo=async(combo:any,comboItems:any[])=>{
        const token=localStorage.getItem('userToken')
         console.log('发送套餐数据:', { comboId: combo.id, quantity: 1 })  // 加这行

        if(!token){
            const cart=JSON.parse(localStorage.getItem('cart')||'[]')
            //套餐作为一个特殊商品加入，标记isCombo=true
            cart.push({
                id:nanoid(),
                comboId:combo.id,
                productName: combo.name,        // 改成 productName
                productPrice: combo.price,      // 改成 productPrice
                productImage: combo.image,      // 改成 productImage
                quantity: 1,
                items: comboItems
            })
            localStorage.setItem('cart',JSON.stringify(cart))
            items.value=cart
            alert('套餐已加入购物车')
        }else{
            try{
                await request.post('/user/cart/combo',{
                    comboId: combo.id,
                    quantity:1
                })
                await loadCart()
                alert('套餐已加入购物车')
            }catch(error){
                console.error('加入购物车失败:', error)
                alert('操作失败')
            }
        }
    }

    //修改数量
    const updateQuantity=async(id:number,quantity:number)=>{
        const token=localStorage.getItem('userToken')

        //未登录,从localStorage加载
        if(!token){
            const localCart=JSON.parse(localStorage.getItem('cart')||'[]')
            const item=localCart.find((i:any)=>i.id===id)
            if(item){
                item.quantity=quantity
                localStorage.setItem('cart',JSON.stringify(localCart))
                items.value=localCart
            }
            return
        }

        try{
            await updateCartQuantity(id,quantity)
            await loadCart()
        }catch(error){
            console.error('更新失败：',error)
        }
    }

    //删除商品
    const removeItem=async(id:number)=>{
        const token=localStorage.getItem('userToken')

        //未登录,从localStorage加载
        if(!token){
            const localCart=JSON.parse(localStorage.getItem('cart')||'[]')
            const newCart=localCart.filter((i:any)=>i.id!==id)
            localStorage.setItem("cart",JSON.stringify(newCart))
            items.value=newCart
            return
        }

        try{
            await deleteCartItem(id)
            await loadCart()
        }catch(error){
            console.error("删除错误：",error)
        }
    }

    //同步本地购物车
    const syncLocalCart=async(localCart:any[])=>{
        if(localCart.length===0){
            return
        }

        const token=localStorage.getItem('userToken')
        if(!token){
            return
        }
        try{
            await syncCart(localCart)
            await loadCart()
        }catch(error){
            console.error("同步失败：",error)
        }
    }

    //总价
    const totalPrice=computed(()=>{
        //reduce():把数组里所有东西，累加变成一个数字
        //sum:累加的和  0是初始值
        return items.value.reduce((sum,item)=>sum+(item.productPrice*item.quantity),0)
    })

    //总数量
    const totalCount=computed(()=>{
        return items.value.reduce((sum,item)=>sum+item.quantity,0)
    })

    //清空购物车
    const clearCart=async()=>{
        const token=localStorage.getItem('userToken')

        //未登录,从localStorage加载
        if(!token){
           localStorage.removeItem('cart')
           items.value=[]
            return
        }

        try{
            await request.delete('/user/cart/all')
            items.value=[]
        }catch(error){
            console.error('清空失败:', error)
        }
    }

    return{
        items,
        loading,
        totalPrice,
        totalCount,
        clearCart,
        loadCart,
        addItem,
        updateQuantity,
        removeItem,
        syncLocalCart,
        addCombo
    }
})
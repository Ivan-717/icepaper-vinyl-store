import { defineStore } from "pinia";
import { ref,computed } from "vue";

export interface CartItem{
    id: number           
    name: string         
    artist: string       
    price: number        
    image: string        
    quantity: number     // 购买数量
}

export const useCartStore=defineStore('cart',()=>{
    const items=ref<CartItem[]>([])

    //从localStorage加载购物车数据
    const loadCart=()=>{
        const saved=localStorage.getItem('cart')
        if(saved){
            try{
                items.value=JSON.parse(saved)
            }catch(e){
                console.error('加载购物车失败：',e)
            }
        }
    }

    //保存购物车到localStorage
    const saveCart=()=>{
        localStorage.setItem('cart',JSON.stringify(items.value))
    }

    //立即加载
    loadCart()

    //计算属性
    const totalPrice =computed(()=>{
        //reduce:遍历数组  sum:累计值  0：reduce的第二个参数，sum的初始值
        return items.value.reduce((sum,item)=>sum+item.price*item.quantity,0)
    })

    //购物车商品总数
    const totalCount=computed(()=>{
        return items.value.reduce((sum,item)=>sum+item.quantity,0)
    })

    //操作方法
    //添加商品到购物车
    const addItem=(product:CartItem)=>{
        //检查商品是已在购物车中
        //存在，返回第一个匹配的商品对象
        //不存在，返回undefined
        const existing=items.value.find(item=>item.id===product.id)

        if(existing){
            existing.quantity+=product.quantity
        }else{
            items.value.push({...product})
        }

        saveCart()
    }

    //移除商品
    const removeItem=(productId:number)=>{
        //findIndex：返回第一个满足元素的索引个，无返回-1
        const index=items.value.findIndex(item=>item.id===productId)
        if(index>-1){
            //splice:第一个是起始索引，第二个是删除个数
            items.value.splice(index,1)
            saveCart()
        }
    }

    //更新商品数量
    const updateQuantity=(productId:number,quantity:number)=>{
        const item =items.value.find(item=>item.id===productId)
        if(item){
            if(quantity<=0){
                removeItem(productId)
            }else{
                item.quantity=quantity
                saveCart()
            }
        }

    }

    //清空购物车
    const clearCart=()=>{
        items.value=[]
        saveCart
    }

    return{
        items,
        totalPrice,
        totalCount,
        addItem,
        removeItem,
        updateQuantity
    }

})
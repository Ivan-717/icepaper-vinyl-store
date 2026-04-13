<script setup lang="ts">
import { ref,computed,onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cart';
import { getAddressList,type Address } from '@/api/address';
import request from '@/api/request';

const router=useRouter()
const cartStore=useCartStore()

const addressList=ref<Address[]>([])
const selectedAddress=ref<Address | null>(null)
const remark=ref('')

//购物车数据
//如果不用计算属性，则不能响应式
const cartItems=computed(()=>cartStore.items)
const totalPrice=computed(()=>cartStore.totalPrice)
const totalCount=computed(()=>cartStore.totalCount)



//跳转到地址管理
const goToAddress=()=>{
    router.push('/address')
}

//提交订单
const submitOrder=async()=>{
    //先检查店铺状态
    const isOpen=await checkShopStatus()
    if(!isOpen){
      return
    }

    if(!selectedAddress.value){
        alert('请选择收货地址')
        return
    }
    if(cartItems.value.length===0){
        alert('购物车为空')
        return
    }

    try{
        const res=await request.post('/user/order/create',{
            addressId:selectedAddress.value.id,
            remark:remark.value
        })

        if(res.data.success){
            alert('订单提交成功')
            //清空购物车
            await cartStore.loadCart()
            //跳转到订单列表
            router.push('/orders')
        }
    }catch (error) {
      console.error('完整错误对象:', error)
      const err = error as any
      alert(err.message || '提交订单失败')
    }
}

onMounted(()=>{
    loadAddress()
    cartStore.loadCart
})

// 地址选择
const selectedAddressId=ref<number | string>('')

//地址变化时
const onAddressChange=()=>{
    const addr=addressList.value.find(a=>a.id===selectedAddressId.value)
    selectedAddress.value=addr || null
}

//加载地址列表
const loadAddress=async()=>{
    try{
        const res=await getAddressList()
        addressList.value=res.data
        //默认选中默认地址
        const defaultAddr=addressList.value.find(a=>a.isDefault===1)
        if(defaultAddr){
            selectedAddressId.value=defaultAddr.id!
            selectedAddress.value=defaultAddr
        }else if(addressList.value.length>0){
            selectedAddress.value=addressList.value[0] || null
        }
    }catch (error) {
      console.error('加载地址失败:', error)
    }
}

//检查店铺状态
const checkShopStatus=async()=>{
  try{
    const res=await request.get('/shop/status')
    if(res.data.status!==1){
      alert('店铺已打烊，暂无法下单')
      return false
    }
    return true
  }catch (error) {
    console.error('获取店铺状态失败:', error)
    return false
  }
}

</script>

<template>
    <div class="checkout">
        <h1>确认订单</h1>

        <!-- 地址选择 -->
        <div class="address-section">
            <div class="section-title">
                <span>收货地址</span>
                <button @click="goToAddress" class="address-btn">管理地址</button>
            </div>

            <!-- 下拉选择地址 -->
            <select class="address-select" v-model="selectedAddressId" @change="onAddressChange">
                <option value="" disabled>请选择收货地址</option>
                <option v-for="addr in addressList" :key="addr.id" :value="addr.id">
                    {{ addr.receiver }} {{ addr.phone }} - {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
                </option>
            </select> 
      
        </div> 


        <!-- 商品列表 -->
        <div class="goods-section">
            <div class="section-title">商品清单</div>
            <div class="goods-list">
                <div v-for="item in cartItems" :key="item.id" class="goods-item">
                    <img :src="item.productImage" :alt="item.productName">
                    <div class="goods-info">
                        <h4>{{ item.productName }}</h4>
                        <p>¥{{ item.productPrice }}</p>
                    </div>
                    <div class="goods-price">
                        ¥{{ item.productPrice }} × {{ item.quantity }}
                    </div>
                    <div class="goods-subtotal">
                        ¥{{ (item.productPrice * item.quantity).toFixed(2) }}
                    </div>
                </div>
            </div>
        </div> 

        <!-- 订单备注 -->
        <div class="remark-section">
            <div class="section-title">订单备注</div>
            <textarea v-model="remark" placeholder="选填，可填写备注信息" rows="3"></textarea>
        </div>

        <!-- 底部结算栏 -->
        <div class="checkout-footer">
            <div class="total">
                共<span>{{ totalCount }}</span>件商品，合计：
                <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <button class="submit-btn" @click="submitOrder()">提交订单</button>
        </div> 


    </div>

</template>


<style scoped>
.checkout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: #fef9f9;
  min-height: 100vh;
  width: 100%;
}

h1 {
  color: #d96c6c;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.section-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #4a4a4a;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 管理地址按钮 */
.address-btn {
  background: white;
  border: 1px solid #ffb3b3;
  border-radius: 30px;
  padding: 6px 16px;
  color: #d96c6c;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s;
}

.address-btn:hover {
  background: #ffb3b3;
  color: white;
}

.address-card {
  background: white;
  border: 1px solid #ffe0e0;
  border-radius: 16px;
  padding: 1rem;
  margin-bottom: 1rem;
}

.no-address {
  background: #fff0f0;
  border: 1px dashed #ffb3b3;
  border-radius: 16px;
  padding: 1rem;
  text-align: center;
  color: #d96c6c;
  cursor: pointer;
}

.goods-list {
  background: white;
  border: 1px solid #ffe0e0;
  border-radius: 16px;
  overflow: hidden;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #ffe0e0;
  gap: 1rem;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-item img {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 12px;
}

.goods-info {
  flex: 2;
}

.goods-info h4 {
  margin: 0 0 4px;
  font-size: 1rem;
  color: #4a4a4a;
}

.goods-info p {
  margin: 0;
  color: #ff9b9b;
  font-size: 0.9rem;
  font-weight: 500;
}

.goods-price {
  width: 100px;
  text-align: center;
  color: #9e9e9e;
}

.goods-subtotal {
  width: 100px;
  text-align: right;
  font-weight: bold;
  color: #d96c6c;
}

.remark-section textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ffe0e0;
  border-radius: 16px;
  font-size: 0.9rem;
  resize: vertical;
  font-family: inherit;
  background: white;
}

.remark-section textarea:focus {
  outline: none;
  border-color: #ffb3b3;
}

/* 底部结算栏 */
.checkout-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #ffe0e0;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.total {
  font-size: 1rem;
  color: #4a4a4a;
}

.total span {
  font-weight: bold;
  color: #d96c6c;
}

.total-price {
  font-size: 1.4rem;
  font-weight: bold;
  color: #ff9b9b;
  margin-left: 0.5rem;
}

.submit-btn {
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 40px;
  padding: 0.8rem 2rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.3);
}

/* 响应式 */
@media (max-width: 768px) {
  .checkout {
    padding: 1rem;
  }
  
  .goods-item {
    flex-wrap: wrap;
  }
  
  .goods-price, .goods-subtotal {
    width: auto;
  }
  
  .checkout-footer {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }
}

.address-select {
  width: 100%;
  padding: 1rem;
  border: 1px solid #ffe0e0;
  border-radius: 16px;
  font-size: 1rem;
  background: white;
  color: #4a4a4a;
  margin-bottom: 1rem;
  cursor: pointer;
  font-weight: 500;
}

.address-select:focus {
  outline: none;
  border-color: #ffb3b3;
  box-shadow: 0 0 0 3px rgba(255, 179, 179, 0.2);
}
</style>
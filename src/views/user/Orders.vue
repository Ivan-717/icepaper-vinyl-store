<script setup lang="ts">
import { onMounted,ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/api/request';

const router=useRouter()

const orderList=ref<any[]>([])
const loading=ref(false)
const activeStatus=ref(0)

//状态选项
const statusTabs=[
    {label:'全部',value:0},
    {label:'待付款',value:1},
    {label:'待发货',value:2},
    {label:'待收货',value:3},
    {label:'已完成',value:4},
    {label:'已取消',value:5}
]

//状态文字
const getStatusText = (status: number) => {
    const map: Record<number, string> = {
      1: '待付款',
      2: '待发货',
      3: '待收货',
      4: '已完成',
      5: '已取消'
    }
    return map[status] || '未知'
}

//状态样式
const getStatusClass=(status:number)=>{
    const map:Record<number,string>={
        1:'status-pending',
        2: 'status-shipping',
        3: 'status-delivering',
        4: 'status-completed',
        5: 'status-cancelled'
    }
    return map[status] || ''
}

//加载订单列表
const loadOrders=async()=>{
    loading.value=true
    try{
        let url='/user/order/my'
        if(activeStatus.value!=0){
            url+=`?status=${activeStatus.value}`
        }
        const res=await request.get(url)
        //为每个订单加载详情
        for(const order of res.data){
            const detailRes=await request.get(`/user/order/${order.id}`)
            order.details=detailRes.data.details
            order.totalQuantity=order.details.reduce((sum:number,item:any)=>sum+item.quantity,0)
        }
        orderList.value=res.data
    }catch (error) {
      console.error('加载订单失败:', error)
    } finally {
      loading.value = false
    }
}

//切换状态
const changeStatus=(status:number)=>{
    activeStatus.value=status
    loadOrders()
}

//查看详情
const viewDetail=(orderId:number)=>{
    router.push(`/orders/${orderId}`)
}

//支付
const handlePay=async(orderId:number)=>{
  try{
    const res=await request.put(`/user/order/pay/${orderId}`)
    alert(res.data.message)
    //刷新订单状态
    loadOrders()
    //刷新订单列表页
    router.push('/orders')
  }catch(error){
    const err=error as any;
    alert(err.response?.data?.message || '支付失败')
  }
}

//取消订单
const handleCancel=async(orderId:number)=>{
  if(!confirm('确定要取消订单吗？')){
    return
  }
  try{
    const res=await request.put(`/user/order/cancel/${orderId}`)
    alert(res.data.message)
    loadOrders()
    router.push('/orders')
  }catch(error:any){
    alert(error.response?.data?.message || '取消失败')
  }
}

onMounted(()=>{
    loadOrders()
})
</script>

<template>
    <div class="orders-page">
        <h1>我的订单</h1>

        <!-- 状态筛选标签 -->
        <div class="status-tabs">
            <button v-for="tab in statusTabs" :key="tab.value"
             :class="{active:activeStatus===tab.value}" @click="changeStatus(tab.value)">
                {{ tab.label }}
            </button>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>
        
        <!-- 订单列表 -->
        <div v-else-if="orderList.length===0" class="empty">
            <p>暂无订单</p>
            <button class="go-shop-btn" @click="router.push('/')">去逛逛</button>
        </div> 


        <div v-else class="order-list">
            <div v-for="order in orderList" :key="order.id" class="order-card">
                <!-- 订单头部 -->
                <div class="order-header">
                    <span class="order-no">订单号：{{ order.orderNo }}</span>
                    <span class="order-status" :class="getStatusClass(order.status)">
                        {{ getStatusText(order.status) }}
                    </span>
                </div> 

                <!-- 订单商品列表 -->
                <div class="order-items">
                    <div class="order-item" v-for="item in order.details" :key="item.id">
                        <img :src="item.productImage" :alt="item.productName">
                        <div class="item-info">
                            <h4>{{ item.productName }}</h4>
                            <p>¥{{ item.price }} × {{ item.quantity }}</p>
                        </div>
                        <div class="item-total">
                             ¥{{ (item.price * item.quantity).toFixed(2) }}
                        </div>
                    </div>    
                </div> 

                <!-- 订单底部 -->
                <div class="order-footer">
                    <div class="order-total">
                        共<span>{{ order.totalQuantity }}</span>件商品，合计：
                        <span class="total-price">¥{{ order.totalAmount }}</span>
                    </div>
                    <div class="order-actions">
                        <button v-if="order.status===1" class="pay-btn" @click="handlePay(order.id)">
                            去支付
                        </button>
                        <button v-if="order.status===1" class="cancel-btn" @click="handleCancel(order.id)">取消</button>
                        <button class="detail-btn" @click="viewDetail(order.id)">查看详情</button>
                    </div>
                </div> 
                


            </div> 
        </div>
    </div> 

</template>


<style scoped>
.orders-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  background: #fef9f9;
  min-height: 100vh;
  width: 100%;
}

h1 {
  color: #d96c6c;
  margin-bottom: 2rem;
  font-size: 2rem;
}

/* 状态筛选标签 */
.status-tabs {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.status-tabs button {
  padding: 0.6rem 1.5rem;
  border: 1px solid #ffe0e0;
  background: white;
  border-radius: 40px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
  color: #9e9e9e;
  font-weight: 500;
}

.status-tabs button.active {
  background: #ffb3b3;
  color: white;
  border-color: #ffb3b3;
}

.loading, .empty {
  text-align: center;
  padding: 4rem;
  color: #9e9e9e;
  font-size: 1rem;
}

.go-shop-btn {
  margin-top: 1rem;
  padding: 0.6rem 1.8rem;
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
}

.go-shop-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #ffe0e0;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #fff5f5;
  border-bottom: 1px solid #ffe0e0;
}

.order-no {
  color: #4a4a4a;
  font-size: 0.9rem;
}

.order-status {
  font-size: 0.9rem;
  font-weight: 600;
  padding: 0.2rem 0.8rem;
  border-radius: 20px;
}

.status-pending { 
  color: #ff9b9b;
  background: #fff0f0;
}
.status-shipping { 
  color: #ff9800;
  background: #fff3e0;
}
.status-delivering { 
  color: #42b983;
  background: #e8f5e9;
}
.status-completed { 
  color: #999;
  background: #f5f5f5;
}
.status-cancelled { 
  color: #999;
  background: #f5f5f5;
}

.order-items {
  padding: 0.5rem 1rem;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.8rem;
  border-bottom: 1px solid #f5f5f5;
}

.order-item:last-child {
  border-bottom: none;
}

.order-item img {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 12px;
}

.item-info {
  flex: 2;
}

.item-info h4 {
  margin: 0 0 4px;
  font-size: 1rem;
  color: #4a4a4a;
  font-weight: 600;
}

.item-info p {
  margin: 0;
  color: #9e9e9e;
  font-size: 0.85rem;
}

.item-total {
  font-weight: bold;
  color: #d96c6c;
  font-size: 1rem;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-top: 1px solid #ffe0e0;
  background: white;
}

.order-total {
  font-size: 1rem;
  color: #4a4a4a;
}

.order-total span {
  font-weight: bold;
  color: #d96c6c;
}

.total-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #ff9b9b;
  margin-left: 0.3rem;
}

.order-actions {
  display: flex;
  gap: 0.8rem;
}

/* 通用按钮样式 */
.pay-btn, .cancel-btn, .detail-btn {
  padding: 0.5rem 1.2rem;
  border-radius: 40px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.3s;
}

/* 支付按钮 */
.pay-btn {
  background: #ffb3b3;
  color: white;
  border: none;
}

.pay-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
}

/* 取消按钮 */
.cancel-btn {
  background: white;
  border: 1px solid #ffb3b3;
  color: #d96c6c;
}

.cancel-btn:hover {
  background: #ffb3b3;
  color: white;
  transform: translateY(-2px);
}

/* 详情按钮 */
.detail-btn {
  background: white;
  border: 1px solid #ffb3b3;
  color: #d96c6c;
}

.detail-btn:hover {
  background: #ffb3b3;
  color: white;
  transform: translateY(-2px);
}
</style>
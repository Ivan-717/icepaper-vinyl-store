<script setup lang="ts">
import { onMounted,ref } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import request from '@/api/request';

const route=useRoute()
const router=useRouter()
const orderId=Number(route.params.id)

const order=ref<any>(null)
const details=ref<any[]>([])
const loading=ref(true)

//状态文字
const getStatusText = (status: number) => {
    const map: Record<number, string> = {
      1: '待付款',
      2: '已支付',
      3: '待发货',
      4: '待收货',
      5: '已完成',
      6: '已取消'
    }
    return map[status] || '未知'
}

//状态样式
const getStatusClass=(status:number)=>{
    const map:Record<number,string>={
        1:'status-pending',
        2: 'status-confirmed',
        3: 'status-shipping',
        4: 'status-delivering',
        5: 'status-completed',
        6: 'status-cancelled'
    }
    return map[status] || ''
}

//加载订单详情
const loadOrderDetail=async()=>{
    loading.value=true
    try{
        const res= await request.get(`/user/order/${orderId}`)
        console.log('返回数据：',res.data)
        order.value=res.data.order
        details.value=res.data.details
    }catch(error){
        console.error('加载订单详情失败:', error)
    }finally{
        loading.value=false
    }
}

//返回上一页
const goBack=()=>{
    router.back()
}

onMounted(()=>{
    loadOrderDetail()
})

//支付
const handlePay=async()=>{
  try{
    const res=await request.put(`/user/order/pay/${orderId}`)
    alert(res.data.message)
    //刷新订单状态
    loadOrderDetail()
    //刷新订单列表页
    router.push('/order')
  }catch(error){
    const err=error as any;
    alert(err.response?.data?.message || '支付失败')
  }
}

//取消订单
const handleCancel=async()=>{
  if(!confirm('确定要取消订单吗？')){
    return
  }
  try{
    const res=await request.put(`/user/order/cancel/${orderId}`)
    alert(res.data.message)
    loadOrderDetail()
    router.push('/orders')
  }catch(error:any){
    alert(error.response?.data?.message || '取消失败')
  }
}

const handleConfirm=async()=>{
  if(!confirm('确定已收到商品吗？')){
    return
  }
  try{
    await request.put(`/user/order/confirm/${orderId}`)
    alert('确认收货成功')
    loadOrderDetail()
    router.push('/orders')
  }catch (error) {
    alert('操作失败')
  }
}

</script>

<template>
    <div class="order-detail">
        <button class="back-btn" @click="goBack">← 返回</button>

        <div v-if="loading" class="loading">加载中...</div>

        <div v-else-if="order" class="detail-content">
        <!-- 订单状态 -->
        <div class="status-card" :class="getStatusClass(order.status)">
            <span class="status-text">{{ getStatusText(order.status) }}</span>
        </div> 

        <!-- 收货信息 -->
        <div class="info-card">
            <h3>收货信息</h3>
            <p><span class="label">收货人：</span>{{ order.receiver }}</p>
            <p><span class="label">联系电话:</span>{{ order.receiverPhone }}</p>
            <p><span class="label">收货地址:</span>{{ order.address }}</p>
            <p v-if="order.remark"><span class="label">备注：</span> {{ order.remark }}</p>
        </div> 

        <!-- 订单信息 -->
        <div class="info-card">
            <h3>订单信息</h3>
            <p><span class="label">订单号：</span>{{ order.orderNo }}</p>
            <p><span class="label">下单时间：</span>{{ order.createTime }}</p>
        </div> 

        <!-- 商品列表 -->
        <div class="info-card">
            <h3>商品清单</h3>
            <div class="goods-list">
                <div v-for="item in details" :key="item.id" class="goods-item">
                    <img :src="item.productImage" :alt="item.productName">
                    <div class="goods-info">
                        <h4>{{ item.productName }}</h4>
                        <p>¥{{ item.price }} × {{ item.quantity }}</p>
                    </div>
                    <div class="goods-price">
                        ¥{{ (item.price * item.quantity).toFixed(2) }}
                    </div>
                </div>
            </div>
        </div> 
        
        <!-- 订单总额 -->
        <div class="total-card">
            <span>共{{ details.length }}件商品，合计：</span>
            <span class="total-price">¥{{ order.totalAmount }}</span>
        </div> 

        <!-- 操作按钮 -->
        <div  class="action-buttons">
            <button v-if="order.status===1" class="pay-btn" @click="handlePay">去支付</button>
            <button v-if="order.status===1" class="cancel-btn" @click="handleCancel">取消订单</button>
            <button v-if="order.status===4" class="confirm-btn" @click="handleConfirm">确认收货</button>
        </div> 

      


        </div>

        <div v-else class="not-found">
            订单不存在
        </div>
    </div>


</template>

<style scoped>
.order-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  background: #fef9f9;
  min-height: 100vh;
  width: 100%;
}

.back-btn {
  background: white;
  border: 1px solid #ffb3b3;
  border-radius: 30px;
  padding: 6px 16px;
  cursor: pointer;
  color: #d96c6c;
  margin-bottom: 1.5rem;
}

.back-btn:hover {
  background: #ffb3b3;
  color: white;
}

.loading, .not-found {
  text-align: center;
  padding: 3rem;
  color: #9e9e9e;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.status-card {
  text-align: center;
  padding: 1rem;
  border-radius: 16px;
  margin-bottom: 0.5rem;
}

.status-text {
  font-size: 1.2rem;
  font-weight: bold;
}

.status-pending { color: #ff9b9b; background: #fff0f0; }
.status-confirmed { color: #42b983; background: #e8f5e9; }
.status-shipping { color: #ff9800; background: #fff3e0; }
.status-delivering { color: #2196f3; background: #e3f2fd; }
.status-completed { color: #999; background: #f5f5f5; }
.status-cancelled { color: #999; background: #f5f5f5; }

.info-card {
  background: white;
  border-radius: 16px;
  padding: 1rem;
  border: 1px solid #ffe0e0;
}

.info-card h3 {
  margin: 0 0 0.8rem;
  font-size: 1rem;
  color: #d96c6c;
}

.info-card p {
  margin: 0.4rem 0;
  font-size: 0.9rem;
  color: #4a4a4a;
}

.info-card .label {
  color: #9e9e9e;
  margin-right: 0.5rem;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f5f5f5;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-item img {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 8px;
}

.goods-info {
  flex: 2;
}

.goods-info h4 {
  margin: 0 0 4px;
  font-size: 0.95rem;
  color: #4a4a4a;
}

.goods-info p {
  margin: 0;
  color: #9e9e9e;
  font-size: 0.8rem;
}

.goods-price {
  font-weight: bold;
  color: #d96c6c;
}

.total-card {
  background: white;
  border-radius: 16px;
  padding: 1rem;
  text-align: right;
  font-size: 1rem;
  border: 1px solid #ffe0e0;
  margin-top: 0.5rem;
}

.total-price {
  font-size: 1.3rem;
  font-weight: bold;
  color: #ff9b9b;
  margin-left: 0.5rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 0.5rem;
}

.pay-btn, .cancel-btn {
  padding: 0.5rem 1.5rem;
  border-radius: 40px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
}

.pay-btn {
  background: #ffb3b3;
  color: white;
  border: none;
}

.pay-btn:hover {
  background: #ff9b9b;
}

.cancel-btn {
  background: white;
  border: 1px solid #ffb3b3;
  color: #d96c6c;
}

.cancel-btn:hover {
  background: #ffb3b3;
  color: white;
}

.confirm-btn {
  background: #ffb3b3;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 40px;
  cursor: pointer;
}

.confirm-btn:hover {
  background: #ff9b9b;
}
</style>
<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';

//订单数据类型
interface Order{
    id:number,
    orderNo:string,
    userId:number,
    totalAmount:number,
    status:number,
    createTime:string
    updateTime:string
    receiver: string        // 收货人
    receiverPhone: string   // 收货人电话
    address: string         // 收货地址
    remark?: string         // 备注（可选）
}

//订单明细类型
interface OrderDetail{
    id:number
    orderId:number
    productId:number
    productName:string
    price:number
    quantity:number
}

const orderList=ref<Order[]>([])
const loading=ref(false)
const activeStatus=ref(0) //0=全部
const detailVisible=ref(false)
const currentOrder=ref<Order|null>(null)
const orderDetails=ref<OrderDetail[]>([])

//状态映射
const statusMap: Record<number,string>={
    1: '待付款',
    2: '待接单',
    3: '已接单',
    4: '派送中',
    5: '已完成',
    6: '已取消'
}

//加载订单列表
const loadOrders=async()=>{
    loading.value=true
    try{
        let url='/admin/orders'
        if(activeStatus.value!==0){
            url+=`?status=${activeStatus.value}`
        }
        const res=await request.get(url)
        orderList.value=res.data
    }catch(error){
        console.error('加载订单失败:',error)
    }finally{
        loading.value=false
    }
}

//切换状态筛选
const changeStatus=(status:number)=>{
    activeStatus.value=status
    loadOrders()
}

//查看订单详情
const showDetail=async(order:Order)=>{
    currentOrder.value=order
    try{
        const res=await request.get(`/admin/orders/${order.id}`)
        orderDetails.value=res.data.details
        detailVisible.value=true
        console.log(orderDetails.value)
    }catch(error){
        console.error('加载订单详情失败:',error)
    }
}


//更新订单状态
const updateStatus=async(orderId:number,newStatus:number)=>{
    try{
        await request.put(`/admin/orders/${orderId}/status`,{status:newStatus})
        loadOrders()
    }catch(error){
        console.error('更新状态失败:',error)
    }
}

//定义操作按钮的类型
interface ActionButton{
    text:string
    action:()=>void
}

//获取操作按钮文字和动作
const getActionButton=(order:Order):ActionButton|null=>{
    switch(order.status){
        case 2:
            return{text:'接单',action:()=>updateStatus(order.id,3)}
        case 3:
            return{text:'派送',action:()=>updateStatus(order.id,4)}
        case 4:
            return{text:'完成',action:()=>updateStatus(order.id,5)}        
        default:
            return null
    }
}

//获取状态样式
const getStatusClass=(status:number)=>{
    //样式名
    return `status-${status}`
}


onMounted(()=>{
    loadOrders()
})

</script>

<template>
    <div class="order">
        <div class="header">
            <h1>📋 订单管理</h1>
        </div>

        <!-- 状态筛选标签 -->
        <div class="status-tabs">
            <button :class="{active:activeStatus===0}" @click="changeStatus(0)">
                全部
            </button>
            <button :class="{active:activeStatus===2}" @click="changeStatus(2)">
                待接单
            </button>
            <button :class="{active:activeStatus===3}" @click="changeStatus(3)">
                已接单
            </button>
            <button :class="{active:activeStatus===4}" @click="changeStatus(4)">
                派送中
            </button>
            <button :class="{active:activeStatus===5}" @click="changeStatus(5)">
                已完成
            </button>
        </div> 

        <div v-if="loading" class="loading">
            加载中...
        </div>

        <!-- 订单表格 -->
        <div v-else class="table-container">
            <table class="order-table">
                <thead>
                    <tr>
                        <th>订单号</th>
                        <th>用户ID</th>
                        <th>总金额</th>
                        <th>状态</th>
                        <th>下单时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="order in orderList" :key="order.id">
                        <td>{{ order.orderNo }}</td>
                        <td>{{ order.userId }}</td>
                        <td>¥{{ order.totalAmount }}</td>
                        <td>
                            <span class="status-badge" :class="getStatusClass(order.status)">
                                {{ statusMap[order.status] }}
                            </span>
                        </td>
                        <td>{{ order.createTime }}</td>
                        <td class="actions">
                            <button class="detail-btn" @click="showDetail(order)">
                                详情
                            </button>
                             <!-- .action取对象里的action函数 -->
                            <button v-if="getActionButton(order)"
                                class="action-btn"
                                @click="getActionButton(order)!.action">
                                {{ getActionButton(order)!.text }}
                            </button>
                        </td>
                        
                    </tr>
                </tbody>
            </table>
        </div> 

        <!-- 订单详情弹窗 -->
        <div v-if="detailVisible" class="modal-overlay" @click.self="detailVisible=false">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>订单详情</h2>
                    <button class="close-btn" @click="detailVisible=false">✕</button>
                </div>

                <div class="modal-body" v-if="currentOrder">
                    <div class="order-info">
                        <p><strong>订单号:</strong>{{ currentOrder.orderNo }}</p>
                        <p><strong>总金额:</strong>{{ currentOrder.totalAmount }}</p>
                        <p><strong>状态:</strong>{{ statusMap[currentOrder.status] }}</p>
                        <p><strong>下单时间:</strong>{{ currentOrder.createTime }}</p>

                        <!-- 收货信息 -->
                        <p><strong>收货人:</strong>{{ currentOrder.receiver }}</p> 
                        <p><strong>联系电话：</strong>{{ currentOrder.receiverPhone }}</p>
                        <p><strong>收货地址：</strong>{{ currentOrder.address }}</p>
                        <p v-if="currentOrder.remark"><strong>备注：</strong>{{ currentOrder.remark }}</p>
                    </div>
                
                    <h3>商品列表</h3>
                    <table class="detail-table">
                        <thead>
                            <tr>
                                <th>商品名称</th>
                                <th>单价</th>
                                <th>数量</th>
                                <th>小计</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="item in orderDetails" :key="item.id">
                                <td>{{ item.productName }}</td>
                                <td>{{ item.price }}</td>
                                <td>{{ item.quantity }}</td>
                                <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>    
            </div>
        </div> 
    </div>
</template>




<style scoped>
.order {
  padding: 1rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0;
  color: #333;
}

/* 状态筛选标签 */
.status-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.status-tabs button {
  padding: 0.5rem 1.5rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.status-tabs button:hover {
  background: #f5f5f5;
}

.status-tabs button.active {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow-x: auto;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;
}

.order-table th {
  background: #f5f5f5;
  padding: 1rem;
  text-align: left;
  font-weight: 500;
  color: #333;
  border-bottom: 2px solid #ddd;
}

.order-table td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.order-table tbody tr:hover {
  background: #f9f9f9;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
}

.status-badge.status-1 { background: #fff3e0; color: #ef6c00; }
.status-badge.status-2 { background: #ffebee; color: #c62828; }
.status-badge.status-3 { background: #e8f5e9; color: #2e7d32; }
.status-badge.status-4 { background: #e3f2fd; color: #1565c0; }
.status-badge.status-5 { background: #e8eaf6; color: #3949ab; }
.status-badge.status-6 { background: #f5f5f5; color: #757575; }

.actions {
  display: flex;
  gap: 0.5rem;
}

.detail-btn, .action-btn {
  padding: 0.25rem 0.75rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.detail-btn:hover {
  border-color: #42b983;
  color: #42b983;
}

.action-btn {
  border-color: #ff9800;
  color: #ff9800;
}

.action-btn:hover {
  background: #ff9800;
  color: white;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 1.5rem;
}

.order-info {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.order-info p {
  margin: 0.5rem 0;
}

.detail-table {
  width: 100%;
  border-collapse: collapse;
}

.detail-table th,
.detail-table td {
  padding: 0.5rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.detail-table th {
  background: #f5f5f5;
}
</style>
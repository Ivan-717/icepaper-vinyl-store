<script setup lang="ts">
import { onMounted, ref } from 'vue';
import axios from 'axios';

//当前状态:1营业 0打烊
const status=ref(1)
const loading=ref(false)

//获取当前状态
const fetchStatus=async()=>{
    try{
        const res=await axios.get('http://localhost:8080/api/admin/shop/status')
        status.value=res.data.status
    }catch(error){
        console.error('获取状态失败：',error)
    }
}

//切换状态 toggle:切换
const toggleStatus=async()=>{
    loading.value=true
    try{
        const newStatus=status.value===1?0:1
        //{ status: newStatus }是请求体，是发给后端的数据
        await axios.put('http://localhost:8080/api/admin/shop/status',{
            status:newStatus
        })

        status.value=newStatus
    }catch(error){
        console.error('更新状态失败：',error)
        alert('操作失败，请重试！')
    }finally{
        loading.value=false
    }
}

onMounted(()=>{
    fetchStatus()
})

</script>

<template>
    <div class="status-card">
        <h1>🏪 营业状态管理</h1>

        <div class="status-card">
            <div class="current-status">
                <span class="status-label">当前状态</span>
                <!-- class是基础样式，:class是差异化样式 -->
                <span class="status-value" 
                    :class="{'status-open':status===1,'status-close':status===0}">
                    {{ status===1 ?'营业中' : '打烊中' }}
                </span>
            </div>

            <button class="toggle-btn"
                :class="{'btn-open':status===1,'btn-closed':status===0}"
                @click="toggleStatus"
                :disabled="loading">
                <span class="btn-text">
                    {{ loading ? '操作中...' :(status===1 ?'打烊' : '营业') }}
                </span>
                <span class="btn-icon">
                    {{ status===1? '🌙' : '☀️' }}
                </span>
            </button>

            <p class="tip">
                {{ status===1?'店铺正在营业，用户可正常下单':'店铺已打烊，用户无法下单' }}
            </p>
        </div>
    </div>
</template>

<style scoped>
.shop-status {
  max-width: 800px;
  margin: 0 auto;
}

.shop-status h1 {
  margin-bottom: 2rem;
  color: #333;
}

.status-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  text-align: center;
}

.current-status {
  margin-bottom: 2rem;
  font-size: 1.2rem;
}

.status-label {
  color: #666;
  margin-right: 0.5rem;
}

.status-value {
  font-weight: bold;
  padding: 0.25rem 1rem;
  border-radius: 20px;
  display: inline-block;
}

.status-open {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-closed {
  background: #ffebee;
  color: #c62828;
}

.toggle-btn {
  position: relative;
  width: 200px;
  height: 60px;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 1.2rem;
  font-weight: 500;
}

.btn-open {
  background: #2e7d32;
  color: white;
}

.btn-closed {
  background: #c62828;
  color: white;
}

.toggle-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.toggle-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 1.5rem;
}

.tip {
  color: #666;
  font-size: 0.9rem;
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 8px;
}
</style>
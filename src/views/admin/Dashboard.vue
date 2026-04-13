<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';

//统计数据
const stats=ref({
    productCount:0,
    comboCount:0,
    employCount:0,
    todayOrder:0,
    todayRevenue:0
})

const loading=ref(false)

//加载统计数据
const loadStats=async()=>{
    loading.value=true
    try{
        //promise all:同时发，不用一条一条
        const [products,combos,employees,todayStats]=await Promise.all([
            request.get('/admin/products'),
            request.get('/admin/combo'),
            request.get('/admin/employee/list'),
            request.get('/admin/orders/today-stats')
        ])

        console.log('todayStats.data:', todayStats.data)

        stats.value.productCount=products.data.length
        stats.value.comboCount=combos.data.length
        stats.value.employCount=employees.data.length
        stats.value.todayOrder=todayStats.data.count
        stats.value.todayRevenue=todayStats.data.total
    }catch(error){
        console.error('加载统计数据失败：',error)
    }finally{
        loading.value=false
    }
}

onMounted(()=>{
    loadStats()
})

</script>

<template>
    <div class="dashboard">
        <h1>📊工作台</h1>
        
        <div v-if="loading" class="loading">
            ⏳ 加载中...
        </div>

        <div v-else class="stats-grids">
            <!-- 商品总数卡片 -->
            <div class="stat-card">
                <div class="stat-icon">💿</div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.productCount }}</div>
                    <div class="stat-label">商品总数</div>
                </div>
            </div> 

            <!-- 套餐总数卡片 -->
            <div class="stat-card">
                <div class="stat-icon">💿</div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.comboCount }}</div>
                    <div class="stat-label">套餐总数</div>
                </div>
            </div> 


            <!-- 员工总数卡片 -->
            <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.employCount }}</div>
                    <div class="stat-label">员工总数</div>
                </div>
            </div> 

            <!-- 今日订单卡片 -->
            <div class="stat-card">
                <div class="stat-icon">📋</div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.todayOrder }}</div>
                    <div class="stat-label">今日订单数</div>
                </div>
            </div> 

            <!-- 今日营业额卡片 -->
            <div class="stat-card revenue-card">
                <div class="stat-icon">💰</div>
                <div class="stat-info">
                    <div class="stat-value">¥{{ stats.todayRevenue }}</div>
                    <div class="stat-label">今日营业额</div>
                </div>
            </div> 

        </div>
    </div>
</template>


<style scoped>
.dashboard {
  padding: 1rem;
}

.dashboard h1 {
  margin-bottom: 2rem;
  color: #333;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 2.5rem;
  background: #f5f5f5;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
  margin-top: 0.25rem;
}

/* 营业额卡片占一整行 */
.revenue-card {
  grid-column: span 4;
  background: linear-gradient(135deg, #42b983 0%, #2c3e50 100%);
  color: white;
}

.revenue-card .stat-icon {
  background: rgba(255, 255, 255, 0.2);
}

.revenue-card .stat-value,
.revenue-card .stat-label {
  color: white;
}

/* 小屏幕适配 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .revenue-card {
    grid-column: span 2;
  }
}
</style>
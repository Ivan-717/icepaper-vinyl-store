<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';
import { useRouter } from 'vue-router';

const router=useRouter()
const comboList=ref<any[]>([])
const loading=ref(false)

const loadCombos=async()=>{
    loading.value=true
    try{
        const res=await request.get('/combo/list')
        //只显示上架的套餐
        comboList.value=res.data.filter((c:any)=>c.status===1)
    }catch(error){
        console.error('加载套餐失败:', error)
    }finally {
        loading.value = false
    }
}

onMounted(()=>{
    loadCombos()
})

</script>

<template>
    <div class="combos-page">
        <h1>🍽️ 精选套餐</h1>

        <div v-if="loading" class="loading">
            加载中...
        </div>

        <div v-else-if="comboList.length===0" class="empty">
            <p>暂无套餐</p>
        </div>

        <div v-else class="combo-list">
            <div v-for="combo in comboList" :key="combo.id" class="combo-card" @click="router.push(`/combo/${combo.id}`)">
                <img :src="combo.image" :alt="combo.name">
                <div class="combo-info">
                    <h3>{{ combo.name }}</h3>
                    <p class="description">{{ combo.description }}</p>
                    <p class="price">¥{{ combo.price }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.combos-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  background: #fef9f9;
  min-height: 100vh;
  width: 100%;
}

.combos-page h1 {
  color: #d96c6c;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.loading, .empty {
  text-align: center;
  padding: 3rem;
  color: #ffb3b3;
}

/* 套餐列表 - 一行4个 */
.combo-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.combo-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ffe0e0;
  display: flex;
  flex-direction: column;
}

.combo-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 179, 179, 0.15);
}

.combo-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.combo-info {
  padding: 1rem;
}

.combo-card h3 {
  font-size: 1rem;
  color: #4a4a4a;
  margin: 0 0 0.5rem;
}

.combo-card .description {
  color: #9e9e9e;
  font-size: 0.85rem;
  margin: 0 0 0.5rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.combo-card .price {
  color: #ff9b9b;
  font-weight: bold;
  font-size: 1.2rem;
  margin-top: 0.5rem;
}

/* 响应式 */
@media (max-width: 1200px) {
  .combo-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 900px) {
  .combo-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 600px) {
  .combo-list {
    grid-template-columns: 1fr;
  }
}
</style>
<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import request from '@/api/request';
import { useCartStore } from '@/stores/cart';

const route=useRoute()
const router=useRouter()
const cartStore=useCartStore()
const comboId=Number(route.params.id)
console.log('comboId:', comboId) 

const combo=ref<any>(null)
const items=ref<any[]>([])
const loading=ref(true)

const loadDetail=async()=>{
    try{
        const res=await request.get(`/combo/${comboId}`)
        combo.value=res.data.combo
        items.value=res.data.items
    }catch (error) {
       console.error('加载套餐详情失败', error)
    } finally {
       loading.value = false
    }
}

const addToCart=async()=>{
    cartStore.addCombo(combo.value,items.value)
    await cartStore.loadCart()
    
}

onMounted(()=>{
    loadDetail()
})

</script>

<template>
    <div class="combo-detail">
        <button class="back-btn" @click="router.back()">← 返回</button>

        <div v-if="loading" class="loading">加载中...</div>

        <div v-else-if="combo" class="detail-content">
            <div class="image-container">
                <img :src="combo.image" :alt="combo.name">
            </div>

            <div class="info-container">
                <h1>{{ combo.name }}</h1>
                <p class="description">{{ combo.description }}</p>
                <p class="price">¥{{ combo.price }}</p>
                <button class="add-cart-btn" @click="addToCart">加入购物车</button>
            </div>

            <div class="items-section">
                <h2>套餐包含</h2>
                <div v-for="item in items" :key="item.id" class="item-card">
                    <img :src="item.productImage" :alt="item.productName">
                    <div class="item-info">
                        <h4>{{ item.productName }}</h4>
                        <p>{{ item.productArtist }}</p>
                        <p>¥{{ item.productPrice }} x {{ item.quantity }}</p>
                    </div>
                </div>
            </div>
        </div>


    </div>
</template>



<style scoped>
.combo-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1.5rem 2rem;
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
  font-size: 0.85rem;
}

.back-btn:hover {
  background: #ffb3b3;
  color: white;
}

.loading, .not-found {
  text-align: center;
  padding: 2rem;
  color: #ffb3b3;
}

/* 左右两列布局 - 图片和信息 */
.detail-content {
  display: grid;
  grid-template-columns: 0.5fr 1.5fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

/* 图片容器 */
.image-container img {
  width: 100%;
  max-width: 350px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

/* 信息容器 */
.info-container h1 {
  color: #d96c6c;
  font-size: 1.8rem;
  margin: 0 0 0.5rem;
}

.info-container .description {
  color: #9e9e9e;
  font-size: 1rem;
  line-height: 1.5;
  margin-bottom: 1rem;
}

.price {
  font-size: 1.8rem;
  color: #ff9b9b;
  font-weight: bold;
  margin: 0.5rem 0;
}

.add-cart-btn {
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 40px;
  padding: 10px 24px;
  cursor: pointer;
  font-weight: 500;
  font-size: 1rem;
  transition: all 0.3s;
  margin-top: 0.5rem;
}

.add-cart-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
}

/* 包含商品区域 */
.items-section {
  margin-top: 1rem;
}

.items-section h2 {
  color: #d96c6c;
  font-size: 1.4rem;
  margin-bottom: 1rem;
  padding-left: 0.5rem;
  border-left: 4px solid #ffb3b3;
}

/* 商品列表 - 网格布局，一行5个 */
.items-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 1rem;
}

/* 商品卡片 */
.item-card {
  display: flex;
  gap: 0.8rem;
  background: white;
  border-radius: 12px;
  padding: 0.8rem;
  border: 1px solid #ffe0e0;
  align-items: center;
}

.item-card img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-info h4 {
  margin: 0 0 2px;
  font-size: 0.85rem;
  color: #4a4a4a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-info p {
  margin: 2px 0;
  font-size: 0.75rem;
  color: #9e9e9e;
}

.item-info .item-price {
  color: #ff9b9b;
  font-weight: bold;
  margin-top: 2px;
  font-size: 0.8rem;
}

/* 响应式 */
@media (max-width: 1200px) {
  .items-list {
    grid-template-columns: repeat(4, 1fr);
  }
}
@media (max-width: 1000px) {
  .items-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 700px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .items-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 500px) {
  .items-list {
    grid-template-columns: 1fr;
  }
}
</style>
<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import axios from 'axios';
import type { Product } from '@/api/product';


const route=useRoute()
const router=useRouter()

const keyword=ref('')
const products=ref<Product[]>([])
const loading=ref(false)

//从url获取关键词
keyword.value=route.query.q as string || ''

//搜索函数
const searchProducts=async()=>{
    if(!keyword.value.trim()){
        return
    }

    loading.value=true


    try{
        //先用前端模拟搜索
        const res=await axios.get(`http://localhost:8080/api/products/search?keyword=${encodeURIComponent(keyword.value)}`)
        products.value=res.data
    }catch(error){
        console.error('搜索失败',error)
    }finally{
        loading.value=false
    }
}

onMounted(()=>{
    if(keyword.value){
        searchProducts()
    }
})

const goHome=()=>{
    router.push('/')
}

const goToProduct=(id:number)=>{
    router.push(`/products/${id}`)
}
</script>

<template>
    <div class="search-page">
        <div class="search-header">
            <button @click="goHome" class="back-btn">←首页</button>
            <h2>搜索结果:"{{ keyword }}"</h2>
        </div>

        <div v-if="loading" class="loading">
            搜索中...
        </div>

        <div v-else-if="products.length>0" class="search-results">
            <div class="result-count">找到{{ products.length }}张专辑</div>
            <div class="product-grid">
                <div v-for="product in products"
                    :key="product.id"
                    class="product-card"
                    @click="goToProduct(product.id)">
                    <img :src="product.image" :alt="product.name">
                    <h3>{{ product.name }}</h3>
                    <p class="artist">{{ product.artist }}</p>
                    <p class="price">¥{{ product.price }}</p>
                </div>
            </div>
        </div>

        <div v-else-if="keyword && !loading" class="no-results">
            <p>没有找到"{{ keyword }}"相关专辑</p>
            <button @click="goHome" class="home-btn">返回首页</button>
        </div>
    </div>

</template>

<style scoped>
.search-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  min-height: calc(100vh - 80px);
  background: #fef9f9;
}

/* 头部样式 */
.search-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #ffe0e0;
}

.back-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ffb3b3;
  background: white;
  border-radius: 30px;
  cursor: pointer;
  font-size: 0.9rem;
  color: #d96c6c;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #ffb3b3;
  color: white;
  transform: translateY(-2px);
}

.search-header h2 {
  color: #d96c6c;
  font-weight: 500;
  font-size: 1.3rem;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #ffb3b3;
  font-size: 1rem;
}

/* 搜索结果信息 */
.result-count {
  margin-bottom: 1rem;
  color: #9e9e9e;
  font-size: 0.9rem;
}

/* ===== 商品网格布局 ===== */
.product-grid {
  display: grid !important;
  grid-template-columns: repeat(5, 1fr) !important;
  gap: 1.5rem !important;
  margin-top: 1rem !important;
}

/* 商品卡片 */
.product-card {
  cursor: pointer;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid #ffe0e0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 179, 179, 0.15);
  border-color: #ffd0d0;
}

/* 卡片内部样式 */
.product-card-inner {
  padding: 1rem;
  display: flex;
  flex-direction: column;
}

/* 商品图片 */
.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 0.75rem;
}

/* 商品标题 */
.product-card h3 {
  margin: 0.5rem 0 0.25rem;
  font-size: 1rem;
  color: #4a4a4a;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 艺术家 */
.product-card .artist {
  color: #9e9e9e;
  font-size: 0.85rem;
  margin: 0.25rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 价格 */
.product-card .price {
  color: #ff9b9b;
  font-weight: bold;
  font-size: 1.1rem;
  margin: 0.5rem 0 0;
}

/* 响应式 */
@media (max-width: 1400px) {
  .product-grid {
    grid-template-columns: repeat(4, 1fr) !important;
  }
}
@media (max-width: 1100px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }
}
@media (max-width: 800px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}
@media (max-width: 500px) {
  .product-grid {
    grid-template-columns: 1fr !important;
  }
}

/* 无结果样式 */
.no-results {
  text-align: center;
  padding: 4rem;
  background: white;
  border-radius: 24px;
  border: 1px solid #ffe0e0;
}

.no-results p {
  color: #d96c6c;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.home-btn {
  margin-top: 1rem;
  padding: 0.6rem 2rem;
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s;
}

.home-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.3);
}
</style>
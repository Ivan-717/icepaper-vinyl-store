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
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: calc(100vh - 80px);
}

/* 头部样式 */
.search-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.back-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.back-btn:hover {
  background: #f5f5f5;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

/* 搜索结果信息 */
.result-count {
  margin-bottom: 1rem;
  color: #666;
}

/* ===== 商品网格布局 - 负margin补偿方案 ===== */
.product-grid {
  display: flex !important;
  flex-wrap: wrap !important;
  margin: 0 -0.75rem !important;        /* 负margin抵消卡片左右padding */
  width: calc(100% + 1.5rem) !important; /* 补偿负margin增加的宽度 */
  margin-top: 1rem !important;
}

/* 商品卡片 - 用padding控制间距，保证宽度准确 */
.product-card {
  width: 25% !important;                 /* 精确的4列，不受gap影响 */
  padding: 0 0.75rem !important;         /* 左右内边距作为卡片间距 */
  margin-bottom: 1.5rem !important;      /* 下边距 */
  box-sizing: border-box !important;
  cursor: pointer;
}

/* 卡片内部样式 */
.product-card-inner {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  background: white;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card-inner:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

/* 商品图片 */
.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  display: block;
}

/* 商品标题 */
.product-card h3 {
  margin: 0.5rem 0 0.25rem;
  font-size: 1rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 艺术家 */
.product-card .artist {
  color: #666;
  font-size: 0.9rem;
  margin: 0.25rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 价格 */
.product-card .price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 1.1rem;
  margin: 0.5rem 0 0;
}

/* 响应式：平板屏幕3列 */
@media (max-width: 992px) {
  .product-card {
    width: 33.333% !important;
  }
}

/* 响应式：手机屏幕2列 */
@media (max-width: 768px) {
  .product-card {
    width: 50% !important;
  }
}

/* 响应式：小手机屏幕1列 */
@media (max-width: 480px) {
  .product-card {
    width: 100% !important;
  }
}

/* 无结果样式 */
.no-results {
  text-align: center;
  padding: 3rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.home-btn {
  margin-top: 1rem;
  padding: 0.5rem 1.5rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.home-btn:hover {
  background: #3aa876;
}
</style>
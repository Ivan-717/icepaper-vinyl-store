<script setup lang="ts">
import { ref,onMounted, computed } from 'vue';
import { getCategories,getProductByCategory,type Category,type Product } from '@/api/product';
import { useRouter } from 'vue-router';
import request from '@/api/request';

//Category[]是ref的数据类型
//([])初始化空数组
const categories=ref<Category[]>([])
//商品列表
const products=ref<Product[]>([])
//当前准备的id(默认第一个)
//0表示显示所有商品
const activeCategory=ref<number>(0)
//是否正在加载数据
const loading=ref(false)

const router=useRouter()

//搜索相关
const searchKeyword=ref('')

//加载所有商品
const loadAllProducts=async()=>{
  loading.value=true
  try{
    const res=await request.get('/products/all')
    products.value=res.data
  }catch(error){
    console.error('加载全部商品失败：',error)
  }finally{
    loading.value=false
  }
}


//加载分类
onMounted(async() => {
    try{
        const res=await getCategories()
        categories.value=res.data
        
        await loadProducts(0)
    }catch(error){
        console.error('加载分类失败',error)
    }
})

//根据分类ID加载商品
const loadProducts=async(categoryId:number)=>{
    loading.value=true //开始加载
    try{
        if(categoryId===0){
          await loadAllProducts()
        }else{
          const res=await getProductByCategory(categoryId)
          products.value=res.data
        }
    }catch(error){
        console.error('加载商品失败:',error)
    }finally{
        loading.value=false
    }
}

const changeCategory=(categoryId:number)=>{
    activeCategory.value=categoryId
    loadProducts(categoryId)
}

//跳转到搜索页
const goToSearch=()=>{
  if(searchKeyword.value.trim()){
    //encodeURIComponent把特殊字符转换成浏览器认识的编码，避免出错
    router.push(`/search?q=${encodeURIComponent(searchKeyword.value.trim())}`)
  }
}
</script>

<template>
    <div class="home">

      <!-- 套餐推荐横幅 -->
      <div class="combo-banner" @click="router.push('/combos')">
        <div class="banner-content">
          <span class="banner-icon">🍽️</span>
          <span class="banner-text">超值套餐，限时优惠</span>
          <span class="banner-arrow">→</span>
        </div>
      </div> 


        <div class="home-search">
          <input type="text"
            v-model="searchKeyword"
            placeholder="搜索专辑或艺术家..."
            @keyup.enter="goToSearch"
            class="home-search-input">
            <button @click="goToSearch" class="home-search-btn">搜索</button>
        </div>

        <div class="categories">
            <button  
                :class="{active:activeCategory === 0}"
                @click="changeCategory(0)" >
                全部
            </button>

            <button v-for="cat in categories" 
                :key="cat.id" 
                :class="{active:activeCategory === cat.id}"
                @click="changeCategory(cat.id)" >
                {{ cat.name }}
            </button>
        </div>

        <!-- 加载状态显示 -->
        <div v-if="loading" class="loading">
          ⏳ 加载中...
        </div>
        <!-- 商品列表区域 -->
        <div v-else class="products">
            <div v-for="product in products" :key="product.id" class="product-card"
            @click="router.push(`/products/${product.id}`)">
                <img 
                    :src="product.image"
                    :alt="product.name"
                    @error="(e) => (e.target as HTMLImageElement).src = '/images/placeholder.jpg'"
                >

                <h3>{{ product.name }}</h3>
                <p class="artist">{{ product.artist }}</p>
                <p class="price">¥{{ product.price }}</p>
            </div>
        </div>

    </div>
</template>


<style scoped>
.home {
  text-align: center;
  padding: 0 2rem 2rem;
  background: #fef9f9;
  min-height: 100vh;
}

/* 分类按钮容器 */
.categories {
  margin: 1.5rem 0;
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.categories button {
  padding: 0.5rem 1.5rem;
  border: 1px solid #ffb3b3;
  background: white;
  border-radius: 30px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
  color: #d96c6c;
  font-weight: 500;
}

.categories button:hover {
  background: #ffb3b3;
  color: white;
  border-color: #ffb3b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.3);
}

.categories button.active {
  background: #ffb3b3;
  color: white;
  border-color: #ffb3b3;
  box-shadow: 0 2px 8px rgba(255, 179, 179, 0.4);
}

.loading {
  padding: 2rem;
  color: #ffb3b3;
  font-size: 1rem;
}

/* 商品列表网格布局 - 固定5列 */
.products {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
  justify-content: start;
  width: 100%;
}

/* 响应式 */
@media (max-width: 1400px) {
  .products {
    grid-template-columns: repeat(4, 1fr);
  }
}
@media (max-width: 1100px) {
  .products {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 800px) {
  .products {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 500px) {
  .products {
    grid-template-columns: 1fr;
  }
}

.product-card {
  border: 1px solid #ffe0e0;
  border-radius: 20px;
  padding: 1rem;
  text-align: left;
  transition: all 0.3s;
  background: white;
  width: 100%;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.product-card:hover {
  box-shadow: 0 8px 24px rgba(255, 179, 179, 0.15);
  transform: translateY(-4px);
  border-color: #ffd0d0;
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 0.75rem;
}

.product-card h3 {
  font-size: 1rem;
  margin: 0.5rem 0 0.25rem;
  color: #4a4a4a;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-card .artist {
  color: #9e9e9e;
  font-size: 0.85rem;
  margin: 0.25rem 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-card .price {
  color: #ff9b9b;
  font-weight: bold;
  font-size: 1.2rem;
  margin: 0.5rem 0 0;
}

/* 首页搜索框 */
.home-search {
  display: flex;
  gap: 0.75rem;
  max-width: 600px;
  margin: 0 auto 2rem;
  padding-top: 1rem;
}

.home-search-input {
  flex: 1;
  padding: 0.8rem 1.2rem;
  border: 2px solid #ffe0e0;
  border-radius: 50px;
  font-size: 1rem;
  transition: all 0.3s;
  background: white;
}

.home-search-input:focus {
  outline: none;
  border-color: #ffb3b3;
  box-shadow: 0 0 0 3px rgba(255, 179, 179, 0.2);
}

.home-search-btn {
  padding: 0.8rem 2rem;
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 6px rgba(255, 179, 179, 0.3);
}

.home-search-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.4);
}

/* 套餐推荐横幅 */
.combo-banner {
  background: linear-gradient(135deg, #ffb3b3, #ff9b9b);
  border-radius: 40px;
  margin: 0 0 2rem 0;
  padding: 0.8rem 1.5rem;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 179, 179, 0.3);
}

.combo-banner:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(255, 179, 179, 0.4);
}

.banner-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  color: white;
  font-weight: 500;
}

.banner-icon {
  font-size: 1.3rem;
}

.banner-text {
  font-size: 1rem;
}

.banner-arrow {
  font-size: 1.2rem;
}
</style>
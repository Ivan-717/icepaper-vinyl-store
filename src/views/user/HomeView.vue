<script setup lang="ts">
import { ref,onMounted, computed } from 'vue';
import { getCategories,getProductByCategory,type Category,type Product } from '@/api/product';
import { useRouter } from 'vue-router';

//Category[]是ref的数据类型
//([])初始化空数组
const categories=ref<Category[]>([])
//商品列表
const products=ref<Product[]>([])
//当前准备的id(默认第一个)
const activeCategory=ref<number>(1)
//是否正在加载数据
const loading=ref(false)

const router=useRouter()

//搜索相关
const searchKeyword=ref('')



//加载分类
onMounted(async() => {
    try{
        const res=await getCategories()
        categories.value=res.data
        
        //如果分类不为空，自动加载第一个分类的商品
        if(categories.value.length>0){
            activeCategory.value=categories.value[0]?.id ?? 1
            loadProducts(activeCategory.value)
        }
    }catch(error){
        console.error('加载分类失败',error)
    }
})

//根据分类ID加载商品
const loadProducts=async(categoryId:number)=>{
    loading.value=true //开始加载
    try{
        const res=await getProductByCategory(categoryId)
        products.value=res.data
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
        <div class="home-search">
          <input type="text"
            v-model="searchKeyword"
            placeholder="搜索专辑或艺术家..."
            @keyup.enter="goToSearch"
            class="home-search-input">
            <button @click="goToSearch" class="home-search-btn">搜索</button>
        </div>

        <div class="categories">
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
  border: 1px solid #42b983;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
}

.categories button:hover {
  background: #42b983;
  color: white;
}

.categories button.active {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

.loading {
  padding: 2rem;
  color: #666;
}

/* 商品列表网格布局 - 固定5列 */
.products {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-top: 1.5rem;
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
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  text-align: left;
  transition: all 0.3s;
  background: white;
  width: 100%;
  box-sizing: border-box;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 0.5rem;
}

.product-card h3 {
  font-size: 1.1rem;
  margin: 0.5rem 0 0.25rem;
  color: #333;
}

.product-card .artist {
  color: #666;
  font-size: 0.9rem;
  margin: 0.25rem 0;
}

.product-card .price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 1.2rem;
  margin: 0.5rem 0 0;
}

/* 首页搜索框 */
.home-search {
  display: flex;
  gap: 0.5rem;
  max-width: 500px;
  margin: 0 auto 1.5rem;
}

.home-search-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.home-search-input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.home-search-btn {
  padding: 0.75rem 1.5rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s;
}

.home-search-btn:hover {
  background: #3aa876;
}
</style>
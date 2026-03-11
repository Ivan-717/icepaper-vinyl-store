<script setup lang="ts">
import { ref,onMounted } from 'vue';
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
</script>

<template>
    <div class="home">
        <h2>欢迎来冰，逛逛店子</h2>
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
  padding: 2rem;
}

/* 分类按钮容器 */
.categories {
  margin: 2rem 0;
  display: flex;
  gap: 1rem;          /* 按钮之间的间距 */
  justify-content: center;
  flex-wrap: wrap;    /* 手机端自动换行 */
}

/* 普通按钮样式 */
.categories button {
  padding: 0.5rem 1.5rem;
  border: 1px solid #42b983;
  background: white;
  border-radius: 20px;  /* 圆角 */
  cursor: pointer;      /* 鼠标变小手 */
  font-size: 1rem;
  transition: all 0.3s; /* 动画过渡 */
}

/* 鼠标悬停效果 */
.categories button:hover {
  background: #42b983;
  color: white;
}

/* 选中状态（当前分类） */
.categories button.active {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

/* 加载状态文字 */
.loading {
  padding: 2rem;
  color: #666;
}

/* 商品列表网格布局 */
.products {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;           /* 卡片间距 */
  margin-top: 2rem;
}

/* 单个商品卡片 */
.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  text-align: left;    /* 文字左对齐 */
  transition: all 0.3s;
  background: white;
}

/* 卡片悬停效果 */
.product-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);  /* 向上浮动 */
}

/* 商品图片 */
.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;   /* 图片裁剪适应，不变形 */
  border-radius: 4px;
  margin-bottom: 0.5rem;
}

/* 专辑名称 */
.product-card h3 {
  font-size: 1.1rem;
  margin: 0.5rem 0 0.25rem;
  color: #333;
}

/* 艺术家 */
.product-card .artist {
  color: #666;
  font-size: 0.9rem;
  margin: 0.25rem 0;
}

/* 价格 */
.product-card .price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 1.2rem;
  margin: 0.5rem 0 0;
}

</style>
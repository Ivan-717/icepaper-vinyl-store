<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import axios from 'axios';
import { useCartStore } from '@/stores/cart';
import type { Product } from '@/api/product';

//获取当前路由对象，用于读取URL参数
const route=useRoute()
//获取路由实例，用于页面跳转
const router=useRouter()
//获取购物车store
const cartStore=useCartStore()

//当前商品详情
//<Product | null>:该对象的值只能是product和null
const product=ref<Product | null>(null)
//是否正在加载数据
const loading=ref(true)
//购买数量，默认为1
const quantity=ref(1)

//从url获取商品id,并转换为数字
const productId=Number(route.params.id)

//自动加载商品详情
onMounted(async()=>{
    try{
        const res=await axios.get(`http://localhost:8080/api/products/${productId}`)
        product.value=res.data
    }catch(error){
        console.error('加载商品详情失败:',error)
    }finally{
        loading.value=false
    }
})

//加入购物车
const addToCart=()=>{
    if(product.value){
        //调用购物车store的addItem方法
        cartStore.addItem({
            id:product.value.id,
            name: product.value.name,          // 商品名称
            artist: product.value.artist,      // 艺术家
            price: product.value.price,        // 单价
            image: product.value.image,        // 图片路径
            quantity: quantity.value  
        })
        alert('已加入购物车')
    }
}

//返回上一页
const goBack=()=>{
   // 如果有上一页就返回，否则去首页
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

</script>

<template>
    <div class="product-detail">
        <button class="back-btn" @click="goBack">← 返回</button>

        <div v-if="loading" class="loading">
            ⏳ 加载中...
        </div>

        <!-- 商品详情 -->
        <div v-else-if="product" class="detail-content">
           <!-- 左侧：图片区域 -->
            <div class="image-container">
               <img :src="product.image" :alt="product.name">
            </div>

            <!-- 右侧：商品信息 -->
            <div class="info-container">
                <h1>{{ product.name }}</h1>
                <h2 class="artist">{{ product.artist }}</h2>

                
                <div class="price-section">
                    <span class="price-label">价格</span>
                    <span class="price">¥{{ product.price }}</span>
                </div>

                <div class="stock-label">库存</div>
                <!-- 动态添加low类 -->
                <span class="stock" :class="{low:product.stock<5}">
                    {{ product.stock }}件
                </span>

                <div class="description-section">
                    <h3>专辑简介</h3>
                    <p>{{ product.description }}</p>
                </div>

                <div class="action-section">
                    <div class="quantity-selector">
                        <button @click="quantity>1 && quantity--"
                            :disabled="quantity<=1">
                            -
                        </button>

                        <input type="number"
                        v-model.number="quantity"
                        min="1"
                        :max="product.stock">

                        <button @click="quantity<product.stock && quantity++"
                        :disabled="quantity>=product.stock">
                            +
                        </button>
                    </div>
                </div>
            </div>
          
        </div>
        <!-- 商品不存在 -->
        <div v-else class="not-found">
           商品不存在
        </div>
    </div>
</template>

<style scoped>
/* 整体容器 */
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

/* 返回按钮 */
.back-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 2rem;
  transition: background 0.3s;
}

.back-btn:hover {
  background: #f5f5f5;
}

/* 加载状态和未找到提示 */
.loading, .not-found {
  text-align: center;
  padding: 3rem;
  color: #666;
}

/* 详情内容容器：使用网格布局，左右两列 */
.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
}

/* 左侧图片容器 */
.image-container img {
  width: 100%;
  max-width: 500px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

/* 右侧信息容器 */
.info-container h1 {
  font-size: 2rem;
  margin: 0 0 0.5rem;
  color: #333;
}

.info-container .artist {
  font-size: 1.2rem;
  color: #666;
  margin: 0 0 1.5rem;
  font-weight: normal;
}

/* 价格区域：带上下边框 */
.price-section {
  margin: 1.5rem 0;
  padding: 1rem 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 2rem;
}

.price-label {
  font-size: 1rem;
  color: #666;
}

.price {
  font-size: 2rem;
  color: #f56c6c;
  font-weight: bold;
}

/* 库存区域 */
.stock-section {
  margin: 1rem 0;
  display: flex;
  align-items: center;
  gap: 2rem;
}

.stock-label {
  color: #666;
}

.stock {
  font-weight: 500;
}

/* 库存不足时的样式 */
.stock.low {
  color: #f56c6c;
}

/* 专辑简介区域 */
.description-section {
  margin: 2rem 0;
}

.description-section h3 {
  margin: 0 0 0.5rem;
  color: #333;
}

.description-section p {
  color: #666;
  line-height: 1.6;
}

/* 底部操作区域 */
.action-section {
  display: flex;
  gap: 1rem;
  align-items: center;
}

/* 数量选择器容器 */
.quantity-selector {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

/* 数量按钮 */
.quantity-selector button {
  width: 40px;
  height: 40px;
  border: none;
  background: white;
  cursor: pointer;
  font-size: 1.2rem;
  transition: background 0.3s;
}

.quantity-selector button:hover:not(:disabled) {
  background: #f5f5f5;
}

/* 禁用状态的按钮 */
.quantity-selector button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* 数量输入框 */
.quantity-selector input {
  width: 60px;
  height: 40px;
  border: none;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
  text-align: center;
  font-size: 1rem;
}

.quantity-selector input:focus {
  outline: none;
}

/* 加入购物车按钮 */
.add-to-cart-btn {
  flex: 1;
  height: 40px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background 0.3s;
}

.add-to-cart-btn:hover {
  background: #3aa876;
}
</style>
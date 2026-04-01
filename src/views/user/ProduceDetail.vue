<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import axios from 'axios';
import { useCartStore } from '@/stores/cart';
import type { Product } from '@/api/product';
import {nanoid} from 'nanoid'

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
const addToCart=async()=>{
  if(!product.value){
    return
  }

  const token=localStorage.getItem('userToken')

  if(!token){
    //存localstorage
    const cart=JSON.parse(localStorage.getItem('cart')||'[]')
    const existing=cart.find((item:any)=>item.productId===product.value?.id)
    if(existing){
      existing.quantity+=quantity.value
    }else{
      cart.push({
        id:nanoid(),  // 加临时 id
        productId: product.value.id,
        quantity: quantity.value,
        productName: product.value.name,
        productArtist: product.value.artist,
        productPrice: product.value.price,
        productImage: product.value.image
      })
    }
    localStorage.setItem('cart',JSON.stringify(cart))
    //刷新store
    await cartStore.loadCart()
    alert('已加入购物车（未登录，登录后同步）')
  }else{
    try{
      await cartStore.addItem(product.value.id,quantity.value)
      alert('添加成功！')
    }catch(error){
      console.error('加入购物车失败:', error)
      alert('加入购物车失败')
  }
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

                      <button class="add-to-cart-btn" @click="addToCart">
                          加入购物车
                      </button>
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
  background: #fef9f9;
  min-height: 100vh;
}

/* 返回按钮 */
.back-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ffb3b3;
  background: white;
  border-radius: 30px;
  cursor: pointer;
  margin-bottom: 2rem;
  transition: all 0.3s;
  color: #d96c6c;
}

.back-btn:hover {
  background: #ffb3b3;
  color: white;
  transform: translateY(-2px);
}

/* 加载状态和未找到提示 */
.loading, .not-found {
  text-align: center;
  padding: 3rem;
  color: #ffb3b3;
  font-size: 1rem;
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
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(255, 179, 179, 0.2);
  border: 1px solid #ffe0e0;
}

/* 右侧信息容器 */
.info-container h1 {
  font-size: 2rem;
  margin: 0 0 0.5rem;
  color: #d96c6c;
  font-weight: 500;
}

.info-container .artist {
  font-size: 1.2rem;
  color: #9e9e9e;
  margin: 0 0 1.5rem;
  font-weight: normal;
}

/* 价格区域：带上下边框 */
.price-section {
  margin: 1.5rem 0;
  padding: 1rem 0;
  border-top: 1px solid #ffe0e0;
  border-bottom: 1px solid #ffe0e0;
  display: flex;
  align-items: center;
  gap: 2rem;
}

.price-label {
  font-size: 1rem;
  color: #9e9e9e;
}

.price {
  font-size: 2rem;
  color: #ff9b9b;
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
  color: #9e9e9e;
}

.stock {
  font-weight: 500;
  color: #4a4a4a;
}

/* 库存不足时的样式 */
.stock.low {
  color: #ff9b9b;
}

/* 专辑简介区域 */
.description-section {
  margin: 2rem 0;
}

.description-section h3 {
  margin: 0 0 0.5rem;
  color: #4a4a4a;
  font-size: 1.1rem;
  font-weight: 600;
}

.description-section p {
  color: #9e9e9e;
  line-height: 1.6;
  font-size: 0.95rem;
}

/* 底部操作区域 */
.action-section {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-top: 2rem;
}

/* 数量选择器容器 */
.quantity-selector {
  display: flex;
  align-items: center;
  border: 1px solid #ffe0e0;
  border-radius: 40px;
  overflow: hidden;
  background: white;
}

/* 数量按钮 */
.quantity-selector button {
  width: 44px;
  height: 44px;
  border: none;
  background: white;
  cursor: pointer;
  font-size: 1.2rem;
  transition: all 0.3s;
  color: #d96c6c;
}

.quantity-selector button:hover:not(:disabled) {
  background: #ffb3b3;
  color: white;
}

.quantity-selector button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* 数量输入框 */
.quantity-selector input {
  width: 60px;
  height: 44px;
  border: none;
  border-left: 1px solid #ffe0e0;
  border-right: 1px solid #ffe0e0;
  text-align: center;
  font-size: 1rem;
  color: #4a4a4a;
}

.quantity-selector input:focus {
  outline: none;
}

/* 加入购物车按钮 */
.add-to-cart-btn {
  flex: 1;
  height: 48px;
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 179, 179, 0.3);
}

.add-to-cart-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.4);
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .product-detail {
    padding: 1rem;
  }
  
  .info-container h1 {
    font-size: 1.5rem;
  }
  
  .price {
    font-size: 1.5rem;
  }
}
</style>
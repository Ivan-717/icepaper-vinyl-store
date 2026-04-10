<script setup lang="ts">
import { useCartStore } from '@/stores/cart';
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';

const cartStore =useCartStore()
const router=useRouter()

onMounted(async()=>{
  await cartStore.loadCart()
  console.log('items.value:', cartStore.items)
  console.log('第一个商品:', cartStore.items[0])
  console.log('购物车数据:', cartStore.items)
})

//更新商品数量
const updateQuantity=(productId:number,quantity:number)=>{
    cartStore.updateQuantity(productId,quantity)
}

//移除商品
const removeItem =(productId:number)=>{
    if(confirm('确定要从购物车移除吗?')){
        cartStore.removeItem(productId)
    }
}

//清空购物车
const clearCart=()=>{
    if(confirm('确定要清空购物车吗？')){
        cartStore.clearCart()
    }
}

//继续购物
const continueShopping=()=>{
    router.push('/')
}

const goToCheckout=()=>{
  const token=localStorage.getItem('userToken')
  if(!token){
    alert('请先登录')
    return
  }
  router.push('/checkout')
}
</script>

<template>
    <div class="cart">
        <h1>🛒 购物车</h1>

        <div v-if="cartStore.items.length===0" class="empty-cart">
            <p>购物车还是空的</p>
            <button @click="continueShopping" class="continue-btn">
                去逛逛
            </button>
        </div>

        <div v-else>
            <div class="cart-list">
                <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
                    <img :src="item.productImage" :alt="item.name" class="item-image">

                    <div class="item-info">
                        <h3>{{ item.productName }}</h3>
                        <p class="item-artist">{{ item.productArtist }}</p>
                        <p class="item-price">{{ item.productPrice }}</p>
                    </div>

                    <div class="item-quantity">
                        <button
                            @click="updateQuantity(item.id,item.quantity-1)"
                            :disabled="item.quantity<=1">
                            -
                        </button>

                        <span>{{ item.quantity }}</span>

                        <button 
                        @click="updateQuantity(item.id, item.quantity + 1)">
                        +  
                        </button>
                    </div>

                    <div class="item-subtotal">
                        ¥{{ (item.productPrice*item.quantity).toFixed(2) }}
                    </div>

                    <button class="remove-btn" @click="removeItem(item.id)">
                        🗑️
                    </button>
                </div>
            </div>

            <div class="cart-footer">
                <div class="total">
                    <span>总计：</span>
                    <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
                </div>

                <div class="cart-actions">
                    <button @click="clearCart" class="clear-btn">
                        清空购物车
                    </button>

                    <button @click="continueShopping" class="continue-btn">
                        继续购物
                    </button>

                    <button class="checkout-btn" @click="goToCheckout()">
                        去结算
                    </button>
                </div>
            </div>
        </div>
    </div>

</template>

<style scoped>
/* 购物车主容器 - 全宽 */
.cart {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem 2rem 2rem;
  background: #fef9f9;
  min-height: 100vh;
  width: 100%;
}

/* 页面主标题 */
.cart h1 {
  margin-bottom: 1.5rem;
  color: #d96c6c;
  font-weight: 500;
  font-size: 1.8rem;
}

/* ---------- 空购物车样式 ---------- */
.empty-cart {
  text-align: center;
  padding: 4rem;
  background: white;
  border-radius: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid #ffe0e0;
}

.empty-cart p {
  color: #d96c6c;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

/* ---------- 购物车列表样式 ---------- */
.cart-list {
  border: 1px solid #ffe0e0;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 1.5rem;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

/* 单个购物车项：网格布局，列宽自适应 */
.cart-item {
  display: grid;
  grid-template-columns: 120px minmax(200px, 1.5fr) 140px 120px 60px;
  gap: 1rem;
  align-items: center;
  padding: 1.2rem;
  border-bottom: 1px solid #ffe0e0;
  transition: background 0.2s;
}

.cart-item:hover {
  background: #fff5f5;
}

.cart-item:last-child {
  border-bottom: none;
}

/* 商品图片 */
.item-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

/* 商品信息 */
.item-info h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: #4a4a4a;
  font-weight: 600;
}

.item-artist {
  margin: 0 0 0.25rem;
  font-size: 0.9rem;
  color: #9e9e9e;
}

.item-price {
  margin: 0;
  font-weight: bold;
  color: #ff9b9b;
  font-size: 1.1rem;
}

/* 数量选择器 */
.item-quantity {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  justify-content: center;
}

.item-quantity button {
  width: 36px;
  height: 36px;
  border: 1px solid #ffe0e0;
  background: white;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 1.2rem;
  font-weight: 500;
  color: #d96c6c;
}

.item-quantity button:hover:not(:disabled) {
  background: #ffb3b3;
  border-color: #ffb3b3;
  color: white;
}

.item-quantity button:disabled {
  color: #ccc;
  cursor: not-allowed;
  border-color: #eee;
}

.item-quantity span {
  min-width: 36px;
  text-align: center;
  font-weight: 500;
  color: #4a4a4a;
  font-size: 1rem;
}

/* 小计金额 */
.item-subtotal {
  font-weight: bold;
  color: #d96c6c;
  font-size: 1.1rem;
  text-align: right;
}

/* 删除按钮 */
.remove-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #ffe0e0;
  background: white;
  color: #ff9b9b;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  margin: 0 auto;
}

.remove-btn:hover {
  background: #ffb3b3;
  border-color: #ffb3b3;
  color: white;
}

/* ---------- 购物车底部样式 ---------- */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.2rem 2rem;
  background: white;
  border-radius: 20px;
  border: 1px solid #ffe0e0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

/* 总计区域 */
.total {
  font-size: 1.2rem;
  color: #4a4a4a;
}

.total-price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #ff9b9b;
  margin-left: 0.5rem;
}

/* 操作按钮组 */
.cart-actions {
  display: flex;
  gap: 1rem;
}

/* 通用按钮样式 */
.clear-btn,
.continue-btn,
.checkout-btn {
  padding: 10px 28px;
  border-radius: 40px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s;
  white-space: nowrap;
  border: none;
}

/* 清空按钮 */
.clear-btn {
  background: white;
  border: 1px solid #ffb3b3;
  color: #ff9b9b;
}

.clear-btn:hover {
  background: #ffb3b3;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.3);
}

/* 继续购物按钮 */
.continue-btn {
  background: white;
  border: 1px solid #ffb3b3;
  color: #ff9b9b;
}

.continue-btn:hover {
  background: #ffb3b3;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.3);
}

/* 结算按钮 */
.checkout-btn {
  background: #ffb3b3;
  color: white;
  box-shadow: 0 2px 6px rgba(255, 179, 179, 0.3);
}

.checkout-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.4);
}

/* 响应式：小屏幕调整 */
@media (max-width: 900px) {
  .cart-item {
    grid-template-columns: 100px 1fr 100px 80px 40px;
    gap: 0.8rem;
    padding: 1rem;
  }
  
  .item-image {
    width: 80px;
    height: 80px;
  }
  
  .item-info h3 {
    font-size: 0.95rem;
  }
}

@media (max-width: 700px) {
  .cart-item {
    grid-template-columns: 80px 1fr 80px 70px 36px;
    gap: 0.5rem;
  }
  
  .item-quantity button {
    width: 30px;
    height: 30px;
  }
  
  .clear-btn,
  .continue-btn,
  .checkout-btn {
    padding: 8px 16px;
    font-size: 0.85rem;
  }
}

@media (max-width: 600px) {
  .cart-item {
    grid-template-columns: 70px 1fr 70px 60px 32px;
  }
  
  .item-image {
    width: 60px;
    height: 60px;
  }
}
</style>
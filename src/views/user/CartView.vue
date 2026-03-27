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

                    <button class="checkout-btn">
                        去结算
                    </button>
                </div>
            </div>
        </div>
    </div>

</template>

<style scoped>
/* 购物车主容器 */
.cart {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem 2rem;
}

/* 页面主标题 */
.cart h1 {
  margin-bottom: 1.5rem;
  color: #333;
}

/* ---------- 空购物车样式 ---------- */
.empty-cart {
  text-align: center;
  padding: 3rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.empty-cart p {
  color: #666;
  margin-bottom: 1rem;
}

/* ---------- 购物车列表样式 ---------- */
.cart-list {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1.5rem;
}

/* 单个购物车项：网格布局 */
.cart-item {
  display: grid;
  grid-template-columns: 100px 1fr 120px 100px 40px;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.cart-item:last-child {
  border-bottom: none;
}

/* 商品图片 */
.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

/* 商品信息 */
.item-info h3 {
  margin: 0 0 0.25rem;
  font-size: 1rem;
  color: #333;
}

.item-artist {
  margin: 0 0 0.25rem;
  font-size: 0.9rem;
  color: #666;
}

.item-price {
  margin: 0;
  font-weight: bold;
  color: #f56c6c;
}

/* 数量选择器 */
.item-quantity {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.item-quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.item-quantity button:hover:not(:disabled) {
  background: #f5f5f5;
}

.item-quantity button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.item-quantity span {
  min-width: 30px;
  text-align: center;
}

/* 小计金额 */
.item-subtotal {
  font-weight: bold;
  color: #333;
}

/* 删除按钮 */
.remove-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ff4444;
  background: white;
  color: #ff4444;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.remove-btn:hover {
  background: #ff4444;
  color: white;
}

/* ---------- 购物车底部样式 ---------- */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 8px;
}

/* 总计区域 */
.total {
  font-size: 1.2rem;
}

.total-price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #f56c6c;
}

/* 操作按钮组 - 按钮分开，有间距 */
.cart-actions {
  display: flex;
  gap: 12px;
}

/* 通用按钮样式 */
.clear-btn,
.continue-btn,
.checkout-btn {
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  white-space: nowrap;
  border: none;
}

/* 清空按钮 */
.clear-btn {
  background: white;
  border: 1px solid #ff4444;
  color: #ff4444;
}

.clear-btn:hover {
  background: #ff4444;
  color: white;
}

/* 继续购物按钮 */
.continue-btn {
  background: white;
  border: 1px solid #42b983;
  color: #42b983;
}

.continue-btn:hover {
  background: #42b983;
  color: white;
}

/* 结算按钮 */
.checkout-btn {
  background: #42b983;
  color: white;
}

.checkout-btn:hover {
  background: #3aa876;
}
</style>
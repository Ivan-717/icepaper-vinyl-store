<script setup lang="ts">
import { useCartStore } from '@/stores/cart';
import { useRouter } from 'vue-router';

const cartStore =useCartStore()
const router=useRouter()

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
                    <img :src="item.image" :alt="item.name" class="item-image">

                    <div class="item-info">
                        <h3>{{ item.name }}</h3>
                        <p class="item-artist">{{ item.artist }}</p>
                        <p class="itm-price">{{ item.price }}</p>
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
                        ¥{{ (item.price*item.quantity).toFixed(2) }}
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

                <div class="cart-action">
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
  padding: 2rem;
}

/* 页面主标题 */
.cart h1 {
  margin-bottom: 2rem;
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
  overflow: hidden;        /* 确保圆角效果 */
  margin-bottom: 2rem;
}

/* 单个购物车项：使用网格布局 */
.cart-item {
  display: grid;
  grid-template-columns: 100px 1fr 120px 100px 40px;  /* 5列固定宽度 */
  gap: 1rem;
  align-items: center;     /* 垂直居中 */
  padding: 1rem;
  border-bottom: 1px solid #eee;  /* 分隔线 */
}

/* 最后一项去掉底部边框 */
.cart-item:last-child {
  border-bottom: none;
}

/* 商品图片 */
.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;      /* 图片裁剪适应，不变形 */
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

/* 数量按钮 */
.item-quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

/* 按钮悬停效果（不禁用时） */
.item-quantity button:hover:not(:disabled) {
  background: #f5f5f5;
}

/* 禁用状态的按钮 */
.item-quantity button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* 数量数字 */
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

/* 删除按钮悬停效果 */
.remove-btn:hover {
  background: #ff4444;
  color: white;
}

/* ---------- 购物车底部样式 ---------- */
.cart-footer {
  display: flex;
  justify-content: space-between;  /* 两端对齐 */
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

/* 操作按钮组 */
.cart-actions {
  display: flex;
  gap: 1px;  
}

.cart-actions button {
  margin-right: 0;
}

/* 最后一个按钮去掉右边距 */
.cart-actions button:last-child {
  margin-right: 0;
}

/* 通用按钮样式 */
.clear-btn, .continue-btn, .checkout-btn {
  padding: 8px 20px;      /* 内边距加大，按钮更大方 */
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;       /* 字体稍微加粗 */
  transition: all 0.3s;
  white-space: nowrap;    /* 防止文字换行 */
  min-width: 100px;       /* 最小宽度，让按钮更整齐 */
  text-align: center;
}

/* 清空按钮：红框白底 */
.clear-btn {
  background: white;
  border: 1px solid #ff4444;
  border-right: none;  /* 去掉右边框 */
  color: #ff4444;
  border-radius: 4px 0 0 4px;  /* 左边圆角，右边直角 */
}

.clear-btn:hover {
  background: #ff4444;
  color: white;
}

/* 继续购物按钮：绿框白底 */
.continue-btn {
  background: white;
  border: 1px solid #42b983;
  border-left: 1px solid #ff4444;  /* 左边是红色，和清空按钮衔接 */
  border-right: none;  /* 去掉右边框 */
  color: #42b983;
  border-radius: 0;  /* 去掉圆角，中间按钮都是直角 */
}

.continue-btn:hover {
  background: #42b983;
  color: white;
}

/* 结算按钮：纯绿底 */
.checkout-btn {
  background: #42b983;
  border: 1px solid #42b983;
  border-left: none;  /* 去掉左边框 */
  color: white;
  border-radius: 0 4px 4px 0;  /* 右边圆角，左边直角 */
}

.checkout-btn:hover {
  background: #3aa876;
}
</style>
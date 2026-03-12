<script setup lang="ts">
  import { RouterLink,RouterView } from 'vue-router';
  import { useCartStore } from './stores/cart';

  const cartStore=useCartStore()
</script>

<template>
  <div id="app">
    <header>
      <h1>🎵 冰纸专辑店</h1>
      <nav>
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/cart">
          🛒 购物车
          <span v-if="cartStore.totalCount>0" class="cart-badge">
            {{ cartStore.totalCount }}
          </span>
        </RouterLink>
      </nav>
    </header>
    <main>
      <RouterView/>
    </main>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background-color: #e8c2dc;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

nav a {
  color: white;
  text-decoration: none;
  margin-left: 1rem;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

nav a:hover {
  background-color: rgba(255,255,255,0.2);
}

main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

/* 购物车链接特殊处理 */
.cart-link {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 购物车角标 */
.cart-badge {
  display: inline-block;
  min-width: 20px;
  height: 20px;
  background-color: #ff4444;
  color: white;
  border-radius: 10px;
  font-size: 12px;
  line-height: 20px;
  text-align: center;
  padding: 0 4px;
  margin-left: 4px;
}

</style>

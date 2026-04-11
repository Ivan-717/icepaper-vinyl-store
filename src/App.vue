<script setup lang="ts">
  import { RouterLink,RouterView } from 'vue-router';
  import { useCartStore } from './stores/cart';
  import { useRoute,useRouter } from 'vue-router';
  import { computed } from 'vue';
  import { onMounted } from 'vue';


  const cartStore=useCartStore()
  const route=useRoute()
  const router=useRouter()

  //判断当前是不是管理后台
  const isAdminRoute=computed(()=>{
    return route.path.startsWith('/admin')
  })

  //用户信息（从localStorage读取）
  const useInfo=computed(()=>{
    const info=localStorage.getItem('userInfo')
    return info ? JSON.parse(info):null
  })

  //是否已登录
  //!!双重取反，把结果强制转成布尔值
  const isLoggedIn=computed(()=>!!localStorage.getItem('userToken'))

  //退出登录
  const logout=()=>{
    //清除登录信息
    localStorage.removeItem('userToken')
    localStorage.removeItem('userInfo')
    //重新加载购物车
    cartStore.loadCart()
    //调回首页
    router.push('/')
    setTimeout(() => window.location.reload(), 100)
  }

  onMounted(()=>{
    cartStore.loadCart()
  })
</script>

<template>
  <div id="app">
    <header v-if="!isAdminRoute">
      <div class="header-left">
        <h1>🎵 冰纸专辑店</h1>
      </div>

      <div class="header-center">
        <span class="welcome-msg">欢迎来冰，逛逛店子！</span>
      </div>
      
      <div class="header-right">
        <nav>
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/cart">
            🛒 购物车
            <span v-if="cartStore.totalCount>0" class="cart-badge">
              {{ cartStore.totalCount }}
            </span>
          </RouterLink>

          <!-- 用户区域 -->
          <div class="user-section">
            <template v-if="isLoggedIn">
              <RouterLink to="/orders" class="orders-link">我的订单</RouterLink>
              <RouterLink to="/address" class="address-link">我的地址</RouterLink>
              <span class="username">{{ useInfo?.username }}</span>
              <button class="logout-btn" @click="logout">退出</button>
            </template>
            <RouterLink v-else to="/login" class="login-link"> 登录</RouterLink>
          </div> 
        </nav>
      </div>
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

/* 用户区域 */
.user-section {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  margin-left: 20px;
}

.username {
  color: white;
  font-size: 14px;
}

.logout-btn {
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.logout-btn:hover {
  background: rgba(255,255,255,0.4);
}

.login-link {
  color: white;
  text-decoration: none;
  padding: 4px 12px;
  background: rgba(255,255,255,0.2);
  border-radius: 20px;
  transition: background 0.2s;
}

.login-link:hover {
  background: rgba(255,255,255,0.4);
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

.header-left {
  flex: 1;
  text-align: left;
}

.header-center {
  flex: 2;
  text-align: center;
}

.header-right {
  flex: 1;
  text-align: right;
}

.welcome-msg {
  font-size: 1.5rem;
  font-weight: bold;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #fff, #ffe6f0);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.address-link {
  color: white;
  text-decoration: none;
  margin-right: 1rem;
  padding: 0.25rem 0.5rem;
  background: rgba(255,255,255,0.2);
  border-radius: 4px;
}

.address-link:hover {
  background: rgba(255,255,255,0.3);
}

.orders-link, .address-link {
  color: white;
  text-decoration: none;
  margin-right: 0.5rem;
  padding: 0.25rem 0.8rem;
  background: rgba(255,255,255,0.2);
  border-radius: 4px;
}

.orders-link:hover, .address-link:hover {
  background: rgba(255,255,255,0.3);
}
</style>

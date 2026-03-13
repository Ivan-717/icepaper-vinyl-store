<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router=useRouter()

//侧边栏菜单折叠状态
const collapsed=ref(false)

//退出登录
const handleLogout=()=>{
    localStorage.removeItem('adminToken')
    router.push('/admin/login')
}

//菜单列表
const menuItems=[
    {key:'dashboard',icon:'📊',label:'工作台',path:'/admin/dashboard'},
    { key: 'shop', icon: '🏪', label: '营业状态', path: '/admin/shop' },
    { key: 'employee', icon: '👥', label: '员工管理', path: '/admin/employee' },
    { key: 'product', icon: '💿', label: '商品管理', path: '/admin/product' },
    { key: 'combo', icon: '📦', label: '套餐管理', path: '/admin/combo' },
    { key: 'order', icon: '📋', label: '订单管理', path: '/admin/order' },
    { key: 'statistics', icon: '📈', label: '数据统计', path: '/admin/statistics' }
]


</script>

<template>
    <div class="admin-layout">
        <!-- collapsed:true时：折叠状态 -->
        <aside class="sidebar" :class="{collapsed}">
            <div class="logo">
                🎵 冰纸管理
            </div>

            <div class="menu">
                <div v-for="item in menuItems"
                    :key="item.key"
                    class="menu-item"
                    @click="router.push(item.path)">
                    <span class="menu-icon">{{ item.icon }}</span>
                    <span class="menu-label">{{ item.label }}</span>
                </div>
            </div>
        </aside>

        <!-- 右侧主区域 -->
        <div class="main">
            <header class="header">
                <div class="header-left">
                    <button class="collapse-btn" @click="collapsed=!collapsed">
                        {{ collapsed ? '▶' : '◀' }}
                    </button>
                </div>

                <div class="header-right">
                    <span class="admin-name">管理员</span>
                    <button class="logout-btn" @click="handleLogout">
                        退出
                    </button>
                </div>
            </header>

            <main class="content">
                <!-- 路由视图换位符 -->
                <RouterView/>
            </main>
        </div> 
    </div>

    
</template>



<style scoped>
/* 整体布局 */
.admin-layout {
  min-height: 100vh;
  display: flex;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: #2c3e50;
  color: white;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 80px;
}

/* Logo区域 */
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 1.2rem;
  font-weight: bold;
  background: #1e2b36;
  overflow: hidden;
  white-space: nowrap;
}

/* 菜单 */
.menu {
  flex: 1;
  padding: 1rem 0;
}

.menu-item {
  padding: 0.75rem 1.5rem;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background 0.3s;
  white-space: nowrap;
}

.menu-item:hover {
  background: #34495e;
}

.menu-icon {
  width: 24px;
  margin-right: 12px;
  font-size: 1.2rem;
}

.collapsed .menu-item {
  padding: 0.75rem 0;
  justify-content: center;
}

.collapsed .menu-label {
  display: none;
}

.collapsed .menu-icon {
  margin-right: 0;
}

/* 右侧主内容区 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  width: 100%;           /* 确保占满宽度 */
  max-width: none;       /* 取消任何最大宽度限制 */
}

/* 顶部状态栏 */
.header {
  height: 60px;
  background: white;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.collapse-btn {
  border: none;
  background: white;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
}

.collapse-btn:hover {
  background: #f5f5f5;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.admin-name {
  color: #333;
  font-weight: 500;
}

.logout-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ff4444;
  background: white;
  color: #ff4444;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #ff4444;
  color: white;
}

/* 主要内容区域 - 去掉内边距 */
.content {
  flex: 1;
  padding: 0;            /* 改为0，让里面的元素自己控制边距 */
  overflow-y: auto;
  width: 100%;
  max-width: none;
  box-sizing: border-box;
}

/* 让 RouterView 里面的内容自己控制内边距 */
.content > * {
  padding: 1.5rem;
  width: 100%;
  max-width: none;
  box-sizing: border-box;
}

/* 如果你想让某些页面有最大宽度限制，可以这样 */
.content .content-container {
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
}

/* 覆盖任何可能的全局样式 */
.admin-layout,
.admin-layout .main,
.admin-layout .content {
  max-width: none !important;
  width: 100% !important;
}
</style>
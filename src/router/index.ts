import HomeView from '@/views/HomeView.vue'
import ProduceDetail from '@/views/ProduceDetail.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path:'/',  //访问根路径
      name:'home', 
      component:HomeView //显示HomeView组件
    },
    {
      path:'/product/:id',
      name:'product-detail',
      component:ProduceDetail
    }
  ],
})

export default router

import CartView from '@/views/CartView.vue'
import HomeView from '@/views/HomeView.vue'
import ProduceDetail from '@/views/ProduceDetail.vue'
import SearchView from '@/views/SearchView.vue'
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
      //:id是一个占位符，任何/product/:xxx路径，都配这个路由，并把xxx自动存在params.id里
      path:'/products/:id',
      name:'product-detail',
      component:ProduceDetail
    },{
      path:'/cart',
      name:'cart',
      component:CartView
    },{
      path:'/search',
      name:'search',
      component:SearchView
    }
  ],
})

export default router

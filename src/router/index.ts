import AdminLayout from '@/views/admin/AdminLayout.vue'
import AdminLogin from '@/views/admin/AdminLogin.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
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
    },{
      path:'/admin/login',  //管理员登录页
      name:'admin-login',
      component:AdminLogin
    },{
      path:'/admin',
      component:AdminLayout,
      children:[
        {
          path:"dashboard",
          name:"admin-dashboard",
          component: () => import('../views/admin/Dashboard.vue')
        },{
          path:'shop',
          name:'admin-shop',
          //不常用的页面：懒加载(用的时候才加载)
          component:()=>import('../views/admin/ShopStatus.vue')
        },{
          path:'employee',
          name:'admin-employee',
          component:()=>import('../views/admin/Employee.vue')
        },{
          path:'product',
          name:'admin-product',
          component:()=>import('../views/admin/Product.vue')
        },{
          path:"combo",
          name:'admin-combo',
          component:()=>import('../views/admin/Combo.vue')
        },{
          path:'statistics',
          name:'admin-statistics',
          component:()=>import('../views/admin/Statistics.vue')
        },{
          path:'order',
          name:'admin-order',
          component:()=>import("../views/admin/Order.vue")
        }
      ]
    }
  ],
})

//路由守卫：每次页面跳转（路由切换前），都会先执行这个函数里的逻辑
/*
  to:要跳转的目标界面
  form:从哪个页面跳过来的
  next:允许跳转
*/
router.beforeEach((to,from,next)=>{
  //如果登录页，直接放行
  if(to.path==='/admin/login'){
    next()
    return
  }

  //如果是管理后台的路由,检查token
  if(to.path.startsWith('/admin')){
    const token=localStorage.getItem('adminToken')
    if(!token){
      next('/admin/login')
      return
    }
  }

  next()
})

export default router

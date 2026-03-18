<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';
import ProductForm
 from './ProductForm.vue';
import type { Category } from '@/api/product';

//商品数据类型
interface Product{
    id: number
    categoryId: number
    name: string
    artist: string
    price: number
    stock: number
    image: string
    description: string
    status: number  // 1上架 0下架
    createTime: string
    updateTime: string
}

const productList=ref<Product[]>([])
const loading=ref(false)

//加载商品列表
const loadList=async()=>{
    loading.value=true
    try{
        const res=await request.get('/admin/products')
        productList.value=res.data
    }catch(error){
        console.error('加载失败：',error)
    }finally{
        loading.value=false
    }
}

//切换上下架状态
const toggleStatus=async(id:number,currentStatus:number)=>{
    const newStatus=currentStatus===1?0:1
    try{
        await request.put(`/admin/products/${id}/status`,{status:newStatus})
        //更新本地数据
        const product=productList.value.find(item=>item.id===id)
        if(product){
            product.status=newStatus
        }
    }catch(error){
        console.error('更新状态失败:',error)
        alert('操作失败')
    }
}

//删除商品
const handleDelete=async(id:number)=>{
    if(!confirm('确定要删除该商品吗？')){
        return
    }
    try{
        await request.delete(`/admin/products/${id}`)
        //重新加载列表
        loadList()
    }catch(error){
        console.error('删除失败:',error)
    }
}

onMounted(()=>{
    loadList()
    loadCategories()
})

const formRef=ref<InstanceType<typeof ProductForm>|null>(null)

const openAdd=()=>{
  formRef.value?.openAdd()
}

const openEdit=(item:Product)=>{
  formRef.value?.openEdit(item)
}

const refreshList=()=>{
  loadList()
}

//分类列表
const categories=ref<Category[]>([])

//加载分类
const loadCategories=async()=>{
  try{
    const res=await request.get('/categories')
    categories.value=res.data
  }catch(error){
    console.log('加载分类失败：',error)
  }
}

//根据id获取分类
const getCategoryName=(categoryId:number)=>{
  const cat=categories.value.find(c=>c.id===categoryId)
  return cat ? cat.name : '未知'
}
    
</script>

<template>
    <div class="product">
        <div class="header">
            <h1>💿 商品管理</h1>
            <button class="add-btn" @click="openAdd">+新增商品</button>
        </div>

        <div v-if="loading" class="loading">
            加载中...
        </div>

        <div v-else class="table-container">
            <table class="product-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>封面</th>
                        <th>专辑名</th>
                        <th>艺术家</th>
                        <th>价格</th>
                        <th>库存</th>
                        <th>分类</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in productList" :key="item.id">
                        <td>{{ item.id }}</td>
                        <td>
                            <img :src="item.image" class="product-image">
                        </td>
                        <td>{{ item.name }}</td>
                        <td>{{ item.artist }}</td>
                        <td>¥{{ item.price }}</td>
                        <td>{{ item.stock }}</td>
                        <td>{{ getCategoryName(item.categoryId) }}</td>
                        <td>
                            <button class="status-btn" 
                                :class="{'status-active':item.status===1,'status-inactive':item.status===0}"
                                @click="toggleStatus(item.id,item.status)">
                                {{ item.status===1 ? '已上架' : '已下架' }}
                            </button>
                        </td>

                        <td class="actions">
                            <button class="action-btn edit" @click="openEdit(item)">编辑</button>
                            <button class="action-btn delete" @click="handleDelete(item.id)">删除</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 弹窗 -->
        <ProductForm ref="formRef" @success="refreshList"/> 
    </div>
</template>

<style scoped>
.product {
  padding: 1rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0;
  color: #333;
}

.add-btn {
  padding: 0.75rem 1.5rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.add-btn:hover {
  background: #3aa876;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow-x: auto;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1200px;
}

.product-table th {
  background: #f5f5f5;
  padding: 1rem;
  text-align: left;
  font-weight: 500;
  color: #333;
  border-bottom: 2px solid #ddd;
}

.product-table td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.product-table tbody tr:hover {
  background: #f9f9f9;
}

.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.status-btn {
  padding: 0.25rem 0.75rem;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s;
}

.status-active {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-active:hover {
  background: #2e7d32;
  color: white;
}

.status-inactive {
  background: #ffebee;
  color: #c62828;
}

.status-inactive:hover {
  background: #c62828;
  color: white;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  padding: 0.25rem 0.5rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #f5f5f5;
}

.edit:hover {
  border-color: #42b983;
  color: #42b983;
}

.delete:hover {
  border-color: #ff4444;
  color: #ff4444;
}
</style>













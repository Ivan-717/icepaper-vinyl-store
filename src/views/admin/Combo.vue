<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';
import ComboFrom from './ComboForm.vue';

interface ComboItem {
  productId: number
  quantity: number
}


//套餐数据类型
interface Combo{
    id:number
    name:string
    price:number
    image:string
    description:string
    status:number
    items?:ComboItem[]
    createTime:string
    updateTime:string
}

const comboList=ref<Combo[]>([])
const loading=ref(false)

//加载套餐列表
const loadList=async()=>{
    loading.value=true
    try{
        const res=await request.get('/admin/combo')
        comboList.value=res.data
    }catch(error){
        console.error('加载失败:',error)
    }finally{
        loading.value=false
    }
}

//切换上下架
const toggleStatus=async(id:number,currentStatus:number)=>{
    const newStatus=currentStatus===1?0:1
    try{
        await request.put(`/admin/combo/${id}/status`,{status:newStatus})
        const combo=comboList.value.find(item=>item.id===id)
        if(combo){
            combo.status=newStatus
        }
    }catch(error){
        console.error('更新操作失败')
        alert('操作失败')
    }
}

//删除套餐
const handleDelete=async(id:number)=>{
    if(!confirm('确定要删除掉该套餐吗？')){
        return
    }
    try{
        await request.delete(`/admin/combo/${id}`)
        loadList()
    }catch(error){
        console.error('删除失败:',error)
    }
}

onMounted(()=>{
    loadList()
})

const formRef=ref<InstanceType<typeof ComboFrom>|null>(null)

//弹窗需要items数据  
const openEdit=async(item:Combo)=>{
    try{
        const res=await request.get(`/admin/combo/${item.id}`)
        formRef.value?.openEdit(res.data)
    }catch(error){
        console.error('获取套餐详情失败:', error)
    }
}

const openAdd=()=>{
  formRef.value?.openAdd()
}

const refreshList=()=>{
    loadList()
}
</script>

<template>
    <div class="combo">
        <div class="header">
            <h1>📦 套餐管理</h1>
            <button class="add-btn" @click="openAdd">+新增套餐</button>
        </div>

        <div v-if="loading" class="loading">
            加载中...
        </div>

        <div v-else class="combo-grid">
            <div v-for="item in comboList" :key="item.id" class="combo-card">
                <img :src="item.image" alt="combo-image">
                <div class="combo-info">
                    <h3>{{ item.name }}</h3>
                    <p class="price">¥{{ item.price }}</p>
                    <p class="description">{{ item.description }}</p>
                    <div class="combo-actions">
                        <button class="status-btn"
                            :class="{'status-active':item.status===1,'status-inactive':item.status===0}"
                            @click="toggleStatus(item.id,item.status)"
                        >
                            {{ item.status===1?'已上架':'已下架' }}
                        </button>
                        <button class="edit-btn" @click="openEdit(item)">编辑</button>
                        <button class="delete-btn" @click="handleDelete(item.id)">删除</button>
                    </div>
                </div>
            </div>
        </div>

        <ComboFrom ref="formRef" @success="refreshList"/>

    </div>


</template>

<style scoped>
.combo {
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

.combo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.combo-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: all 0.3s;
}

.combo-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.combo-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.combo-info {
  padding: 1rem;
}

.combo-info h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: #333;
}

.price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #f56c6c;
  margin: 0.5rem 0;
}

.description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.combo-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
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

.edit-btn {
  padding: 0.25rem 0.75rem;
  border: 1px solid #42b983;
  background: white;
  color: #42b983;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn:hover {
  background: #42b983;
  color: white;
}

.delete-btn {
  padding: 0.25rem 0.75rem;
  border: 1px solid #ff4444;
  background: white;
  color: #ff4444;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn:hover {
  background: #ff4444;
  color: white;
}
</style>
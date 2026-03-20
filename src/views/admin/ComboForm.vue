<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';

//模式
const mode=ref<'add'|'edit'>('edit')

//商品数据类型
interface Product{
    id:number
    name:string
    artist:string
    price:number
}

//套餐内商品项
interface ComboItem{
    productId:number
    quantity:number
    productName?:string
    productArtist?:string
    productPrice?:number
}

//套餐数据类型
interface Combo{
    id?:number
    name:string
    price:number
    image:string
    description:string
    status:number
    items:ComboItem[]
}

//弹窗显示状态
const visible=ref(false)

//商品列表
const products=ref<Product[]>([])

//表单数据
const formData=ref<Combo>({
    name: '',
    price: 0,
    image: '',
    description: '',
    status: 1,
    items: []
})

//加载商品列表
const loadProducts=async()=>{
    try{
        const res =await request.get('/admin/products')
        products.value=res.data
    }catch(error){
        console.error('加载商品失败：',error)
    }
}

//打开弹窗，新增
const openAdd=async()=>{
  mode.value='add'
  await loadProducts()
  formData.value={
    name:'',
    price:0,
    image:'',
    description: '',
    status: 1,
    items: []
  }
  visible.value=true
}

//打开弹窗，编辑
const openEdit=async(data:Combo)=>{
  mode.value='edit'
  //先加载商品列表
  await loadProducts()
  formData.value={...data}
  visible.value=true
}

//关闭弹窗
const close=()=>{
    visible.value=false
}

//添加一个商品项
const addItem=()=>{
    formData.value.items.push({
        productId:0,
        quantity:1
    })
}

//移除商品项
const removeItem=(index:number)=>{
    formData.value.items.splice(index,1)
}

//提交表单
const submit=async()=>{
    try{
      if(mode.value=='add'){
        await request.post('/admin/combo',formData.value)
        alert('新增成功')
      }else{
        await request.put('/admin/combo',formData.value)
        alert('更新成功')
      }
        close()
        emit('success')
    }catch(error){
        console.error('操作失败：',error)
        alert('操作失败，请重试')
    }
}

//定义事件
const emit=defineEmits(['success'])

//暴露方法
defineExpose({
    openEdit,
    openAdd
})

</script>

<template>
    <Teleport to="body">
        <div v-if="visible" class="modal-overlay" @click.self="close">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>{{ mode==='add' ? '新增套餐' : '编辑套餐' }}</h2>
                    <button class="close-btn" @click="close">✕</button>
                </div>

                <form @submit.prevent="submit" class="modal-form">
                    <div class="form-item">
                        <label>套餐名称</label>
                        <input type="text" v-model="formData.name" placeholder="请输入套餐名称">
                    </div>
                    
                
                    <div class="form-item">
                        <label>套餐价格</label>
                        <input type="number" v-model.number="formData.price" step="0.01" placeholder="请输入价格">

                    </div>


                    <div class="form-item">
                        <label>图片路径</label>
                        <input type="text" v-model="formData.image" placeholder="请输入图片路径">
                    </div>

                    <div class="form-item">
                        <label>描述</label>
                        <textarea v-model="formData.description" rows="2" placeholder="请输入描述"></textarea>
                    </div>

                    <div class="form-item">
                        <label>包含商品</label>
                        <div v-for="(item,index) in formData.items" :key="index" class="item-row">
                            <select v-model="item.productId" class="product-select">
                                <option value="0">请选择商品</option>
                                <option v-for="p in products" :key="p.id" :value="p.id">
                                    {{ p.name }} - {{ p.artist }} ({{ p.price }})
                                </option>
                            </select>
                            <input type="number" v-model.number="item.quantity" min="1" class="quantity-input" placeholder="数量">
                            <button type="button" class="remove-btn" @click="removeItem(index)">✕</button>
                        </div>
                        <button type="button" class="add-item-btn" @click="addItem">
                          +添加商品
                        </button>
                    </div>

                    <div class="form-item">
                        <label>状态</label>
                        <div class="radio-group">
                            <label class="radio">
                                <input type="radio" v-model="formData.status" :value="1">
                                <span>上架</span>
                            </label>
                            <label class="radio">
                                <input type="radio" v-model="formData.status" :value="0">
                                <span>下架</span>
                            </label>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="cancel-btn" @click="close">取消</button>
                        <button type="submit" class="submit-btn">保存</button>
                    </div>
                </form>
            </div>

        </div>
    </Teleport>
</template>


<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.modal-form {
  padding: 1.5rem;
}

.form-item {
  margin-bottom: 1.2rem;
}

.form-item label {
  display: block;
  margin-bottom: 0.3rem;
  color: #333;
  font-size: 0.9rem;
}

.form-item input[type="text"],
.form-item input[type="number"],
.form-item select,
.form-item textarea {
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  box-sizing: border-box;
}

.form-item input:focus,
.form-item select:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #42b983;
}

/* 商品行：专辑名下拉框变长 */
.item-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  align-items: center;
}

/* 专辑名下拉框：占更多宽度 */
.product-select {
  flex: 100%;
  padding: 0.6rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

/* 数量输入框：固定宽度 */
.quantity-input {
  width: 80px;
  padding: 0.6rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
}

.remove-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ff4444;
  background: white;
  color: #ff4444;
  border-radius: 4px;
  cursor: pointer;
  flex-shrink: 0;
}

.remove-btn:hover {
  background: #ff4444;
  color: white;
}

.add-item-btn {
  padding: 0.3rem 1rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.add-item-btn:hover {
  background: #3aa876;
}

.radio-group {
  display: flex;
  gap: 1.5rem;
}

.radio {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  cursor: pointer;
}

.radio input[type="radio"] {
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-btn {
  padding: 0.6rem 1.5rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn:hover {
  background: #f5f5f5;
}

.submit-btn {
  padding: 0.6rem 1.5rem;
  border: none;
  background: #42b983;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn:hover {
  background: #3aa876;
}
</style>
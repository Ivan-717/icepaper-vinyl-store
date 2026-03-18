<script setup lang="ts">
import { ref,onMounted } from 'vue';
import request from '@/api/request';

//分类数据类型
interface Category{
    id:number
    name:string
}

//上平数据类型
interface Product{
    id?: number
    categoryId: number
    name: string
    artist: string
    price: number
    stock: number
    image: string
    description: string
    status: number
}

//弹窗显示状态
const visible=ref(false)

//分类列表
const categories=ref<Category[]>([])

//表单数据
const formData=ref<Product>({
    categoryId:0,
    name:'',
    artist:'',
    price:0,
    stock:0,
    image:'',
    description:'',
    status:1
})

//当前模式
const mode=ref<'add'|'edit'>('add')

//表单校验错误
const errors=ref<Record<string,string>>({})

//加载分类列表
const loadCategories=async()=>{
    try{
        const res=await request.get('/categories')
        categories.value=res.data
        //分类列表有数据  当前表单分类还是0，就把第一个选项赋值
        if(categories.value.length>0 && formData.value.categoryId===0){
            formData.value.categoryId=categories.value[0]?.id??0
        }
    }catch(error){
        console.error('分类加载失败:',error)
    }
}

//打开弹窗（新增）
const openAdd=()=>{
    mode.value='add'
    formData.value={
        categoryId:categories.value[0]?.id||0,
        name: '',
        artist: '',
        price: 0,
        stock: 0,
        image: '',
        description: '',
        status: 1
    }
    errors.value={}
    visible.value=true
}

//打开弹窗（编辑）
const openEdit=(data:Product)=>{
    mode.value='edit'
    formData.value={...data}
    errors.value={}
    visible.value=true
}

//关闭弹窗
const close=()=>{
    visible.value=false
}

//表单校验
const validate=()=>{
    const newErrors:Record<string,string>={}

    if(!formData.value.categoryId){
        newErrors.categoryId='请选择分类'
    }
    if(!formData.value.name){
        newErrors.name='专辑名不能为空'
    }
    if (!formData.value.artist) {
    newErrors.artist = '艺术家不能为空'
    }
    if (formData.value.price <= 0) {
      newErrors.price = '价格必须大于0'
    }
    if (formData.value.stock < 0) {
      newErrors.stock = '库存不能小于0'
    }
    if (!formData.value.image) {
      newErrors.image = '图片路径不能为空'
    }

    errors.value=newErrors
    //newErrors为空。返回true
    return Object.keys(newErrors).length===0
}

//提交表单
const submit=async()=>{
    if(!validate()){
        return
    }

    try{
        if(mode.value==='add'){
            await request.post('/admin/products',formData.value)
            alert('新增成功')
        }else{
            await request.put('/admin/products',formData.value)
            alert('更新成功')
        }
        close()
        emit('success')
    }catch(error){
        console.error('操作失败：',error)
        alert('操作失败，请重试')
    }
}

//加载分类
onMounted(()=>{
    loadCategories()
})

//定义事件
const emit=defineEmits(['success'])



defineExpose({
    openAdd,
    openEdit
})

</script>

<template>
    <Teleport to="body">
        <div v-if="visible" class="modal-overlay" @click.self="close">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>{{ mode==='add'?'新增商品':'编辑商品' }}</h2>
                    <button class="close-btn" @click="close">✕</button>
                </div>

                <form @submit.prevent="submit" class="modal-form">
                    <!-- 分类 -->
                    <div class="form-item">
                        <label>分类<span class="required">*</span></label>
                        <select v-model="formData.categoryId" class="select-input">
                            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                                {{ cat.name }}
                            </option>
                        </select>
                        <span v-if="errors.categoryId" class="error">
                            {{ errors.categoryId }}
                        </span>
                    </div>

                    <!-- 专辑名 -->
                    <div class="form-item">
                        <label>专辑名<span class="required">*</span></label>
                        <input type="text" v-model="formData.name" placeholder="请输入专辑名">
                        <span v-if="errors.name" class="error">{{ errors.name }}</span>
                    </div> 

                    <!-- 艺术家 -->
                    <div class="form-item">
                        <label>艺术家<span class="required">*</span></label>
                        <input type="text" v-model="formData.artist" placeholder="请输入艺术家">
                        <span v-if="errors.artist" class="error">{{ errors.artist }}</span>
                    </div> 

                    <!-- 价格 -->
                    <div class="form-item">
                        <label>价格 <span class="required">*</span></label>
                        <input v-model.number="formData.price" type="number" step="0.01" placeholder="请输入价格">
                        <span v-if="errors.price" class="error">{{ errors.price }}</span>
                    </div>

                    <!-- 库存 -->
                    <div class="form-item">
                        <label>库存 <span class="required">*</span></label>
                        <input v-model.number="formData.stock" type="number" placeholder="请输入库存">
                        <span v-if="errors.stock" class="error">{{ errors.stock }}</span>
                    </div>

                     <!-- 图片路径 -->
                    <div class="form-item">
                        <label>图片路径 <span class="required">*</span></label>
                        <input v-model="formData.image" type="text" placeholder="请输入图片路径">
                        <span v-if="errors.image" class="error">{{ errors.image }}</span>
                    </div>
                
                    <!-- 描述 -->
                    <div class="form-item">
                        <label>描述</label>
                        <textarea v-model="formData.description" rows="3" placeholder="请输入商品描述"></textarea>
                    </div>

                    <!-- 状态 -->
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

                    <!-- 按钮组 -->
                    <div class="form-actions">
                        <button type="button" class="cancel-btn" @click="close">取消</button>
                        <button type="submit" class="submit-btn">
                            {{ mode==='add' ? '新增' : '保存' }}
                        </button>
                    </div> 
                </form>


            </div>
        </div>
    </Teleport>
</template>

<style scoped>
/* 遮罩层 */
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

/* 弹窗内容 */
.modal-content {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

/* 弹窗头部 */
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

/* 表单 */
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

.select-input {
  height: 38px;
}

textarea {
  resize: vertical;
  font-family: inherit;
}

.required {
  color: #ff4444;
}

.error {
  display: block;
  margin-top: 0.3rem;
  color: #ff4444;
  font-size: 0.8rem;
}

/* 单选框组 */
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

/* 按钮组 */
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








<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAddressList,addAddress, updateAddress, deleteAddress, type Address } from '@/api/address';
import provinces from 'china-division/dist/provinces.json'
import cities from 'china-division/dist/cities.json'
import areas from 'china-division/dist/areas.json'

// 类型定义
interface Province{
    code:string
    name:string
}

interface City{
    code:string
    name:string
    provinceCode:string
}

interface Area{
    code:string
    name:string
    cityCode:string
}

const addressList=ref<Address[]>([])
const loading=ref(false)
//弹窗状态显示
const showModal=ref(false)
//是否为便急忙模式（false：新增）
const editing=ref(false)
const currentAddress=ref<Address>({
    receiver: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: 0
})

//省市区数据
const provinceList=ref<Province[]>(provinces)
const cityList=ref<City[]>([])
const areaList=ref<Area[]>([])

//表单校验
const validateForm=()=>{
    //手机号正则
    const phoneReg=/^1[3-9]\d{9}$/
    if(!phoneReg.test(currentAddress.value.phone)){
        alert('请输入正确手机号')
        return false
    }
    if(!currentAddress.value.receiver.trim()){
        alert('请输入收货人姓名')
        return false
    }
    if(!currentAddress.value.detail.trim()){
        alert('请输入详细地址')
        return false
    }
    if(!currentAddress.value.province){
        alert('请选择省份')
        return false
    }
    if (!currentAddress.value.city) {
    alert('请选择城市')
    return false
    }
    if (!currentAddress.value.district) {
      alert('请选择区/县')
      return false
    }
    return true
}

//省区市联动
//省变化，更新城市列表
const onProvinceChange=(provinceName:string,keepCity:boolean=false)=>{
    const province=provinceList.value.find(p=>p.name===provinceName)
    if(province){
        cityList.value=cities.filter((c:City)=>c.provinceCode===province.code)
        if(!keepCity){
            currentAddress.value.city=''
            currentAddress.value.district=''
        }
        areaList.value=[]
    }else{
        cityList.value=[]
        areaList.value=[]
    }
}

const onCityChange=(cityName:string,keepDistrict: boolean = false)=>{
    const city=cityList.value.find(c=>c.name===cityName)
    if(city){
        areaList.value=areas.filter((a:Area)=>a.cityCode===city.code)
        if (!keepDistrict) {
          currentAddress.value.district = ''
        }
    }else{
        areaList.value=[]
    }
}

//加载地址列表
const loadAddresses=async()=>{
    loading.value=true
    try{
        const res=await getAddressList()
        addressList.value=res.data
    }catch(error){
        console.log("加载地址失败：",error)
    }finally{
        loading.value=false
    }
}

//打开新增弹窗
const openAdd=()=>{
    editing.value=false
    currentAddress.value={
        receiver: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detail: '',
        isDefault: 0
    }
    showModal.value=true
}

//打开编辑地址弹窗 
const openEdit=(addr:Address)=>{
    editing.value=true
    //..addr:把对象里的所有属性，一个一个拆开来，拷贝到新对象里
    currentAddress.value={...addr}

    cityList.value=[]
    areaList.value=[]
    
    if(addr.province){
        //true代表不请客城市
        onProvinceChange(addr.province,true)
        if(addr.city){
            onCityChange(addr.city,true)
        }
    }
    showModal.value=true
}

//保存地址
const saveAddress=async()=>{

    if(!validateForm()){
        return
    }

    try{
        if(editing.value){
            await updateAddress(currentAddress.value)
        }else{
            await addAddress(currentAddress.value)
        }
        showModal.value=false
        loadAddresses()
    }catch(error){
        console.error('保存地址失败：',error)
        alert('操作失败')
    }
}

//删除地址
const handleDelete=async(id:number)=>{
    if(confirm('你确定要删除该地址吗？')){
        try{
            await deleteAddress(id)
            loadAddresses()
        }catch(error){
            console.error('删除失败：',error)
        }
    }
}


onMounted(()=>{
    loadAddresses()
})
</script>


<template>
    <div class="address-page">
        <div class="header">
            <h1>收货地址</h1>
            <button class="add-btn" @click="openAdd">+新增地址</button>
        </div>

        <div v-if="loading" class="loading">加载中...</div>

        <div v-else-if="addressList.length===0" class="empty">
            <p>暂无收货地址</p>
            <button @click="openAdd" class="add-first-btn">添加第一个地址</button>
        </div>

        <div v-else class="address-list">
            <div v-for="addr in addressList" :key="addr.id" class="address-card">
                <div class="address-info">
                    <div class="receiver">
                        <span class="name">{{ addr.receiver }}</span>
                        <span class="phone">{{ addr.phone }}</span>
                        <span v-if="addr.isDefault===1" class="default-badge">默认</span>
                    </div>
                </div>    
                <div class="address-detail">
                    {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}
                </div>
                <!-- 操作按钮 -->
                <div class="address-actions">
                    <button class="edit-btn" @click="openEdit(addr)">编辑</button>
                    <button class="delete-btn" @click="handleDelete(addr.id!)">删除</button>
                    <button v-if="addr.isDefault!==1" class="set-default-btn" >设为默认</button>
                </div> 
            </div>
        </div>
    </div>

    <!-- 新增编辑弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal=false">
        <div class="modal-content">
            <h2>{{ editing ? '编辑地址' : '新增地址' }}</h2>
            <form @submit.prevent="saveAddress" class="address-form">
                <input v-model="currentAddress.receiver" placeholder="收货人姓名" required>

                <input v-model="currentAddress.phone" placeholder="手机号" required pattern="1[3-9]\d{9}" title="请输入正确的11位手机号">

                <select v-model="currentAddress.province" @change="onProvinceChange(currentAddress.province)" required>
                    <option value="">请选择省份</option>
                    <option v-for="p in provinceList" :key="p.code" :value="p.name">{{ p.name }}</option>
                </select>

                <select v-model="currentAddress.city" @change="onCityChange(currentAddress.city)" :disabled="!currentAddress.province" required>
                    <option value="">请选择城市</option>
                    <option v-for="c in cityList" :key="c.code" :value="c.name">{{ c.name }}</option>
                </select>

                <select v-model="currentAddress.district" :disabled="!currentAddress.city" required>
                    <option value="">请选择区/县</option>
                    <option v-for="a in areaList" :key="a.code" :value="a.name">{{ a.name }}</option>
                </select>

                <input v-model="currentAddress.detail" placeholder="详细地址（街道、门牌号）" required>
            
                <label class="default-checkbox">
                    <input type="checkbox" v-model="currentAddress.isDefault" :true-value="1" :false-value="0">
                    设为默认地址
                </label>

                <div class="form-actions">
                    <button type="button" @click="showModal=false">取消</button>
                    <button type="submit">保存</button>
                </div>
            </form>

        </div>

    </div>
</template>


<style scoped>
.address-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  background: #fef9f9;
  min-height: 100vh;
  width: 100%;
}

/* 页面头部 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #ffe0e0;
}

.header h1 {
  font-size: 2rem;
  color: #d96c6c;
  margin: 0;
  font-weight: 500;
}

.add-btn {
  padding: 0.7rem 1.8rem;
  background: #ffb3b3;
  color: #fff;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 6px rgba(255, 179, 179, 0.3);
}

.add-btn:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 179, 0.4);
}

/* 加载和空状态 */
.loading, .empty {
  text-align: center;
  padding: 4rem;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
}

.loading {
  color: #ffb3b3;
  font-size: 1.1rem;
}

.empty {
  color: #d96c6c;
}

.add-first-btn {
  margin-top: 1rem;
  padding: 0.7rem 2rem;
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 1rem;
}

.add-first-btn:hover {
  background: #ff9b9b;
  transform: scale(1.02);
}

/* 地址列表容器 - 网格布局，一行4个 */
.address-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

/* 单个地址卡片 */
.address-card {
  background: white;
  border-radius: 20px;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  border: 1px solid #ffe0e0;
  position: relative;
}

.address-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
  border-color: #ffd0d0;
}

/* 地址信息区域 */
.address-info {
  flex: 1;
}

/* 收货人信息行 */
.receiver {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 0.6rem;
  flex-wrap: wrap;
  padding-right: 60px;
}

.name {
  font-weight: 600;
  font-size: 1.1rem;
  color: #4a4a4a;
}

.phone {
  color: #9e9e9e;
  font-size: 0.85rem;
}

/* 默认地址标签 - 右上角 */
.default-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #ffb3b3;
  color: white;
  padding: 3px 12px;
  border-radius: 30px;
  font-size: 0.7rem;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 详细地址 */
.address-detail {
  color: #888;
  font-size: 0.85rem;
  line-height: 1.5;
  word-break: break-all;
  margin-top: 0.2rem;
}

/* 操作按钮组 */
.address-actions {
  display: flex;
  gap: 0.6rem;
  margin-top: 0.5rem;
  justify-content: flex-end;
  border-top: 1px solid #ffe0e0;
  padding-top: 0.8rem;
}

.address-actions button {
  padding: 0.35rem 1rem;
  border-radius: 30px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.2s;
  border: none;
}

.edit-btn {
  background: #fff0f0;
  color: #ff9b9b;
}

.edit-btn:hover {
  background: #ffb3b3;
  color: white;
}

.delete-btn {
  background: #fff0f0;
  color: #ff9b9b;
}

.delete-btn:hover {
  background: #ffb3b3;
  color: white;
}

.set-default-btn {
  background: #fff0f0;
  color: #ff9b9b;
}

.set-default-btn:hover {
  background: #ffb3b3;
  color: white;
}

/* 响应式：大屏幕保持4列，中等屏幕3列，小屏幕2列 */
@media (max-width: 1200px) {
  .address-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .address-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .address-list {
    grid-template-columns: 1fr;
  }
}

/* ---------- 弹窗样式 ---------- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 28px;
  padding: 2rem;
  width: 520px;
  max-width: 90%;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  animation: fadeInUp 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-content h2 {
  margin: 0 0 1.5rem;
  font-size: 1.4rem;
  color: #d96c6c;
  text-align: center;
  font-weight: 500;
}

/* 弹窗表单 */
.address-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.address-form input,
.address-form select {
  padding: 0.75rem 1rem;
  border: 1px solid #ffe0e0;
  border-radius: 16px;
  font-size: 0.9rem;
  transition: all 0.3s;
  background: #fff;
}

.address-form input:focus,
.address-form select:focus {
  outline: none;
  border-color: #ffb3b3;
  box-shadow: 0 0 0 3px rgba(255, 179, 179, 0.2);
}

.address-form select:disabled {
  background: #faf5f5;
  color: #bbb;
  cursor: not-allowed;
}

/* 默认地址复选框 */
.default-checkbox {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.5rem 0;
  cursor: pointer;
  color: #d96c6c;
  font-size: 0.9rem;
}

.default-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #ffb3b3;
}

/* 表单按钮组 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.form-actions button {
  padding: 0.6rem 1.5rem;
  border-radius: 30px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s;
  border: none;
}

.form-actions button:first-child {
  background: #f0f0f0;
  color: #999;
}

.form-actions button:first-child:hover {
  background: #e5e5e5;
}

.form-actions button:last-child {
  background: #ffb3b3;
  color: white;
  box-shadow: 0 2px 8px rgba(255, 179, 179, 0.3);
}

.form-actions button:last-child:hover {
  background: #ff9b9b;
  transform: translateY(-2px);
}
</style>
<script setup lang="ts">
import { ref,watch } from 'vue';
import { addEmployee,updateEmployee,type Employee} from '../../api/employee'

//弹窗显示状态
const visible=ref(false)

//表单数据
const formData=ref<Employee>({
    username: '',
    name: '',
    phone: '',
    role: 'employee',
    status: 1
})

//表单校验错误
//record:Record<键类型, 值类型>
const errors=ref<Record<string,string>>({})

//当前模式:add或edit
//<'add'|'edit'>:值只能是这两个  ('add')：初始值是add
const mode=ref<'add'|'edit'>('add')

//打开弹窗（新增）
const openAdd=()=>{
    mode.value='add'
    formData.value={
        username: '',
        name: '',
        phone: '',
        role: 'employee',
        status: 1
    }
    errors.value={}
    visible.value=true
}

//打开弹窗（编辑）
const openEdit=(data:Employee)=>{
    mode.value='edit'
    formData.value={...data} //浅拷贝
    errors.value={}
    visible.value=true
}

const close=()=>{
    visible.value=false
}

//表单校验
const validate=()=>{
    const newErrors:Record<string,string>={}
    if(!formData.value.username){
        newErrors.username='用户名不能为空'
    }
    if(!formData.value.name){
        newErrors.name='姓名不能为空'
    }
    if(!formData.value.phone){
        newErrors.phone='手机号不能为空'
    }
    //!/^1[3-9]\d{9}$/.test(formData.value.phone):手机格式校验
    else if(!/^1[3-9]\d{9}$/.test(formData.value.phone)){
        newErrors.phone=('手机号格式不正确')
    }

    errors.value=newErrors

    //Object.keys:提取对象的所有键名
    //length===0:没有错误返回True
    return Object.keys(newErrors).length===0
}

//提交表单
const submit=async()=>{
    if(!validate()) {
        return
    }

    try{
        if(mode.value==='add'){
            await addEmployee(formData.value)
            alert('新增成功！')
        }else{
            await updateEmployee(formData.value)
            alert('更新成功')
        }
        close()
        //触发列表刷新（通过事件通知父组件）
        emit('success')
    }catch(error){
        console.error('操作失败：',error)
        alert('操作失败，请重试')
    }
}

//定义事件
const emit=defineEmits(['success'])

//暴露方法给父组件
defineExpose({
    openAdd,
    openEdit
})

</script>

<template>
    <Teleport to="Body">
        <!-- 只有点击遮罩层本身，才触发关闭方法 -->
        <div v-if="visible" class="modal-overlay" @click.self="close">
            <!-- 弹窗内容 -->
            <div class="modal-content">
                <div class="modal-header">
                    <h2>{{ mode==='add'?'新增员工':'编辑员工' }}</h2>
                    <button class="close-btn" @click="close">✕</button>
                </div>
          
                <!-- prevent:阻止默认提交 -->
                <form @submit.prevent="submit" class="modal-form">
                    <!-- 用户名 -->
                    <div class="form-item">
                        <label for="username">
                            <!-- *代表必须填 -->
                            用户名 <span class="required">*</span>
                        </label>
                        <!-- :disable:绑定值为true,元素禁用 -->
                        <input id="username" v-model="formData.username" type="text"
                            placeholder="请输入用户名" :disabled="mode==='edit'"
                        >
                        <span v-if="errors.username" class="error">
                            {{ errors.username }}
                        </span>
                    </div>

                    <!-- 姓名 -->
                    <div class="form-item">
                        <label for="name">
                            <!-- *代表必须填 -->
                            姓名 <span class="required">*</span>
                        </label>
                        <!-- :disable:绑定值为true,元素禁用 -->
                        <input id="name" v-model="formData.name" type="text"
                            placeholder="请输入姓名"
                        >
                        <span v-if="errors.username" class="error">
                            {{ errors.name }}
                        </span>
                    </div>

                    <!-- 手机号 -->
                    <div class="form-item">
                        <label for="phone">手机号 <span class="required">*</span></label>
                        <input id="phone" v-model="formData.phone"
                            type="text" placeholder="请输入手机号"
                        >
                        <span v-if="errors.phone" class="error">
                            {{ errors.phone }}
                        </span>
                    </div>

                    <!-- 角色 -->
                    <div class="form-item">
                        <label>角色</label>
                        <div class="radio-group">
                            <label class="radio">
                                <input type="radio" v-model="formData.role" value="employee">
                                <span>员工</span>
                            </label>
                            <label class="radio">
                                <input type="radio" v-model="formData.role" value="admin">
                                <span>管理员</span>
                            </label>
                        </div>
                    </div> 

                    <!-- 状态 -->
                    <div class="form-item">
                        <label>状态</label>
                        <div class="radio-group">
                            <label class="radio">
                                <input type="radio" v-model="formData.status" :value="1">
                                <span>启动</span>
                            </label>
                            <label class="radio">
                                <input type="radio" v-model="formData.status" :value="0">
                                <span>禁用</span>
                            </label>
                        </div>
                    </div> 

                    <!-- 按钮组 -->
                    <div class="form-actions">
                        <button type="button" class="cancel-btn" @click="close">
                            取消
                        </button>
                        <!-- type="submit":触发表单的submit事件 -->
                        <button type="submit" class="submit-btn">
                            {{ mode==='add'?'新增':'保存' }}
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

.form-item input[type="text"] {
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  box-sizing: border-box;
}

.form-item input[type="text"]:focus {
  outline: none;
  border-color: #42b983;
}

.form-item input[type="text"]:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
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
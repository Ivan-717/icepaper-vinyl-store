<script setup lang="ts">
import { ref,onMounted } from 'vue';
import{
    getEmployeeList, 
    updateEmployeeStatus,
    resetPassword,
    deleteEmployee,
    type Employee 
} from '../../api/employee'
import { fa } from 'element-plus/lib/locale/index.js';
import { idText } from 'typescript';

//员工列表数据
const employeeList=ref<Employee[]>([])
const loading=ref(false)

//加载列表
const loadList=async()=>{
    loading.value=true
    try{
        const res=await getEmployeeList()
        employeeList.value=res.data
    }catch(error){
        console.error('加载失败:',error);
    }finally{
        loading.value=false
    }

}

//切换状态(启用/禁用)
const toggleStatus=async(id:number,currentStatus:number)=>{
    const newStatus=currentStatus===1?0:1
    try{
        await updateEmployeeStatus(id,newStatus)
        //更新本地数据
        const employee=employeeList.value.find(item=>item.id===id)
        if(employee){
            employee.status=newStatus
        }
    }catch(error){
        console.error('更新状态失败:',error)
        alert('操作失败')
    }
}

//重置密码
const handleResetPassword=async(id:number)=>{
    if(!confirm('确定要重置该员工的密码为123456吗')){
        return
    }
    try{
        await resetPassword(id)
        alert('密码已经重置为123456')
    }catch(error){
        console.error('重置密码失败:',error)
    }
}

//删除员工
const handleDelete=async(id:number)=>{
    if(!confirm('确定要删除该员工吗？')){
        return
    }
    try{
        await deleteEmployee(id)
        loadList()
    }catch(error){
        console.error('删除失败：',error)
    }
}

onMounted(()=>{
    loadList()
})
</script>

<template>
    <div class="employee">
        <div class="header">
            <h1>👥 员工管理</h1>
            <button class="add-btn">+新增员工</button>
        </div>

        <div v-if="loading" class="loading">
            加载中...
        </div>

        <div v-else class="table-container">
            <!-- table：表格标签 -->
            <table class="employee-table">
                <!-- thead:表格头部 -->
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>用户名</th>
                        <th>姓名</th>
                        <th>手机号</th>
                        <th>角色</th>
                        <th>状态</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="emp in employeeList" :key="emp.id">
                        <td>{{ emp.id }}</td>
                        <td>{{ emp.username }}</td>
                        <td>{{ emp.name }}</td>
                        <td>{{ emp.phone }}</td>
                        <td>
                            <span class="role" :class="{'role-admin':emp.role==='admin'}">
                                {{ emp.role==='admin'?'管理员':'员工' }}
                            </span>
                        </td>
                        <td>
                            <button class="status-btn"
                                :class="{'status-active':emp.status===1,'status=inactive':emp.status===0}"
                                @click="toggleStatus(emp.id!,emp.status)">
                                {{ emp.status===1? '启用' : '禁用'}}
                            </button>
                        </td>
                        <td>
                            {{ emp.createTime?.slice(0,10) }}
                        </td>
                        <td class="actions">
                            <button class="action-btn edit">编辑</button>
                            <button class="action-btn reset" @click="handleResetPassword(emp.id!)">
                                重置密码
                            </button>
                            <button class="action-btn delete" @click="handleDelete(emp.id!)">
                                删除
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<style scoped>
.employee {
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

.employee-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1000px;
}

.employee-table th {
  background: #f5f5f5;
  padding: 1rem;
  text-align: left;
  font-weight: 500;
  color: #333;
  border-bottom: 2px solid #ddd;
}

.employee-table td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.employee-table tbody tr:hover {
  background: #f9f9f9;
}

.role {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  background: #e8f5e9;
  color: #2e7d32;
}

.role-admin {
  background: #fff3e0;
  color: #ef6c00;
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

.reset:hover {
  border-color: #ff9800;
  color: #ff9800;
}

.delete:hover {
  border-color: #ff4444;
  color: #ff4444;
}
</style>
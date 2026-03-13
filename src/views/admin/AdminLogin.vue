<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router=useRouter()

//表单数据
const loginForm=ref({
    username:'',
    password:''
})

//加载状态
const loading=ref(false)
//错误提示
const errorMsg=ref('')

//登录方法
const handleLogin=async()=>{
    if(!loginForm.value.username||!loginForm.value.password){
        errorMsg.value='请输入用户名和密码'
        return
    }

    loading.value=true
    errorMsg.value=''

    try{
        //调用后端接口

        //模拟登陆成功
        if(loginForm.value.username==='admin' && loginForm.value.password==='123456'){
            //存储登录状态
            localStorage.setItem('adminToken','fake-token')
            router.push('/admin/dashboard')

        }else{
            errorMsg.value='用户名或密码错误'
        }
    }catch(error){
        errorMsg.value='登录失败，请稍后重试'
        console.error('登录错误:',error)
    }finally{
        loading.value=false
    }
}
</script>

<template>
    <div class="login-card">
        <h1 class="title">🎵 冰纸专辑店</h1>
        <h2 class="subtitle">管理员登录</h2>

        <!-- 登录表单  表单提交时，会统一收集数据-->
        <!-- submit:监听表单提交，.prevent：阻止默认刷新 -->
        <form @submit.prevent="handleLogin">
            <!-- 用户名输入框 -->
            <div class="form-item">
                <!-- for:把文本和输入框绑定，点击文本也能激活输入框 -->
                <label for="username">用户名</label>
                <input type="text"
                    id="username"
                    v-model="loginForm.username"
                    placeholder="请输入用户名"
                    autocomplete="off"
                >
            </div> 

            <!-- 密码输入框 -->
            <div class="form-item">
                <label for="password">密码</label>
                <input
                    id="password"
                    v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                >
            </div> 

            <!-- 错误提示 -->
            <div v-if="errorMsg" class="error-message">{{ errorMsg }}</div>
            
            <!-- 登录按钮 -->
            <button type="submit" class="login-btn" :disabled="loading">
                {{ loading ? '登录中...' : '登录' }}
            </button> 
        </form> 
    </div>
</template>

<style scoped>
/* 整个页面容器：全屏居中 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #42b983 0%, #2c3e50 100%);
}

/* 登录卡片 */
.login-card {
  width: 400px;
  padding: 2.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

/* 标题 */
.title {
  margin: 0 0 0.5rem;
  color: #333;
  text-align: center;
  font-size: 1.8rem;
}

.subtitle {
  margin: 0 0 2rem;
  color: #666;
  text-align: center;
  font-size: 1.2rem;
  font-weight: normal;
}

/* 表单 */
.login-form {
  width: 100%;
}

/* 表单项 */
.form-item {
  margin-bottom: 1.5rem;
}

.form-item label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 0.9rem;
}

.form-item input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-item input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

/* 错误提示 */
.error-message {
  margin-bottom: 1rem;
  padding: 0.75rem;
  background: #fef0f0;
  color: #f56c6c;
  border-radius: 4px;
  font-size: 0.9rem;
  text-align: center;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  padding: 0.75rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover:not(:disabled) {
  background: #3aa876;
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

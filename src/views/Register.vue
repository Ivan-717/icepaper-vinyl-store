<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { userRegister } from '@/api/user';

const router=useRouter()
const username=ref('')
const password=ref('')
const confirmPassword=ref('')
const phone=ref('')

const handleRegister=async()=>{
    //表单验证
    if(!username.value||!password.value){
        alert("请填写用户名和密码")
        return
    }
    if(password.value!==password.value){
        alert('两次输入的密码不一致')
        return
    }
    try{
        const res=await userRegister({
            username:username.value,
            password:password.value,
            phone:phone.value
        })
        if(res.data.success){
            alert("注册成功！请登录")
            router.push('/login')
        }
    }catch(error){
        console.error('注册失败：',error)
        alert('注册失败')
    }
}

const goToLogin=()=>{
    router.push("/login")
}
</script>

<template>
    <div class="register-container">
        <div class="register-card">
            <h1>🎵 冰纸专辑店</h1>
            <h2>用户注册</h2>
        
            <form @submit.prevent="handleRegister">
                <div class="form-item">
                    <input type="text" v-model="username" placeholder="用户名">
                </div>

                <div class="form-item">
                    <input type="password" v-model="password" placeholder="密码">
                </div>

                <div class="form-item">
                    <input type="password" v-model="confirmPassword" placeholder="确认密码">
                </div>

                <div class="form-item">
                    <input type="tel" v-model="phone" placeholder="手机号">
                </div>

                <button type="submit" class="register-btn">注册</button>
            </form>

            <p class="login-link">
                已有账号？<a @click="goToLogin">立即登录</a>
            </p>
        </div>
    </div>
</template>






<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* background: #42b983; */
}

.register-card {
  width: 400px;
  padding: 2.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  text-align: center;
}

.register-card h1 {
  margin: 0 0 0.5rem;
  color: #42b983;
  font-size: 1.8rem;
}

.register-card h2 {
  margin: 0 0 2rem;
  color: #333;
  font-size: 1.2rem;
  font-weight: normal;
}

.form-item {
  margin-bottom: 1rem;
}

.form-item input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-item input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66,185,131,0.1);
}

.register-btn {
  width: 100%;
  padding: 0.75rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s;
  margin-top: 0.5rem;
}

.register-btn:hover {
  background: #3aa876;
}

.login-link {
  margin-top: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.login-link a {
  color: #42b983;
  cursor: pointer;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { userLogin } from '@/api/user';
import { useCartStore } from '@/stores/cart';

const router=useRouter()
const username=ref('')
const password=ref('')

const cartStore=useCartStore()

const handleLogin=async()=>{
    try{
        const res=await userLogin({
            username:username.value,
            password:password.value
        })

        //保存Token
        localStorage.setItem('userToken',res.data.token)
        localStorage.setItem('userInfo',JSON.stringify(res.data))


        //给未登录的人用的
        //获取本地购物车
        const localCart=JSON.parse(localStorage.getItem('cart')||'[]')

        if(localCart.length>0){
          await cartStore.syncLocalCart(localCart)
          localStorage.removeItem('cart')
        }

        //跳转到首页
        //强行刷新界面
        window.location.href=('/')
    }catch(error){
        console.error('登录失败:',error)
        alert('登录失败')
    }
}
const goToRegister=()=>{
    router.push('/register')
}

//游客进入
const enterAsGuest=()=>{
  //清除任何可能残留的token
  localStorage.removeItem('userToken')
  localStorage.removeItem('userInfo')
  router.push('/')
}

</script>

<template>
    <div class="login-container">
        <div class="login-card">
            <h1>🎵 冰纸专辑店</h1>
            <h2>用户登录</h2>

            <form @submit.prevent="handleLogin">
                <div class="form-item">
                    <input type="text" v-model="username" placeholder="用户名">
                </div>
                <div class="form-item">
                    <input type="text" v-model="password" placeholder="密码">
                </div>
                <button type="submit" class="login-btn">登录</button>
            </form>

            <div class="guest-entry">
              <button @click="enterAsGuest" class="guest-btn">以游客身份进入</button>
            </div>

            <p class="register-link">
                还没有账号？<a @click="goToRegister">立即注册</a>
            </p>
        </div>
    </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* background:  */
}

.login-card {
  width: 400px;
  padding: 2.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  text-align: center;
}

.login-card h1 {
  margin: 0 0 0.5rem;
  color: #42b983;
  font-size: 1.8rem;
}

.login-card h2 {
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

.login-btn:hover {
  background: #3aa876;
}

.register-link {
  margin-top: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.register-link a {
  color: #42b983;
  cursor: pointer;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 游客区域 */
.guest-entry {
  margin-top: 1rem;
  text-align: center;
}

.guest-btn {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 0.9rem;
  text-decoration: underline;
  padding: 0;
}

.guest-btn:hover {
  color: #42b983;
}
</style>
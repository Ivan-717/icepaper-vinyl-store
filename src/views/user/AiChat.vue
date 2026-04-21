<script setup lang="ts">
import { ref,nextTick } from 'vue';
import request from '@/api/request';

//对象数组
const messages=ref<{role:string;content:string}[]>([
    {role:'assistant',content:'你好呀!我是冰纸专辑店的AI客服,有什么可以帮你的吗?😊'}
])
const inputMessage=ref('')
const loading=ref(false)
const messageContainer=ref<HTMLElement>()

//快捷问题
const quickQuestions=[
    '有什么周杰伦的专辑？',
    '刘聪有哪些专辑？',
    '店铺营业时间是几点？',
    '怎么退换货？'
]    

//滚动到底部
const scrollToBottom=async()=>{
    //等待vue完成dom更新
    await nextTick()
    if(messageContainer.value){
        //滚动条滚到最底部
        messageContainer.value.scrollTop=messageContainer.value.scrollHeight
    }
}

//发送消息
const sendMessage=async()=>{
    if(!inputMessage.value.trim() || loading.value){
        return
    }

    const userMessage=inputMessage.value.trim()

    //添加用户消息
    messages.value.push({role:'user',content:userMessage})
    inputMessage.value=''
    await scrollToBottom()

    //调用后端API
    loading.value=true
    try{
        const res=await request.post('/ai/chat',{message:userMessage})
        messages.value.push({role:'assistant',content:res.data.reply})
    }catch(error){
        console.error('发送失败:', error)
        messages.value.push({ role: 'assistant', content: '抱歉，出了点问题，请稍后再试。' })
    }finally{
        loading.value=false
        await scrollToBottom()
    }
}

//发送快捷问题
const sendQuickQuestion=(question:string)=>{
    inputMessage.value=question
    sendMessage()
}
</script>

<template>
    <div class="ai-chat">
        <div class="chat-header">
            <h2>🤖 AI智能客服</h2>
            <button class="close-btn" @click="$router.back()">✕</button>
        </div>

        <div class="chat-messages" ref="messagesContainer">
            <div v-for="(msg,index) in messages" :key="index" :class="['message',msg.role]">
                <div class="message-content">{{ msg.content }}</div>
            </div>
            <div v-if="loading" class="message assistant">
                <div class="message-content typing">正在输入...</div>
            </div>
        </div>

        <div class="chat-input">
            <input v-model="inputMessage" @keyup.enter="sendMessage" placeholder="输入你的问题" :disabled="loading">
            <button @click="sendMessage" :disabled="loading||!inputMessage.trim()">
                发送
            </button>
        </div>

        <!-- 快捷提问按钮 -->
        <div class="quick-questions">
            <button v-for="q in quickQuestions" :key="q" @click="sendQuickQuestion(q)" class="quick-btn">
                {{ q }}
            </button>
        </div> 
    </div>
</template>

<style scoped>
.ai-chat {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background: #fef9f9;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #ffb3b3;
  color: white;
  flex-shrink: 0;
}

.chat-header h2 {
  margin: 0;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1.3rem;
  cursor: pointer;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message {
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.user .message-content {
  background: #ffb3b3;
  color: white;
  border-radius: 16px 16px 4px 16px;
}

.message.assistant .message-content {
  background: #f0f0f0;
  color: #333;
  border-radius: 16px 16px 16px 4px;
}

.message-content {
  max-width: 70%;
  padding: 10px 16px;
  font-size: 0.95rem;
  line-height: 1.5;
  word-wrap: break-word;
}

.typing {
  color: #999;
}

.chat-input {
  display: flex;
  padding: 16px 24px;
  border-top: 1px solid #ffe0e0;
  gap: 12px;
  background: white;
  flex-shrink: 0;
}

.chat-input input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ffe0e0;
  border-radius: 24px;
  outline: none;
  font-size: 0.95rem;
}

.chat-input input:focus {
  border-color: #ffb3b3;
}

.chat-input button {
  padding: 12px 28px;
  background: #ffb3b3;
  color: white;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px 24px;
  border-top: 1px solid #ffe0e0;
  background: #fafafa;
  flex-shrink: 0;
}

.quick-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #ffb3b3;
  border-radius: 20px;
  font-size: 13px;
  color: #d96c6c;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: #ffb3b3;
  color: white;
}
</style>
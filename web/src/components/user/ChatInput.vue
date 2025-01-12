<template>
  <div class="user-input">
    <button class="new-chat-btn" @click="new_chat">新的聊天</button>
    <input type="text" v-model="userInput" @keyup.enter="send" placeholder="请输入...">
    <button class="send-btn" @click="send">发送</button>
  </div>
</template>

<script>
export default {
  name: "ChatInput",
  props: {
    sendMessage: Function,
    newChat: Function
  },
  data() {
    return {
      userInput: ''
    };
  },
  methods: {
    send() {
      this.sendMessage(this.userInput);
      this.userInput = ''; // 清空输入框
    },
    new_chat() {
      this.newChat();
    }
  }
};
</script>

<style>
.user-input {
  display: flex;
  justify-content: center;
  margin: 6px;
  align-items: center; /* 垂直居中对齐 */
}

.user-input input {
  width: 70%; /* 控制输入框的宽度 */
  padding: 8px;
  margin: 0 10px;
  border: 2px solid #ccc;
  border-radius: 15px; /* 设置输入框的边角为圆滑 */
}

.new-chat-btn {
  padding: 10px 20px;
  color: white;
  border: none;
  border-radius: 20px; /* 更圆滑的按钮 */
  background-color: #df973e; /* 橙色背景 */
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease; /* 平滑过渡效果 */
}

.new-chat-btn:hover {
  background-color: #aa7937; /* 深橙色背景 */
}

.send-btn {
  padding: 10px 20px;
  color: white;
  border: none;
  border-radius: 20px; /* 更圆滑的按钮 */
  background-color: #8fbed5; /* 绿色背景 */
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease; /* 平滑过渡效果 */
}

.send-btn:hover {
  background-color: #7699aa; /* 深绿色背景 */
}
</style>



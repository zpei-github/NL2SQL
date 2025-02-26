<template>
  <div class="main-container">
    <!-- Chat container that holds messages and input fixed at the bottom -->
    <div class="chat-container">
      <div class="multi-messages-container">
        <div class="messages-container" ref="messageContainer1">
          <EngineBox1 ref="box1"
                      :allmessages="messages"/>
        </div>
      </div>
      <div class="chat-input">
        <ChatInput :sendMessage="sendMessage"
                   :newChat="handleNewChat"
                   :getSQL="getSQL"/>
      </div>
    </div>
  </div>
</template>

<script>
import ChatInput from '/src/components/user/ChatInput.vue';
import EngineBox1 from "/src/components/user/EngineBox1.vue";
import http from "@/utils/http_interceptor";
import db from '@/utils/db';

export default {
  components: {
    ChatInput,
    EngineBox1
  },
  data() {
    return {
      messages: []
    };
  },

  async mounted() {
    await db.initDB();
    this.loadMessages();
  },

  methods: {
    async loadMessages() {
      this.messages = await db.getAllChats();
    },
    async sendMessage(message) {
      if (message.trim() !== '') {
        try{
          const my_question = {
            messageId: this.messages.length + 1,
            sender: "user",
            uuid: null,
            content: message,
            messageMark:null,
            createTime: Date.now()};
          let response1 = await http.post("/chat/sendMessage", {
            question:my_question,
            existMessages:this.messages});
          if(response1.data){
            for(let i = 0; i < response1.data.length; i++){
              this.messages.push(response1.data[i])
              await db.addChat(response1.data[i]);
            }
          }
        } catch (error) {
          console.error("Failed to send message: ", error);
          this.$message.error("Failed to send message: ", error.message);
        }
      }
    },

    handleNewChat() {
        this.$confirm('此操作将删除已有的聊天记录, 是否继续?', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 确认删除操作
          this.newChat();
        }).catch(() => {
          // 取消删除操作
          this.$message({
            type: 'info',
            message: '已取消'
          });
        });
    },

    async newChat() {
      try {
        await db.clearChats();
        this.loadMessages();
        // 取消删除操作
        this.$message({
          type: 'info',
          message: '已新建'
        });
      } catch (error) {
        console.error("Error creating new chat:", error);
        this.$message.error("Error creating new chat:", error.message);
      }
    },

    async getSQL(message) {
      try {
        const my_question = {
          messageId: this.messages.length + 1,
          sender: "user",
          uuid: null,
          messageMark:null,
          content: message,
          createTime: Date.now()};

        let response1 = await http.post("/chat/get_sql", {
          question:my_question,
          existMessages:this.messages});



        if(response1.data){
          for(let i = 0; i < response1.data.length; i++){
            this.messages.push(response1.data[i])
            await db.addChat(response1.data[i]);
          }
        }
      } catch (error) {
        console.error("Error get sql:", error);
        this.$message.error("Error get sql:", error.message);
      }
    }
  }
};
</script>

<style>
.main-container {
  display: flex;
  height: 100vh; /* Full viewport height */
}

.sidebar {
  width: 200px;
  background-color: #f8f9fa;
  overflow-y: auto; /* Scrollable */
  transition: all 0.3s; /* 平滑过渡效果 */
}

.chat-container {
  flex: 1; /* Fill remaining space */
  display: flex;
  flex-direction: column; /* Stack vertically */
  transition: all 0.3s; /* 平滑过渡效果 */
}

.multi-messages-container {
  flex: 1; /* Fill remaining space */
  display: flex;
  flex-direction: row; /* Lay out children side by side */
}

.messages-container {
  flex: 1; /* Each takes half the available space */
  overflow-y: auto; /* Scrollable */
}

.chat-input {
  padding: 8px;
  margin-top: auto; /* Keep this at the bottom */
}

.sidebar-toggle {
  position: absolute; /* 使用绝对定位 */
  top: 50%;           /* 垂直居中 */
  transform: translateY(-50%); /* 确保完全居中 */
  z-index: 100;       /* 确保按钮在侧边栏之上 */
  background-color: #607D8B; /* 蓝灰色背景 */
  border: none;       /* 去除边框 */
  color: white;       /* 白色文本 */
}

/* 修改鼠标悬停和激活状态下的背景颜色 */
.el-button.sidebar-toggle:hover,
.el-button.sidebar-toggle:focus {
  background-color: #546E7A; /* 更深的蓝灰色 */
  color: #FFFFFF;            /* 保持文本颜色不变 */
}

/* 根据侧边栏状态调整按钮位置 */
.main-container {
  position: relative; /* 父容器使用相对定位 */
}
</style>

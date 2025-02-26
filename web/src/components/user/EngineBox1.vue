<template>
  <div class="engine-container">
    <div class="message-container">
      <div
          v-for="message in allmessages"
          :key="message.id"
          class="message"
          :class="{'user-message': message.sender === 'user', 'client-message': message.sender === 'client'}"
      >
        <!-- 使用 v-html 渲染 Markdown 内容 -->
        <div class="message-content" v-html="renderMarkdown(message.content)"></div>
      </div>
    </div>
    <!-- Updated floating-window position -->
  </div>
</template>

<script>
import { marked } from 'marked';
import DOMPurify from 'dompurify';

// 配置 marked 选项
marked.setOptions({
  breaks: true, // 将换行符转换为 <br>
});

export default {
  props: {
    allmessages: Array,
  },
  methods: {
    // 将 Markdown 转换为安全的 HTML
    renderMarkdown(content) {
      const rawHtml = marked(content || ''); // 解析 Markdown
      return DOMPurify.sanitize(rawHtml, {
        ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'code', 'pre', 'span', 'ul', 'ol', 'li', 'h1', 'h2', 'h3', 'blockquote'], // 允许的标签
      });
    },
  },
};
</script>

<style scoped>
.engine-container {
  display: flex;
  flex-direction: column;
  height: 90vh; /* Assuming full viewport height for vertical centering */
  position: relative; /* Needed for absolute positioning of floating-window */
}

.floating-window {
  position: absolute;
  top: 3%; /* Align vertically to the middle */
  left: 0; /* Stick to the left edge of the container */
  transform: translateY(-50%); /* Center vertically */
  width: 150px;
  padding: 1px;
  background: white;
  border-radius: 5px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.message {
  max-width: 70%;
  padding: 10px;
  border-radius: 10px;
  margin: 10px;
}

.message-container {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.user-message {
  align-self: flex-end;
  background-color: #dcf8c6;
  margin-right: 10px;
}

.client-message {
  align-self: flex-start;
  background-color: #ffffff;
  margin-left: 10px;
}

/* Markdown 样式 */
.message-content {
  line-height: 1.6;
}

.message-content p {
  margin: 0.5em 0;
}

.message-content strong {
  font-weight: 600;
}

.message-content em {
  font-style: italic;
}

.message-content pre {
  background: #f4f4f4;
  padding: 1em;
  border-radius: 4px;
  overflow-x: auto;
}

.message-content code {
  font-family: Consolas, Monaco, monospace;
  background: #f4f4f4;
  padding: 0.2em 0.4em;
  border-radius: 3px;
}

.message-content blockquote {
  border-left: 3px solid #ddd;
  margin: 0.5em 0;
  padding-left: 1em;
  color: #666;
}
</style>
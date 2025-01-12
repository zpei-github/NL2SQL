<template>
  <div class="engine-container">
    <div class="message-container">
      <div v-for="message in allmessages" class="message"
           :class="{'user-message': message.sender === 'user', 'client-message': message.sender === 'client'}">
        {{ message.content }}
      </div>
    </div>
    <!-- Updated floating-window position -->
  </div>
</template>

<script>
export default {
  props: {
    allmessages:Array,
  },
}
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
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
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
  background-color: #DCF8C6;
  margin-right: 10px;
}

.client-message {
  align-self: flex-start;
  background-color: #FFFFFF;
  margin-left: 10px;
}
</style>


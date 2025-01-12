export default {
    dbName: 'ChatDatabase',
    storeName: 'ChatStore',
    db: null,

    // 初始化数据库
    initDB() {
        return new Promise((resolve, reject) => {
            const request = window.indexedDB.open(this.dbName, 1);

            request.onerror = (e) => reject(e);
            request.onsuccess = (e) => {
                this.db = e.target.result;
                resolve(this.db);
            };
            request.onupgradeneeded = (e) => {
                const db = e.target.result;
                if (!db.objectStoreNames.contains(this.storeName)) {
                    db.createObjectStore(this.storeName, { keyPath: 'messageId' });
                }
            };
        });
    },

    // 添加聊天记录
    addChat(chat) {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction([this.storeName], 'readwrite');
            const store = transaction.objectStore(this.storeName);
            const request = store.add(chat);

            request.onsuccess = () => resolve(chat);
            request.onerror = (e) => reject(e);
        });
    },

    // 获取所有聊天记录
    getAllChats() {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction([this.storeName], 'readonly');
            const store = transaction.objectStore(this.storeName);
            const request = store.getAll();

            request.onsuccess = (e) => resolve(e.target.result);
            request.onerror = (e) => reject(e);
        });
    },

    // 根据 messageId 获取聊天记录
    getChatById(messageId) {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction([this.storeName], 'readonly');
            const store = transaction.objectStore(this.storeName);
            const request = store.get(messageId);

            request.onsuccess = (e) => resolve(e.target.result);
            request.onerror = (e) => reject(e);
        });
    },

    // 删除指定聊天记录
    deleteChat(messageId) {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction([this.storeName], 'readwrite');
            const store = transaction.objectStore(this.storeName);
            const request = store.delete(messageId);

            request.onsuccess = () => resolve(messageId);
            request.onerror = (e) => reject(e);
        });
    },

    // 删除所有聊天记录
    clearChats() {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction([this.storeName], 'readwrite');
            const store = transaction.objectStore(this.storeName);
            const request = store.clear();

            request.onsuccess = () => resolve();
            request.onerror = (e) => reject(e);
        });
    },
};

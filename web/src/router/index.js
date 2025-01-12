import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 防止连续点击多次路由报错
let routerPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/chat',
    name: 'userChat',
    component: () => import( '../views/user/ChatPage.vue'),
    meta: {title: 'userChat'},
  },
]

const router = new VueRouter({
  //mode: 'history',
  base: process.env.BASE_URL,
  routes,
})

export default router


import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import WeeklyMealPlan from '@/components/WeeklyMealPlan.vue'
import LoginView from '@/views/LoginView.vue'
import { getTokenExp } from '@/utils/jwt'
import RegisterView from '@/views/RegisterView.vue'
import ProfileView from '@/views/ProfileView.vue'

const routes = [
  { path: '/',          name: 'home',       component: HomeView },
  { path: '/about',     name: 'about',      component: () => import('../views/AboutView.vue') },
  { path: '/meal-plan', name: 'meal-plan',  component: WeeklyMealPlan },
  { path: '/login',     name: 'login',      component: LoginView },
  { path: '/register',  name: 'register',   component: RegisterView},
  { path: '/profile',   name: 'profile',    component: ProfileView}
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 全局前置守衛：檢查 JWT 是否過期
router.beforeEach((to, from, next) => {
  // 如果要去「非登入頁」且有 token，就檢查過期
  const token = localStorage.getItem('jwt')
  if (token && Date.now() > getTokenExp(token)) {
    localStorage.removeItem('jwt')
    return next({ name: 'login' })
  }
  next()
})
//舊版router
// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes: [
//     {
//       path: '/',
//       name: 'home',
//       component: HomeView,
//     },
//     {
//       path: '/about',
//       name: 'about',
//       // route level code-splitting
//       // this generates a separate chunk (About.[hash].js) for this route
//       // which is lazy-loaded when the route is visited.
//       component: () => import('../views/AboutView.vue'),
//     },
//     {
//       path: '/meal-plan',
//       name: 'meal-plan',
//       component: WeeklyMealPlan
//     },
//     {
//       path: '/login',
//       name: 'login',
//       component: LoginView
//     }
//   ],
// })

export default router

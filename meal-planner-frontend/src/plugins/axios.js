import axios from 'axios'

// 1. 全局 Base URL
axios.defaults.baseURL = 'http://localhost:8080'

//舊版攔截器，不使用原因是因為要分割明確
// //2.單一攔截器，先檢查token有沒有過期，再帶入，再帶入Authorization
// axios.interceptors.request.use(config => {
//   const token = localStorage.getItem('jwt')
//   if (token) {
//     // 解析 exp
//     try {
//       const payload = JSON.parse(atob(token.split('.')[1]))
//       if (Date.now() > payload.exp * 1000) {
//         // 过期，清除并跳登录
//         localStorage.removeItem('jwt')
//         //window.location.href = '/login'
//         router.push({name:'login'}) //測試是否可以去掉清除token的畫面閃爍
//         return Promise.reject(new Error('Token expired'))
//       }
//     } catch { /* 忽略 */ }

//     // Token 有效
//     config.headers.Authorization = `Bearer ${token}`
//   }
//   return config
// })

axios.interceptors.request.use(cfg =>{
  const token =localStorage.getItem('jwt')
  if(token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

export default axios
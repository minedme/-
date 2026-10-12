import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/login/index.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/register/index.vue')
    },
    {
      path: '/',
      component: () => import('../views/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '仪表盘' }
        },
        {
          path: 'goals',
          name: 'Goals',
          component: () => import('../views/goals/index.vue'),
          meta: { title: '学习目标' }
        },
        {
          path: 'goals/:id/plans',
          name: 'Plans',
          component: () => import('../views/plans/index.vue'),
          meta: { title: '学习计划' }
        },
        {
          path: 'progress',
          name: 'Progress',
          component: () => import('../views/progress/index.vue'),
          meta: { title: '学习进度' }
        },
        {
          path: 'reminders',
          name: 'Reminders',
          component: () => import('../views/reminders/index.vue'),
          meta: { title: '学习提醒' }
        },
        {
          path: 'resources',
          name: 'Resources',
          component: () => import('../views/resources/index.vue'),
          meta: { title: '资源推荐' }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  if (!token && to.path !== '/login' && to.path !== '/register') {
    next('/login')
  } else if (token && (to.path === '/login' || to.path === '/register')) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router

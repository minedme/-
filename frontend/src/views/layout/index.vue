<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">学习计划助手</div>
      <el-menu :default-active="$route.path" router class="menu" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/goals">
          <el-icon><Target /></el-icon>
          <span>学习目标</span>
        </el-menu-item>
        <el-menu-item index="/progress">
          <el-icon><TrendCharts /></el-icon>
          <span>学习进度</span>
        </el-menu-item>
        <el-menu-item index="/reminders">
          <el-icon><Bell /></el-icon>
          <span>学习提醒</span>
        </el-menu-item>
        <el-menu-item index="/resources">
          <el-icon><Collection /></el-icon>
          <span>资源推荐</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userStore.userInfo?.username || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (cmd: string) => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.menu {
  border-right: none;
}
.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.header-right {
  cursor: pointer;
}
.user-info {
  color: #606266;
}
.main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>

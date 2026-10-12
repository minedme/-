<template>
  <div>
    <h2>仪表盘</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-title">学习目标</div>
            <div class="stat-value">{{ goals.length }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-title">今日任务</div>
            <div class="stat-value">{{ todayPlans.length }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-title">已完成</div>
            <div class="stat-value">{{ completedCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>今日学习计划</span>
      </template>
      <el-empty v-if="todayPlans.length === 0" description="暂无今日计划" />
      <el-timeline v-else>
        <el-timeline-item v-for="plan in todayPlans" :key="plan.id" :type="plan.status === 2 ? 'success' : 'primary'">
          <el-card>
            <h4>{{ plan.title }}</h4>
            <p>{{ plan.content }}</p>
            <el-tag :type="plan.status === 2 ? 'success' : plan.status === 1 ? 'warning' : 'info'">
              {{ plan.status === 2 ? '已完成' : plan.status === 1 ? '进行中' : '未开始' }}
            </el-tag>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { listGoals } from '../../api/goal'
import { listPlans } from '../../api/plan'

const goals = ref<any[]>([])
const allPlans = ref<any[]>([])

const today = new Date().toISOString().split('T')[0]

const todayPlans = computed(() => {
  return allPlans.value.filter(p => p.planDate === today)
})

const completedCount = computed(() => {
  return allPlans.value.filter(p => p.status === 2).length
})

onMounted(async () => {
  const res: any = await listGoals()
  goals.value = res.data || []
  for (const goal of goals.value) {
    const planRes: any = await listPlans(goal.id)
    if (planRes.data) {
      allPlans.value.push(...planRes.data)
    }
  }
})
</script>

<style scoped>
.stat-item {
  text-align: center;
}
.stat-title {
  color: #909399;
  font-size: 14px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}
</style>

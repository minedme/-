<template>
  <div>
    <div style="display: flex; align-items: center; gap: 10px">
      <el-button @click="$router.back()" circle><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>学习计划</h2>
    </div>

    <el-card style="margin-top: 20px">
      <el-calendar v-model="calendarDate">
        <template #date-cell="{ data }">
          <div style="height: 100%">
            <p>{{ data.day.split('-')[2] }}</p>
            <div v-for="plan in getPlansByDate(data.day)" :key="plan.id">
              <el-tag size="small" :type="plan.status === 2 ? 'success' : plan.status === 1 ? 'warning' : 'info'" style="margin-top: 2px">
                {{ plan.title.substring(0, 8) }}
              </el-tag>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>计划列表</span>
      </template>
      <el-table :data="plans" style="width: 100%">
        <el-table-column prop="planDate" label="日期" width="120" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 2 ? '已完成' : row.status === 1 ? '进行中' : '未开始' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="updateStatus(row, 1)">进行中</el-button>
            <el-button size="small" type="success" @click="updateStatus(row, 2)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listPlans, updatePlanStatus } from '../../api/plan'

const route = useRoute()
const goalId = route.params.id as string
const plans = ref<any[]>([])
const calendarDate = ref(new Date())

const loadPlans = async () => {
  const res: any = await listPlans(goalId)
  plans.value = res.data || []
}

const getPlansByDate = (date: string) => {
  return plans.value.filter(p => p.planDate === date)
}

const updateStatus = async (row: any, status: number) => {
  await updatePlanStatus(row.id, status)
  ElMessage.success('状态更新成功')
  loadPlans()
}

onMounted(loadPlans)
</script>

<style scoped>
:deep(.el-calendar-day) {
  height: 80px;
}
</style>

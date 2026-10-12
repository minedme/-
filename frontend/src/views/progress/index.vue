<template>
  <div>
    <h2>学习进度</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12" v-for="stat in stats" :key="stat.goalId" style="margin-bottom: 20px">
        <el-card>
          <template #header>
            <span>{{ stat.goalTitle }}</span>
          </template>
          <div style="display: flex; align-items: center; gap: 20px">
            <el-progress type="circle" :percentage="stat.completionRate || 0" />
            <div>
              <p>总任务：{{ stat.totalPlans }}</p>
              <p>已完成：{{ stat.completedPlans }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>进度概览</span>
      </template>
      <v-chart class="chart" :option="chartOption" autoresize />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getProgressStats } from '../../api/progress'

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent, LegendComponent])

const stats = ref<any[]>([])

const chartOption = computed(() => {
  return {
    tooltip: {},
    xAxis: { type: 'category', data: stats.value.map(s => s.goalTitle) },
    yAxis: { type: 'value', max: 100 },
    series: [{
      data: stats.value.map(s => s.completionRate || 0),
      type: 'bar',
      itemStyle: { color: '#409EFF' }
    }]
  }
})

onMounted(async () => {
  const res: any = await getProgressStats()
  stats.value = res.data || []
})
</script>

<style scoped>
.chart {
  height: 300px;
}
</style>

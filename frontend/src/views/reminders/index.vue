<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center">
      <h2>学习提醒</h2>
      <el-button type="primary" @click="openDialog">新增提醒</el-button>
    </div>

    <el-card style="margin-top: 20px">
      <el-empty v-if="reminders.length === 0" description="暂无提醒" />
      <el-timeline v-else>
        <el-timeline-item v-for="r in reminders" :key="r.id" :type="r.isSent ? 'success' : 'primary'">
          <el-card>
            <div style="display: flex; justify-content: space-between">
              <div>
                <p><strong>{{ r.content }}</strong></p>
                <p style="color: #909399; font-size: 12px">提醒时间：{{ r.remindTime }}</p>
                <p style="color: #909399; font-size: 12px">类型：{{ r.remindType === 1 ? '邮件' : '短信' }}</p>
              </div>
              <div>
                <el-tag :type="r.isSent ? 'success' : 'info'">{{ r.isSent ? '已发送' : '待发送' }}</el-tag>
                <el-button type="danger" size="small" @click="handleDelete(r.id)" style="margin-left: 10px">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增提醒" width="500px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="学习目标">
          <el-select v-model="selectedGoalId" placeholder="选择学习目标" style="width: 100%" @change="onGoalChange">
            <el-option v-for="goal in goals" :key="goal.id" :label="goal.title" :value="goal.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学习计划">
          <el-select v-model="form.planId" placeholder="选择学习计划" style="width: 100%" :disabled="!selectedGoalId || plans.length === 0">
            <el-option v-for="plan in plans" :key="plan.id" :label="plan.title + ' (' + plan.planDate + ')'" :value="plan.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="提醒类型">
          <el-radio-group v-model="form.remindType">
            <el-radio :label="1">邮件</el-radio>
            <el-radio :label="2">短信</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提醒时间">
          <el-date-picker v-model="form.remindTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="提醒内容">
          <el-input v-model="form.content" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listReminders, createReminder, deleteReminder } from '../../api/reminder'
import { listGoals } from '../../api/goal'
import { listPlans } from '../../api/plan'

const reminders = ref<any[]>([])
const goals = ref<any[]>([])
const plans = ref<any[]>([])
const selectedGoalId = ref('')
const dialogVisible = ref(false)
const formRef = ref()
const form = ref({
  planId: '',
  remindType: 1,
  remindTime: '',
  content: ''
})

const loadReminders = async () => {
  const res: any = await listReminders()
  reminders.value = res.data || []
}

const loadGoals = async () => {
  const res: any = await listGoals()
  goals.value = res.data || []
}

const openDialog = () => {
  selectedGoalId.value = ''
  plans.value = []
  form.value = { planId: '', remindType: 1, remindTime: '', content: '' }
  loadGoals()
  dialogVisible.value = true
}

const onGoalChange = async (goalId: string) => {
  form.value.planId = ''
  if (!goalId) {
    plans.value = []
    return
  }
  const res: any = await listPlans(goalId)
  plans.value = res.data || []
}

const submitForm = async () => {
  if (!form.value.planId) {
    ElMessage.error('请选择学习计划')
    return
  }
  if (!form.value.remindTime) {
    ElMessage.error('请选择提醒时间')
    return
  }
  await createReminder({
    planId: form.value.planId,
    remindType: form.value.remindType,
    remindTime: form.value.remindTime,
    content: form.value.content
  })
  ElMessage.success('创建成功')
  dialogVisible.value = false
  form.value = { planId: '', remindType: 1, remindTime: '', content: '' }
  selectedGoalId.value = ''
  plans.value = []
  loadReminders()
}

const handleDelete = async (id: string) => {
  await ElMessageBox.confirm('确定删除该提醒？', '提示', { type: 'warning' })
  await deleteReminder(id)
  ElMessage.success('删除成功')
  loadReminders()
}

onMounted(loadReminders)
</script>

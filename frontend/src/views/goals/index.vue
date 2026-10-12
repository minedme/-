<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center">
      <h2>学习目标</h2>
      <el-button type="primary" @click="dialogVisible = true">新增目标</el-button>
    </div>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8" v-for="goal in goals" :key="goal.id" style="margin-bottom: 20px">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>{{ goal.title }}</span>
              <el-dropdown @command="(cmd: string) => handleCommand(cmd, goal)">
                <el-icon><More /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="view">查看计划</el-dropdown-item>
                    <el-dropdown-item command="generate">生成计划</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
          <div>
            <p>类别：{{ goal.category }}</p>
            <p>时间：{{ goal.startDate }} 至 {{ goal.endDate }}</p>
            <p>每日时长：{{ goal.dailyHours }} 小时</p>
            <el-progress :percentage="goal.progressRate || 0" style="margin-top: 10px" />
            <p style="margin-top: 5px; color: #909399; font-size: 12px">
              {{ goal.completedPlans || 0 }} / {{ goal.totalPlans || 0 }} 完成
            </p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="新增学习目标" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="目标名称" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-input v-model="form.category" placeholder="如：英语、编程、数学" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="描述学习内容，将用于生成计划" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每日时长" prop="dailyHours">
          <el-input-number v-model="form.dailyHours" :min="0.5" :max="12" :step="0.5" style="width: 100%" />
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listGoals, createGoal, deleteGoal, generatePlan } from '../../api/goal'

const router = useRouter()
const goals = ref<any[]>([])
const dialogVisible = ref(false)
const formRef = ref()
const form = ref({
  title: '',
  category: '',
  description: '',
  startDate: '',
  endDate: '',
  dailyHours: 1
})

const formRules = {
  title: [{ required: true, message: '请输入目标名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入类别', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  dailyHours: [{ required: true, message: '请输入每日时长', trigger: 'blur' }]
}

const loadGoals = async () => {
  const res: any = await listGoals()
  goals.value = res.data || []
}

const submitForm = async () => {
  await formRef.value.validate()
  await createGoal(form.value)
  ElMessage.success('创建成功')
  dialogVisible.value = false
  form.value = { title: '', category: '', description: '', startDate: '', endDate: '', dailyHours: 1 }
  loadGoals()
}

const handleCommand = async (cmd: string, goal: any) => {
  if (cmd === 'view') {
    router.push('/goals/' + goal.id + '/plans')
  } else if (cmd === 'generate') {
    try {
      await generatePlan(goal.id)
      ElMessage.success('计划生成成功')
      loadGoals()
    } catch (e) {
      // 错误已在 request 拦截器中提示
    }
  } else if (cmd === 'delete') {
    await ElMessageBox.confirm('确定删除该目标？', '提示', { type: 'warning' })
    await deleteGoal(goal.id)
    ElMessage.success('删除成功')
    loadGoals()
  }
}

onMounted(loadGoals)
</script>

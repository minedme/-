<template>
  <div>
    <h2>资源推荐</h2>
    <el-card style="margin-top: 20px">
      <div style="display: flex; gap: 10px; margin-bottom: 20px">
        <el-input v-model="searchKeyword" placeholder="输入关键词搜索" style="width: 200px" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="searchKeyword = ''; handleSearch()">重置</el-button>
      </div>

      <el-empty v-if="resources.length === 0" description="暂无资源" />
      <el-row :gutter="20" v-else>
        <el-col :span="6" v-for="r in resources" :key="r.id" style="margin-bottom: 20px">
          <el-card :body-style="{ padding: '0px' }">
            <div style="padding: 14px">
              <div style="display: flex; justify-content: space-between; align-items: center">
                <span style="font-weight: bold">{{ r.title }}</span>
                <el-tag size="small">{{ r.typeName }}</el-tag>
              </div>
              <div style="margin-top: 10px; color: #909399; font-size: 12px">
                <p>分类：{{ r.category }}</p>
                <p>{{ r.description }}</p>
                <p>评分：{{ r.rating }}</p>
              </div>
              <div style="margin-top: 10px">
                <el-button type="primary" size="small" @click="openUrl(r.url)">查看资源</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listResources } from '../../api/resource'

const resources = ref<any[]>([])
const searchKeyword = ref('')

const loadResources = async (keyword?: string) => {
  const res: any = await listResources(keyword)
  resources.value = res.data || []
}

const handleSearch = () => {
  loadResources(searchKeyword.value || undefined)
}

const openUrl = (url: string) => {
  window.open(url, '_blank')
}

onMounted(() => loadResources())
</script>

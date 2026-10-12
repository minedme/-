import request from '../utils/request'

export const createGoal = (data: any) => {
  return request.post('/goals', data)
}

export const listGoals = () => {
  return request.get('/goals')
}

export const getGoal = (id: string) => {
  return request.get('/goals/' + id)
}

export const deleteGoal = (id: string) => {
  return request.delete('/goals/' + id)
}

export const generatePlan = (id: string) => {
  return request.post('/goals/' + id + '/generate')
}

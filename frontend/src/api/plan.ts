import request from '../utils/request'

export const listPlans = (goalId: string) => {
  return request.get('/plans/goal/' + goalId)
}

export const updatePlanStatus = (id: string, status: number) => {
  return request.put('/plans/' + id + '/status?status=' + status)
}

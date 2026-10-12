import request from '../utils/request'

export const getProgressStats = () => {
  return request.get('/progress/stats')
}

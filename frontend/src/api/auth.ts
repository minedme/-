import request from '../utils/request'

export const register = (data: any) => {
  return request.post('/auth/register', data)
}

export const login = (data: any) => {
  return request.post('/auth/login', data)
}

export const getProfile = () => {
  return request.get('/user/profile')
}

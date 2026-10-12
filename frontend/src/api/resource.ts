import request from '../utils/request'

export const listResources = (keyword?: string, type?: number) => {
  let url = '/resources?'
  if (keyword) url += 'keyword=' + encodeURIComponent(keyword) + '&'
  if (type !== undefined) url += 'type=' + type
  return request.get(url)
}

export const recommendResources = (category: string) => {
  return request.get('/resources/recommend?category=' + category)
}

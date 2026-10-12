import request from '../utils/request'

export const createReminder = (data: any) => {
  return request.post('/reminders', data)
}

export const listReminders = () => {
  return request.get('/reminders')
}

export const deleteReminder = (id: string) => {
  return request.delete('/reminders/' + id)
}

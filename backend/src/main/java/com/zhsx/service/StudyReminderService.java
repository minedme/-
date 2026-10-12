package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.common.BusinessException;
import com.zhsx.dto.ReminderCreateDTO;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import com.zhsx.entity.StudyReminder;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyReminderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyReminderService {

    private final StudyReminderMapper reminderMapper;
    private final StudyPlanMapper planMapper;
    private final StudyGoalMapper goalMapper;

    public void createReminder(Long userId, ReminderCreateDTO dto) {
        StudyPlan plan = planMapper.selectById(dto.getPlanId());
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        // 归属校验：防止给他人计划创建提醒
        StudyGoal goal = goalMapper.selectById(plan.getGoalId());
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException("计划不存在或无权访问");
        }
        StudyReminder reminder = new StudyReminder();
        reminder.setUserId(userId);
        reminder.setPlanId(dto.getPlanId());
        reminder.setRemindType(dto.getRemindType());
        reminder.setRemindTime(dto.getRemindTime());
        reminder.setContent(dto.getContent());
        reminder.setIsSent(0);
        reminderMapper.insert(reminder);
    }

    public List<StudyReminder> listReminders(Long userId) {
        LambdaQueryWrapper<StudyReminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyReminder::getUserId, userId).orderByDesc(StudyReminder::getRemindTime);
        return reminderMapper.selectList(wrapper);
    }

    public void deleteReminder(Long userId, Long reminderId) {
        StudyReminder reminder = reminderMapper.selectById(reminderId);
        if (reminder == null || !reminder.getUserId().equals(userId)) {
            throw new BusinessException("提醒不存在");
        }
        reminderMapper.deleteById(reminderId);
    }

    public List<StudyReminder> getPendingReminders() {
        return reminderMapper.selectPendingReminders(LocalDateTime.now());
    }

    public void markSent(Long reminderId) {
        StudyReminder reminder = new StudyReminder();
        reminder.setId(reminderId);
        reminder.setIsSent(1);
        reminderMapper.updateById(reminder);
    }
}

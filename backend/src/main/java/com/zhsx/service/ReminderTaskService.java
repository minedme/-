package com.zhsx.service;

import com.zhsx.entity.StudyReminder;
import com.zhsx.entity.User;
import com.zhsx.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderTaskService {

    private final StudyReminderService reminderService;
    private final MailService mailService;
    private final UserMapper userMapper;

    @Scheduled(fixedRate = 60000)
    public void checkAndSendReminders() {
        List<StudyReminder> pending = reminderService.getPendingReminders();
        for (StudyReminder reminder : pending) {
            User user = userMapper.selectById(reminder.getUserId());
            if (user != null && user.getEmail() != null) {
                String subject = "学习提醒：" + reminder.getContent();
                String text = "您好，\n\n您的学习任务提醒：\n" + reminder.getContent() + "\n\n请及时完成学习计划。\n\n智能学习计划助手";
                mailService.sendReminder(user.getEmail(), subject, text);
                reminderService.markSent(reminder.getId());
            }
        }
    }
}

package com.zhsx.service;

import com.zhsx.entity.StudyReminder;
import com.zhsx.entity.User;
import com.zhsx.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        if (pending.isEmpty()) {
            return;
        }

        // 批量查询用户，消除 N+1
        List<Long> userIds = pending.stream()
                .map(StudyReminder::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        for (StudyReminder reminder : pending) {
            User user = userMap.get(reminder.getUserId());
            if (user == null || !StringUtils.hasText(user.getEmail())) {
                // 用户不存在或无邮箱：永远无法投递，标记终止，避免每分钟重复扫描
                log.warn("提醒 {} 的用户不存在或未配置邮箱，跳过发送", reminder.getId());
                reminderService.markSent(reminder.getId());
                continue;
            }
            String subject = "学习提醒：" + reminder.getContent();
            String text = "您好，\n\n您的学习任务提醒：\n" + reminder.getContent()
                    + "\n\n请及时完成学习计划。\n\n智能学习计划助手";
            boolean sent = mailService.sendReminder(user.getEmail(), subject, text);
            // 仅投递成功才标记，失败的下轮重试
            if (sent) {
                reminderService.markSent(reminder.getId());
            } else {
                log.warn("提醒 {} 发送失败，等待下轮重试", reminder.getId());
            }
        }
    }
}

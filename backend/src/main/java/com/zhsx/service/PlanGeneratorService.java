package com.zhsx.service;

import com.zhsx.common.BusinessException;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlanGeneratorService {

    /** 单个目标最多生成的计划天数，防止超长周期一次性写入海量数据 */
    private static final long MAX_PLAN_DAYS = 366;

    public List<StudyPlan> generatePlan(StudyGoal goal) {
        List<StudyPlan> plans = new ArrayList<>();
        LocalDate start = goal.getStartDate();
        LocalDate end = goal.getEndDate();
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        if (days <= 0) {
            return plans;
        }
        if (days > MAX_PLAN_DAYS) {
            throw new BusinessException("学习周期过长，单目标最多支持 " + MAX_PLAN_DAYS + " 天");
        }

        String desc = goal.getDescription() != null ? goal.getDescription() : goal.getTitle();
        String[] sections = splitContent(desc, (int) days);

        int dailyMinutes = goal.getDailyHours().multiply(new java.math.BigDecimal("60")).intValue();

        for (int i = 0; i < days; i++) {
            StudyPlan plan = new StudyPlan();
            plan.setGoalId(goal.getId());
            plan.setPlanDate(start.plusDays(i));
            plan.setTitle("第" + (i + 1) + "天：" + goal.getTitle());
            plan.setContent(sections[i]);
            plan.setDuration(dailyMinutes);
            plan.setStatus(0);
            plan.setSortOrder(i);
            plans.add(plan);
        }
        return plans;
    }

    private String[] splitContent(String content, int days) {
        String[] paragraphs = content.split("[\n。；;]+");
        String[] result = new String[days];
        int avg = Math.max(1, paragraphs.length / days);

        for (int i = 0; i < days; i++) {
            StringBuilder sb = new StringBuilder();
            int start = i * avg;
            int end = (i == days - 1) ? paragraphs.length : Math.min(start + avg, paragraphs.length);
            for (int j = start; j < end; j++) {
                if (!paragraphs[j].trim().isEmpty()) {
                    sb.append(paragraphs[j].trim()).append("；");
                }
            }
            if (sb.length() == 0) {
                sb.append("继续学习相关内容");
            }
            result[i] = sb.toString();
        }
        return result;
    }
}

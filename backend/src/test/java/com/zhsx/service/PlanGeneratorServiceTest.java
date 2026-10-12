package com.zhsx.service;

import com.zhsx.common.BusinessException;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PlanGeneratorService 纯逻辑单元测试
 */
class PlanGeneratorServiceTest {

    private final PlanGeneratorService service = new PlanGeneratorService();

    private StudyGoal buildGoal(LocalDate start, LocalDate end) {
        StudyGoal goal = new StudyGoal();
        goal.setTitle("Java 学习");
        goal.setDescription("基础语法。集合框架；泛型与反射");
        goal.setDailyHours(new BigDecimal("2"));
        goal.setStartDate(start);
        goal.setEndDate(end);
        return goal;
    }

    @Test
    void generatePlan_normalRange_createsOnePlanPerDay() {
        List<StudyPlan> plans = service.generatePlan(
                buildGoal(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 3)));

        assertEquals(3, plans.size());
        assertEquals(LocalDate.of(2026, 1, 1), plans.get(0).getPlanDate());
        assertEquals(LocalDate.of(2026, 1, 3), plans.get(2).getPlanDate());
        // dailyHours=2 → 120 分钟
        assertEquals(120, plans.get(0).getDuration());
        // sortOrder 递增
        assertEquals(0, plans.get(0).getSortOrder());
        assertEquals(2, plans.get(2).getSortOrder());
        // 每天都有非空内容
        plans.forEach(p -> assertFalse(p.getContent().isBlank()));
    }

    @Test
    void generatePlan_startAfterEnd_returnsEmpty() {
        List<StudyPlan> plans = service.generatePlan(
                buildGoal(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 2, 1)));
        assertTrue(plans.isEmpty());
    }

    @Test
    void generatePlan_overOneYear_throwsBusinessException() {
        // 370 天，超过 366 上限
        StudyGoal goal = buildGoal(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 5));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.generatePlan(goal));
        assertTrue(ex.getMessage().contains("366"));
    }

    @Test
    void generatePlan_exactlyOneYear_succeeds() {
        // 365 天，恰好在上限内
        List<StudyPlan> plans = service.generatePlan(
                buildGoal(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));
        assertEquals(365, plans.size());
    }

    @Test
    void generatePlan_descriptionMissing_fallsBackToTitle() {
        StudyGoal goal = buildGoal(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2));
        goal.setDescription(null);
        List<StudyPlan> plans = service.generatePlan(goal);
        assertEquals(2, plans.size());
        plans.forEach(p -> assertFalse(p.getContent().isBlank()));
    }
}

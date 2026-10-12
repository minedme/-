package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.common.BusinessException;
import com.zhsx.dto.GoalCreateDTO;
import com.zhsx.dto.PlanStatDTO;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyProgressMapper;
import com.zhsx.mapper.StudyReminderMapper;
import com.zhsx.vo.GoalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGoalService {

    private final StudyGoalMapper goalMapper;
    private final StudyPlanMapper planMapper;
    private final StudyProgressMapper progressMapper;
    private final StudyReminderMapper reminderMapper;
    private final PlanGeneratorService planGeneratorService;

    @Transactional
    public void createGoal(Long userId, GoalCreateDTO dto) {
        StudyGoal goal = new StudyGoal();
        BeanUtils.copyProperties(dto, goal);
        goal.setUserId(userId);
        goal.setStatus(1);
        goalMapper.insert(goal);
    }

    public List<GoalVO> listGoals(Long userId) {
        LambdaQueryWrapper<StudyGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyGoal::getUserId, userId).orderByDesc(StudyGoal::getCreateTime);
        List<StudyGoal> goals = goalMapper.selectList(wrapper);

        // 一次聚合查询替代每目标 2 次 COUNT（消除 N+1）
        Map<Long, PlanStatDTO> statMap = batchQueryStats(goals);
        return goals.stream()
                .map(goal -> convertToVO(goal, statMap.get(goal.getId())))
                .collect(Collectors.toList());
    }

    public GoalVO getGoal(Long userId, Long goalId) {
        StudyGoal goal = goalMapper.selectById(goalId);
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException("目标不存在");
        }
        return convertToVO(goal, querySingleStat(goalId));
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        StudyGoal goal = goalMapper.selectById(goalId);
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException("目标不存在");
        }
        // 关联计划 id 列表，用于清理孤儿进度与提醒
        LambdaQueryWrapper<StudyPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(StudyPlan::getGoalId, goalId);
        List<Long> planIds = planMapper.selectList(planWrapper).stream()
                .map(StudyPlan::getId)
                .collect(Collectors.toList());

        goalMapper.deleteById(goalId);
        planMapper.delete(planWrapper);
        if (!planIds.isEmpty()) {
            progressMapper.delete(new LambdaQueryWrapper<com.zhsx.entity.StudyProgress>()
                    .in(com.zhsx.entity.StudyProgress::getPlanId, planIds));
            reminderMapper.delete(new LambdaQueryWrapper<com.zhsx.entity.StudyReminder>()
                    .in(com.zhsx.entity.StudyReminder::getPlanId, planIds));
        }
    }

    @Transactional
    public void generatePlan(Long userId, Long goalId) {
        StudyGoal goal = goalMapper.selectById(goalId);
        if (goal == null) {
            throw new BusinessException("目标不存在");
        }
        if (!goal.getUserId().equals(userId)) {
            throw new BusinessException("目标不存在或无权访问");
        }
        LambdaQueryWrapper<StudyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyPlan::getGoalId, goalId);
        planMapper.delete(wrapper);

        List<StudyPlan> plans = planGeneratorService.generatePlan(goal);
        for (StudyPlan plan : plans) {
            planMapper.insert(plan);
        }
    }

    private Map<Long, PlanStatDTO> batchQueryStats(List<StudyGoal> goals) {
        if (goals.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> goalIds = goals.stream().map(StudyGoal::getId).collect(Collectors.toList());
        return planMapper.selectStatsByGoalIds(goalIds).stream()
                .collect(Collectors.toMap(PlanStatDTO::getGoalId, Function.identity()));
    }

    private PlanStatDTO querySingleStat(Long goalId) {
        return planMapper.selectStatsByGoalIds(Collections.singletonList(goalId))
                .stream().findFirst().orElse(null);
    }

    private GoalVO convertToVO(StudyGoal goal, PlanStatDTO stat) {
        GoalVO vo = new GoalVO();
        BeanUtils.copyProperties(goal, vo);

        int total = stat != null && stat.getTotal() != null ? stat.getTotal().intValue() : 0;
        int completed = stat != null && stat.getCompleted() != null ? stat.getCompleted().intValue() : 0;
        vo.setTotalPlans(total);
        vo.setCompletedPlans(completed);

        if (total > 0) {
            BigDecimal rate = BigDecimal.valueOf(completed)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            vo.setProgressRate(rate);
        } else {
            vo.setProgressRate(BigDecimal.ZERO);
        }
        return vo;
    }
}

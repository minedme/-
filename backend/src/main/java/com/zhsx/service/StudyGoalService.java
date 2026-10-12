package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.common.BusinessException;
import com.zhsx.dto.GoalCreateDTO;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyProgressMapper;
import com.zhsx.vo.GoalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGoalService {

    private final StudyGoalMapper goalMapper;
    private final StudyPlanMapper planMapper;
    private final StudyProgressMapper progressMapper;
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
        return goals.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public GoalVO getGoal(Long userId, Long goalId) {
        StudyGoal goal = goalMapper.selectById(goalId);
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException("目标不存在");
        }
        return convertToVO(goal);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        StudyGoal goal = goalMapper.selectById(goalId);
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException("目标不存在");
        }
        goalMapper.deleteById(goalId);
        LambdaQueryWrapper<StudyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyPlan::getGoalId, goalId);
        planMapper.delete(wrapper);
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

    private GoalVO convertToVO(StudyGoal goal) {
        GoalVO vo = new GoalVO();
        BeanUtils.copyProperties(goal, vo);

        Long total = progressMapper.selectTotalCountByGoalId(goal.getId());
        Long completed = progressMapper.selectCompletedCountByGoalId(goal.getId());
        vo.setTotalPlans(total != null ? total.intValue() : 0);
        vo.setCompletedPlans(completed != null ? completed.intValue() : 0);

        if (vo.getTotalPlans() > 0) {
            BigDecimal rate = BigDecimal.valueOf(vo.getCompletedPlans())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(vo.getTotalPlans()), 2, RoundingMode.HALF_UP);
            vo.setProgressRate(rate);
        } else {
            vo.setProgressRate(BigDecimal.ZERO);
        }
        return vo;
    }
}

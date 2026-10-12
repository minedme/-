package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.common.BusinessException;
import com.zhsx.entity.StudyPlan;
import com.zhsx.entity.StudyProgress;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyProgressMapper;
import com.zhsx.vo.PlanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyPlanService {

    private final StudyPlanMapper planMapper;
    private final StudyProgressMapper progressMapper;

    public List<PlanVO> listPlansByGoal(Long userId, Long goalId) {
        List<StudyPlan> plans = planMapper.selectByGoalId(goalId);
        return plans.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Transactional
    public void updatePlanStatus(Long userId, Long planId, Integer status) {
        StudyPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        plan.setStatus(status);
        planMapper.updateById(plan);

        StudyProgress progress = new StudyProgress();
        progress.setPlanId(planId);
        progress.setUserId(userId);
        if (status == 2) {
            progress.setCompletionRate(BigDecimal.valueOf(100));
            progress.setActualDuration(plan.getDuration());
        } else if (status == 1) {
            progress.setCompletionRate(BigDecimal.valueOf(50));
        } else {
            progress.setCompletionRate(BigDecimal.ZERO);
        }

        LambdaQueryWrapper<StudyProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyProgress::getPlanId, planId);
        if (progressMapper.selectCount(wrapper) > 0) {
            progressMapper.update(progress, wrapper);
        } else {
            progressMapper.insert(progress);
        }
    }

    private PlanVO convertToVO(StudyPlan plan) {
        PlanVO vo = new PlanVO();
        BeanUtils.copyProperties(plan, vo);
        return vo;
    }
}

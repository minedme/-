package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.entity.StudyGoal;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyProgressMapper;
import com.zhsx.vo.ProgressStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyProgressService {

    private final StudyProgressMapper progressMapper;
    private final StudyGoalMapper goalMapper;

    public List<ProgressStatsVO> getStats(Long userId) {
        LambdaQueryWrapper<StudyGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyGoal::getUserId, userId).orderByDesc(StudyGoal::getCreateTime);
        List<StudyGoal> goals = goalMapper.selectList(wrapper);

        return goals.stream().map(goal -> {
            ProgressStatsVO vo = new ProgressStatsVO();
            vo.setGoalId(goal.getId());
            vo.setGoalTitle(goal.getTitle());

            Long total = progressMapper.selectTotalCountByGoalId(goal.getId());
            Long completed = progressMapper.selectCompletedCountByGoalId(goal.getId());
            vo.setTotalPlans(total != null ? total : 0);
            vo.setCompletedPlans(completed != null ? completed : 0);

            if (vo.getTotalPlans() > 0) {
                BigDecimal rate = BigDecimal.valueOf(vo.getCompletedPlans())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(vo.getTotalPlans()), 2, RoundingMode.HALF_UP);
                vo.setCompletionRate(rate);
            } else {
                vo.setCompletionRate(BigDecimal.ZERO);
            }
            return vo;
        }).collect(Collectors.toList());
    }
}

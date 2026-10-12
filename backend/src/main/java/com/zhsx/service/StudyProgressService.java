package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.dto.PlanStatDTO;
import com.zhsx.entity.StudyGoal;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyProgressMapper;
import com.zhsx.vo.ProgressStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyProgressService {

    private final StudyProgressMapper progressMapper;
    private final StudyGoalMapper goalMapper;
    private final StudyPlanMapper planMapper;

    public List<ProgressStatsVO> getStats(Long userId) {
        LambdaQueryWrapper<StudyGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyGoal::getUserId, userId).orderByDesc(StudyGoal::getCreateTime);
        List<StudyGoal> goals = goalMapper.selectList(wrapper);

        // 一次聚合查询替代每目标 2 次 COUNT（消除 N+1）
        Map<Long, PlanStatDTO> statMap = goals.isEmpty() ? Map.of()
                : planMapper.selectStatsByGoalIds(
                        goals.stream().map(StudyGoal::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(PlanStatDTO::getGoalId, Function.identity()));

        return goals.stream().map(goal -> {
            ProgressStatsVO vo = new ProgressStatsVO();
            vo.setGoalId(goal.getId());
            vo.setGoalTitle(goal.getTitle());

            PlanStatDTO stat = statMap.get(goal.getId());
            long total = stat != null && stat.getTotal() != null ? stat.getTotal() : 0L;
            long completed = stat != null && stat.getCompleted() != null ? stat.getCompleted() : 0L;
            vo.setTotalPlans(total);
            vo.setCompletedPlans(completed);

            if (total > 0) {
                BigDecimal rate = BigDecimal.valueOf(completed)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
                vo.setCompletionRate(rate);
            } else {
                vo.setCompletionRate(BigDecimal.ZERO);
            }
            return vo;
        }).collect(Collectors.toList());
    }
}

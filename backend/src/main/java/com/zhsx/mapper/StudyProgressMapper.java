package com.zhsx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhsx.entity.StudyProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface StudyProgressMapper extends BaseMapper<StudyProgress> {

    @Select("SELECT AVG(completion_rate) FROM study_progress sp " +
            "JOIN study_plan p ON sp.plan_id = p.id " +
            "WHERE p.goal_id = #{goalId}")
    BigDecimal selectAvgRateByGoalId(@Param("goalId") Long goalId);

    @Select("SELECT COUNT(*) FROM study_plan WHERE goal_id = #{goalId} AND status = 2 AND deleted = 0")
    Long selectCompletedCountByGoalId(@Param("goalId") Long goalId);

    @Select("SELECT COUNT(*) FROM study_plan WHERE goal_id = #{goalId} AND deleted = 0")
    Long selectTotalCountByGoalId(@Param("goalId") Long goalId);
}

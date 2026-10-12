package com.zhsx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhsx.dto.PlanStatDTO;
import com.zhsx.entity.StudyPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface StudyPlanMapper extends BaseMapper<StudyPlan> {

    @Select("SELECT * FROM study_plan WHERE goal_id = #{goalId} AND deleted = 0 ORDER BY plan_date, sort_order")
    List<StudyPlan> selectByGoalId(Long goalId);

    /**
     * 按目标批量聚合计划总数与完成数，替代循环 COUNT（消除 N+1）
     */
    @Select("<script>SELECT goal_id AS goalId, COUNT(*) AS total, " +
            "SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS completed " +
            "FROM study_plan WHERE deleted = 0 AND goal_id IN " +
            "<foreach collection='goalIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "GROUP BY goal_id</script>")
    List<PlanStatDTO> selectStatsByGoalIds(@Param("goalIds") Collection<Long> goalIds);
}

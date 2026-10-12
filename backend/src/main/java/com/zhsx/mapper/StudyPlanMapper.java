package com.zhsx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhsx.entity.StudyPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyPlanMapper extends BaseMapper<StudyPlan> {

    @Select("SELECT * FROM study_plan WHERE goal_id = #{goalId} AND deleted = 0 ORDER BY plan_date, sort_order")
    List<StudyPlan> selectByGoalId(Long goalId);
}

package com.zhsx.service;

import com.zhsx.common.BusinessException;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import com.zhsx.entity.StudyProgress;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyProgressMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StudyPlanService 越权防护与状态校验单元测试
 */
@ExtendWith(MockitoExtension.class)
class StudyPlanServiceTest {

    private static final Long USER_A = 1L;
    private static final Long USER_B = 2L;
    private static final Long GOAL_ID = 10L;
    private static final Long PLAN_ID = 100L;

    @Mock
    private StudyPlanMapper planMapper;
    @Mock
    private StudyProgressMapper progressMapper;
    @Mock
    private StudyGoalMapper goalMapper;

    @InjectMocks
    private StudyPlanService service;

    private StudyGoal goalOf(Long userId) {
        StudyGoal goal = new StudyGoal();
        goal.setId(GOAL_ID);
        goal.setUserId(userId);
        return goal;
    }

    private StudyPlan planOf(Long goalId) {
        StudyPlan plan = new StudyPlan();
        plan.setId(PLAN_ID);
        plan.setGoalId(goalId);
        plan.setStatus(0);
        plan.setDuration(60);
        return plan;
    }

    @Test
    void listPlansByGoal_foreignGoal_throws() {
        when(goalMapper.selectById(GOAL_ID)).thenReturn(goalOf(USER_B));

        assertThrows(BusinessException.class,
                () -> service.listPlansByGoal(USER_A, GOAL_ID));
        // 不应继续查询计划列表
        verify(planMapper, never()).selectByGoalId(any());
    }

    @Test
    void listPlansByGoal_ownGoal_returnsPlans() {
        when(goalMapper.selectById(GOAL_ID)).thenReturn(goalOf(USER_A));
        when(planMapper.selectByGoalId(GOAL_ID)).thenReturn(java.util.List.of(planOf(GOAL_ID)));

        assertEquals(1, service.listPlansByGoal(USER_A, GOAL_ID).size());
    }

    @Test
    void updatePlanStatus_invalidStatus_throws() {
        assertThrows(BusinessException.class,
                () -> service.updatePlanStatus(USER_A, PLAN_ID, 99));
        assertThrows(BusinessException.class,
                () -> service.updatePlanStatus(USER_A, PLAN_ID, null));
        verify(planMapper, never()).updateById(any());
    }

    @Test
    void updatePlanStatus_planOfOtherUser_throws() {
        when(planMapper.selectById(PLAN_ID)).thenReturn(planOf(GOAL_ID));
        when(goalMapper.selectById(GOAL_ID)).thenReturn(goalOf(USER_B));

        assertThrows(BusinessException.class,
                () -> service.updatePlanStatus(USER_A, PLAN_ID, 2));
        verify(planMapper, never()).updateById(any());
    }

    @Test
    void updatePlanStatus_ownPlanCompleted_createsProgress() {
        when(planMapper.selectById(PLAN_ID)).thenReturn(planOf(GOAL_ID));
        when(goalMapper.selectById(GOAL_ID)).thenReturn(goalOf(USER_A));
        when(progressMapper.selectCount(any())).thenReturn(0L);

        assertDoesNotThrow(() -> service.updatePlanStatus(USER_A, PLAN_ID, 2));

        verify(planMapper).updateById(any(StudyPlan.class));
        verify(progressMapper).insert(any(StudyProgress.class));
    }
}

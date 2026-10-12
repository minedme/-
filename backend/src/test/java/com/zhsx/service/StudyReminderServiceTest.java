package com.zhsx.service;

import com.zhsx.common.BusinessException;
import com.zhsx.dto.ReminderCreateDTO;
import com.zhsx.entity.StudyGoal;
import com.zhsx.entity.StudyPlan;
import com.zhsx.entity.StudyReminder;
import com.zhsx.mapper.StudyGoalMapper;
import com.zhsx.mapper.StudyPlanMapper;
import com.zhsx.mapper.StudyReminderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StudyReminderService 越权防护单元测试
 */
@ExtendWith(MockitoExtension.class)
class StudyReminderServiceTest {

    private static final Long USER_A = 1L;
    private static final Long USER_B = 2L;
    private static final Long GOAL_ID = 10L;
    private static final Long PLAN_ID = 100L;

    @Mock
    private StudyReminderMapper reminderMapper;
    @Mock
    private StudyPlanMapper planMapper;
    @Mock
    private StudyGoalMapper goalMapper;

    @InjectMocks
    private StudyReminderService service;

    private ReminderCreateDTO dto() {
        ReminderCreateDTO dto = new ReminderCreateDTO();
        dto.setPlanId(PLAN_ID);
        return dto;
    }

    @Test
    void createReminder_planOfOtherUser_throws() {
        StudyPlan plan = new StudyPlan();
        plan.setId(PLAN_ID);
        plan.setGoalId(GOAL_ID);
        when(planMapper.selectById(PLAN_ID)).thenReturn(plan);

        StudyGoal foreignGoal = new StudyGoal();
        foreignGoal.setId(GOAL_ID);
        foreignGoal.setUserId(USER_B);
        when(goalMapper.selectById(GOAL_ID)).thenReturn(foreignGoal);

        assertThrows(BusinessException.class, () -> service.createReminder(USER_A, dto()));
        verify(reminderMapper, never()).insert(any(StudyReminder.class));
    }

    @Test
    void createReminder_ownPlan_insertsReminder() {
        StudyPlan plan = new StudyPlan();
        plan.setId(PLAN_ID);
        plan.setGoalId(GOAL_ID);
        when(planMapper.selectById(PLAN_ID)).thenReturn(plan);

        StudyGoal ownGoal = new StudyGoal();
        ownGoal.setId(GOAL_ID);
        ownGoal.setUserId(USER_A);
        when(goalMapper.selectById(GOAL_ID)).thenReturn(ownGoal);

        assertDoesNotThrow(() -> service.createReminder(USER_A, dto()));

        ArgumentCaptor<StudyReminder> captor = ArgumentCaptor.forClass(StudyReminder.class);
        verify(reminderMapper).insert(captor.capture());
        assertEquals(USER_A, captor.getValue().getUserId());
        assertEquals(0, captor.getValue().getIsSent());
    }
}

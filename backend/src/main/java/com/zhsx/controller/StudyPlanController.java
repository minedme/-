package com.zhsx.controller;

import com.zhsx.common.Result;
import com.zhsx.security.SecurityUtils;
import com.zhsx.service.StudyPlanService;
import com.zhsx.vo.PlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学习计划管理")
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanService planService;

    @Operation(summary = "获取目标下的计划列表")
    @GetMapping("/goal/{goalId}")
    public Result<List<PlanVO>> listPlans(@PathVariable Long goalId) {
        return Result.success(planService.listPlansByGoal(SecurityUtils.getCurrentUserId(), goalId));
    }

    @Operation(summary = "更新计划状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        planService.updatePlanStatus(SecurityUtils.getCurrentUserId(), id, status);
        return Result.success();
    }
}

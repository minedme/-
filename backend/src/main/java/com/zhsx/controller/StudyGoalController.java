package com.zhsx.controller;

import com.zhsx.common.Result;
import com.zhsx.dto.GoalCreateDTO;
import com.zhsx.security.SecurityUtils;
import com.zhsx.service.StudyGoalService;
import com.zhsx.vo.GoalVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学习目标管理")
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class StudyGoalController {

    private final StudyGoalService goalService;

    @Operation(summary = "创建学习目标")
    @PostMapping
    public Result<Void> createGoal(@Valid @RequestBody GoalCreateDTO dto) {
        goalService.createGoal(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "获取目标列表")
    @GetMapping
    public Result<List<GoalVO>> listGoals() {
        return Result.success(goalService.listGoals(SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "获取目标详情")
    @GetMapping("/{id}")
    public Result<GoalVO> getGoal(@PathVariable Long id) {
        return Result.success(goalService.getGoal(SecurityUtils.getCurrentUserId(), id));
    }

    @Operation(summary = "删除目标")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "生成学习计划")
    @PostMapping("/{id}/generate")
    public Result<Void> generatePlan(@PathVariable Long id) {
        goalService.generatePlan(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}

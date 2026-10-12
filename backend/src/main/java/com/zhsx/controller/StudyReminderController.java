package com.zhsx.controller;

import com.zhsx.common.Result;
import com.zhsx.dto.ReminderCreateDTO;
import com.zhsx.entity.StudyReminder;
import com.zhsx.security.SecurityUtils;
import com.zhsx.service.StudyReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学习提醒")
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class StudyReminderController {

    private final StudyReminderService reminderService;

    @Operation(summary = "创建提醒")
    @PostMapping
    public Result<Void> createReminder(@Valid @RequestBody ReminderCreateDTO dto) {
        reminderService.createReminder(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "提醒列表")
    @GetMapping
    public Result<List<StudyReminder>> listReminders() {
        return Result.success(reminderService.listReminders(SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "删除提醒")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}

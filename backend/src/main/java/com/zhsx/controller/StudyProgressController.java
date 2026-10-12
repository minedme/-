package com.zhsx.controller;

import com.zhsx.common.Result;
import com.zhsx.security.SecurityUtils;
import com.zhsx.service.StudyProgressService;
import com.zhsx.vo.ProgressStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "学习进度")
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class StudyProgressController {

    private final StudyProgressService progressService;

    @Operation(summary = "获取学习进度统计")
    @GetMapping("/stats")
    public Result<List<ProgressStatsVO>> getStats() {
        return Result.success(progressService.getStats(SecurityUtils.getCurrentUserId()));
    }
}

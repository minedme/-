package com.zhsx.controller;

import com.zhsx.common.Result;
import com.zhsx.service.StudyResourceService;
import com.zhsx.vo.ResourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "学习资源")
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class StudyResourceController {

    private final StudyResourceService resourceService;

    @Operation(summary = "资源列表")
    @GetMapping
    public Result<List<ResourceVO>> listResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {
        return Result.success(resourceService.listResources(keyword, type));
    }

    @Operation(summary = "根据目标推荐资源")
    @GetMapping("/recommend")
    public Result<List<ResourceVO>> recommend(@RequestParam String category) {
        return Result.success(resourceService.recommendByGoal(category));
    }
}

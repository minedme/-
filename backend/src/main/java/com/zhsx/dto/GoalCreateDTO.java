package com.zhsx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalCreateDTO {
    @NotBlank(message = "目标名称不能为空")
    private String title;

    @NotBlank(message = "类别不能为空")
    private String category;

    private String description;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "每日学习时长不能为空")
    private BigDecimal dailyHours;
}

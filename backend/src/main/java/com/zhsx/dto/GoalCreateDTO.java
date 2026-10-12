package com.zhsx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalCreateDTO {
    @NotBlank(message = "目标名称不能为空")
    @Size(max = 100, message = "目标名称不能超过100字")
    private String title;

    @NotBlank(message = "类别不能为空")
    @Size(max = 50, message = "类别不能超过50字")
    private String category;

    @Size(max = 2000, message = "描述不能超过2000字")
    private String description;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "每日学习时长不能为空")
    private BigDecimal dailyHours;
}

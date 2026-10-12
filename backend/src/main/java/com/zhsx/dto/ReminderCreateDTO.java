package com.zhsx.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderCreateDTO {
    @NotNull(message = "计划ID不能为空")
    private Long planId;

    @NotNull(message = "提醒类型不能为空")
    private Integer remindType;

    @NotNull(message = "提醒时间不能为空")
    private LocalDateTime remindTime;

    private String content;
}

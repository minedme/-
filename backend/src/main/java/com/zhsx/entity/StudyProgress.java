package com.zhsx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("study_progress")
public class StudyProgress {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long planId;
    private Long userId;
    private BigDecimal completionRate;
    private Integer actualDuration;
    private String note;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;
}

package com.zhsx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_reminder")
public class StudyReminder {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long planId;
    private Integer remindType;
    private LocalDateTime remindTime;
    private String content;
    private Integer isSent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}

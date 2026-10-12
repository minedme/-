package com.zhsx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("study_plan")
public class StudyPlan {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long goalId;
    private String title;
    private String content;
    private LocalDate planDate;
    private Integer duration;
    private Integer status;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}

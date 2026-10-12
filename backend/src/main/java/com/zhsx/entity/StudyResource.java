package com.zhsx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("study_resource")
public class StudyResource {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String category;
    private String title;
    private Integer type;
    private String url;
    private String description;
    private String cover;
    private BigDecimal rating;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

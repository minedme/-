package com.zhsx.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlanVO {
    private Long id;
    private Long goalId;
    private String title;
    private String content;
    private LocalDate planDate;
    private Integer duration;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createTime;
}

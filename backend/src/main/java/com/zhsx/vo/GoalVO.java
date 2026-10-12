package com.zhsx.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoalVO {
    private Long id;
    private String title;
    private String category;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal dailyHours;
    private Integer status;
    private LocalDateTime createTime;
    private Integer totalPlans;
    private Integer completedPlans;
    private BigDecimal progressRate;
}

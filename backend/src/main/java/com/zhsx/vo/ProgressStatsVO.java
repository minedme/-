package com.zhsx.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProgressStatsVO {
    private Long goalId;
    private String goalTitle;
    private Long totalPlans;
    private Long completedPlans;
    private BigDecimal completionRate;
    private Integer totalDuration;
    private Integer actualDuration;
}

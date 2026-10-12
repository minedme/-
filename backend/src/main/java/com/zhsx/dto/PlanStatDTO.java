package com.zhsx.dto;

import lombok.Data;

/**
 * 目标下学习计划的完成度聚合统计（用于消除 N+1 查询）
 */
@Data
public class PlanStatDTO {
    private Long goalId;
    private Long total;
    private Long completed;
}

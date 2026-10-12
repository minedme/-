package com.zhsx.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResourceVO {
    private Long id;
    private String category;
    private String title;
    private Integer type;
    private String typeName;
    private String url;
    private String description;
    private String cover;
    private BigDecimal rating;
    private LocalDateTime createTime;
}

package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单状态实体类
 */
@Data
public class TicketStatus {
    private Long id;
    private String name; // 状态名称
    private Integer sortOrder; // 排序序号
    private String description; // 状态描述
    private Integer isSystem; // 是否系统预置：0-否，1-是
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

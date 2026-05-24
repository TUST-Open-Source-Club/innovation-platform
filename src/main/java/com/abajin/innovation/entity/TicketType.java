package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单类型实体类
 */
@Data
public class TicketType {
    private Long id;
    private String name; // 类型名称
    private String description; // 类型描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

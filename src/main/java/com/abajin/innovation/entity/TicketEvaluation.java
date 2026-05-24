package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单评价实体类
 */
@Data
public class TicketEvaluation {
    private Long id;
    private Long ticketId; // 工单ID
    private String satisfaction; // 满意度：SATISFIED/DISSATISFIED
    private String content; // 评价内容
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

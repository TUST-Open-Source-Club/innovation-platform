package com.abajin.innovation.dto;

import lombok.Data;

/**
 * 工单查询DTO
 */
@Data
public class TicketQueryDTO {
    private Integer pageNum = 1; // 页码
    private Integer pageSize = 10; // 每页大小
    private Long typeId; // 工单类型ID
    private Long statusId; // 工单状态ID
    private String urgency; // 紧急程度
    private String keyword; // 关键词（标题和内容模糊搜索）
    private Long creatorId; // 创建者ID（用于查询我的工单）
    private Long collegeId; // 学院ID（用于学院管理员查询）
}

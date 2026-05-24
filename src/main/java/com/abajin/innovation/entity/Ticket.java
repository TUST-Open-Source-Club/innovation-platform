package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单实体类
 */
@Data
public class Ticket {
    private Long id;
    private String title; // 工单标题
    private String content; // 工单内容（支持Markdown+LaTeX富文本）
    private Long typeId; // 工单类型ID
    private String typeName; // 类型名称（关联查询）
    private Long statusId; // 工单状态ID
    private String statusName; // 状态名称（关联查询）
    private String urgency; // 紧急程度：LOW/MEDIUM/HIGH/URGENT
    private String images; // 图片URL列表（JSON数组格式）
    private Long creatorId; // 创建者ID
    private String creatorName; // 创建者姓名（关联查询）
    private Long collegeId; // 所属学院ID
    private String collegeName; // 学院名称（关联查询）
    private Integer isDeleted; // 是否删除：0-未删除，1-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

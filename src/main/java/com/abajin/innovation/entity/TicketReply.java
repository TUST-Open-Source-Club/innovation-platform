package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单回复实体类
 */
@Data
public class TicketReply {
    private Long id;
    private Long ticketId; // 工单ID
    private Long parentId; // 父回复ID（NULL表示顶层回复）
    private String content; // 回复内容（支持Markdown+LaTeX富文本）
    private Long creatorId; // 回复者ID
    private String creatorName; // 回复者姓名（关联查询）
    private String creatorRole; // 回复者角色（关联查询）
    private Integer isDeleted; // 是否删除：0-未删除，1-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<TicketReply> children; // 子回复列表（树形结构）
}

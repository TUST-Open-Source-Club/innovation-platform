package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 新闻实体类
 */
@Data
public class News {
    private Long id;
    private String title; // 新闻标题
    private Long categoryId; // 分类ID
    private String categoryName; // 分类名称（关联查询）
    private Long authorId; // 作者ID
    private String authorName; // 作者姓名
    private String coverImage; // 封面图片URL
    private String summary; // 摘要
    private String content; // 新闻内容
    private String source; // 来源
    private String attachments; // 附件（JSON数组，存储文件路径）
    private Long relatedActivityId; // 关联活动ID（可选）
    private String relatedActivityTitle; // 关联活动标题（关联查询）
    private Integer viewCount; // 浏览次数
    private Integer likeCount; // 点赞数
    private Integer isTop; // 是否置顶：0-否，1-是
    private String status; // 状态：DRAFT-草稿, PENDING-待审核, PUBLISHED-已发布, REJECTED-已驳回
    private String approvalStatus; // 审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝
    private Long reviewerId; // 审批人ID
    private String reviewerName; // 审批人姓名（关联查询）
    private String reviewComment; // 审批意见
    private LocalDateTime reviewTime; // 审批时间
    private LocalDateTime publishTime; // 发布时间
    private Boolean isDeleted; // 是否删除：false-未删除，true-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

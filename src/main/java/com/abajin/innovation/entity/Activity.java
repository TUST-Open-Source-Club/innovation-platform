package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动实体类
 */
@Data
public class Activity {
    private Long id;
    private String title; // 活动标题
    private Long activityTypeId; // 活动类型ID
    private String activitySeries; // 活动系列：先锋双创榜样、双创技术讲坛、企业家大讲堂
    private String activityTypeOther; // 其他活动类型（选择“其他”时填写）
    private Long organizerId; // 组织者ID（用户ID）
    private String organizerName; // 组织者姓名
    private String organizerType; // 组织者类型：USER-个人, TEAM-团队, ORGANIZATION-组织
    private Long organizerEntityId; // 组织者实体ID（团队ID或组织ID）
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private Long spaceId; // 关联预约空间ID（可选）；为空时使用 location 文本
    private String location; // 活动地点（选空间时可为空间名称，选“其他”时为自定义文本）
    private String description; // 活动描述
    private String content; // 活动内容
    private String registrationLink; // 报名链接
    private String qrCodeUrl; // 报名二维码图片URL
    private Long hostUnitId; // 主办单位ID（组织）
    private String coOrganizerIds; // 承办单位ID列表，逗号分隔
    private String otherUnits; // 其他单位（文本）
    private String posterUrl; // 海报图片URL
    private Integer maxParticipants; // 最大参与人数
    private LocalDateTime registrationDeadline; // 报名截止时间
    private String status; // 状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝, PUBLISHED-已发布, ONGOING-进行中, COMPLETED-已完成, CANCELLED-已取消
    private Integer isDeleted; // 是否删除：0-未删除，1-已删除（软删除）
    private String approvalStatus; // 审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝
    private Long reviewerId; // 审批人ID
    private String reviewComment; // 审批意见
    private LocalDateTime reviewTime; // 审批时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目实体类
 */
@Data
public class Project {
    private Long id;
    private String title; // 项目标题
    private String description; // 项目描述
    private String category; // 项目类别
    private Long leaderId; // 项目负责人ID，null 表示虚位以待
    private String leaderName; // 负责人姓名
    private String leaderPhone; // 负责人电话（关联 user 表）
    private Long previousLeaderId; // 上一任负责人ID（招募负责人时保留联系方式）
    private String previousLeaderName; // 上一任负责人姓名
    private String previousLeaderPhone; // 上一任负责人联系方式
    private String instructorName; // 指导老师姓名
    private Long teamId; // 团队ID
    private String status; // 状态：DRAFT-草稿, PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, IN_PROGRESS-进行中, COMPLETED-已完成
    private Integer isDeleted; // 是否删除：0-未删除，1-已删除（软删除）
    private String approvalStatus; // 审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝（学院/学校两级）
    private String reviewComment; // 审核意见
    private Long reviewerId; // 审核人ID
    private LocalDateTime reviewTime; // 审核时间
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

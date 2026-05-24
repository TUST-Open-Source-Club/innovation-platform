package com.abajin.innovation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队招募申请实体
 */
@Data
public class TeamRecruitmentApplication {
    private Long id;
    private Long recruitmentId;
    private Long teamId;
    private String teamName;
    private String recruitmentPositionName;
    private Long applicantId;
    private String applicantName;
    private String applicantRole;
    private String desiredPosition;
    private Long applicantCollegeId;
    private String applicantCollegeName;
    private String applicantMajor;
    private String qualifications;
    private String answerContent;
    private String resumeUrl;
    private String remark;
    private String approvalStatus;
    private Long approverId;
    private String approverName;
    private String approvalComment;
    private LocalDateTime approvalTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

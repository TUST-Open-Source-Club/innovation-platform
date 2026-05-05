package com.abajin.innovation.service;

import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.entity.FundApplication;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.FundApplicationMapper;
import com.abajin.innovation.mapper.ProjectMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 基金申请服务类
 */
@Service
public class FundApplicationService {
    @Autowired
    private FundApplicationMapper fundApplicationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    /**
     * 创建基金申请
     */
    @Transactional
    public FundApplication createFundApplication(FundApplication application, Long userId) {
        // 验证项目是否存在且用户是负责人
        if (application.getProjectId() != null) {
            Project project = projectMapper.selectById(application.getProjectId());
            if (project == null) {
                throw new RuntimeException("项目不存在");
            }
            if (!project.getLeaderId().equals(userId)) {
                throw new RuntimeException("只有项目负责人可以申请基金");
            }
        }

        User applicant = userMapper.selectById(userId);
        if (applicant == null) {
            throw new RuntimeException("用户不存在");
        }

        application.setApplicantId(userId);
        application.setApplicantName(applicant.getRealName());
        if (application.getProjectId() != null) {
            application.setApplicantType("PROJECT");
            application.setApplicantEntityId(application.getProjectId());
        }
        application.setStatus("DRAFT");
        application.setApprovalStatus(ApprovalStatus.PENDING.name());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        fundApplicationMapper.insert(application);
        return application;
    }

    /**
     * 提交基金申请
     */
    @Transactional
    public FundApplication submitFundApplication(Long id, Long userId) {
        FundApplication application = fundApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("基金申请不存在");
        }
        if (!application.getApplicantId().equals(userId)) {
            throw new RuntimeException("无权提交此申请");
        }
        if (!"DRAFT".equals(application.getStatus())) {
            throw new RuntimeException("只能提交草稿状态的申请");
        }

        application.setStatus("SUBMITTED");
        application.setApprovalStatus(ApprovalStatus.PENDING.name());
        application.setUpdateTime(LocalDateTime.now());
        fundApplicationMapper.update(application);

        if (emailService != null) {
            emailService.notifyCollegeAdmins("基金申请", application.getTitle() != null ? application.getTitle() : "基金申请");
        }
        return application;
    }

    /**
     * 审核基金申请
     */
    @Transactional
    public FundApplication reviewFundApplication(Long id, String approvalStatus, String reviewComment, BigDecimal approvedAmount, Long reviewerId) {
        FundApplication application = fundApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("基金申请不存在");
        }
        // reviewer role: COLLEGE_ADMIN -> first review; SCHOOL_ADMIN -> final review
        User reviewer = userMapper.selectById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("审核人不存在");
        }
        String reviewerRole = reviewer.getRole();

        ApprovalStatus status;
        try {
            status = ApprovalStatus.valueOf(approvalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("审批状态无效");
        }
        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审核操作只能设置为 APPROVED 或 REJECTED");
        }

        if (!ApprovalStatus.PENDING.name().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该申请已审核");
        }

        application.setReviewComment(reviewComment);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());

        boolean isFinal = false;
        if (com.abajin.innovation.common.Constants.ROLE_COLLEGE_ADMIN.equals(reviewerRole)) {
            // First stage: only SUBMITTED applications can be reviewed by college admin
            if (!"SUBMITTED".equals(application.getStatus())) {
                throw new RuntimeException("只能审核待学院审核的申请");
            }
            if (ApprovalStatus.APPROVED.equals(status)) {
                // College approved -> waiting for school review
                application.setStatus("APPROVED");
                application.setApprovalStatus(ApprovalStatus.PENDING.name());
                if (emailService != null) {
                    emailService.notifySchoolAdminsAfterCollegeApproval("基金申请",
                            application.getTitle() != null ? application.getTitle() : "基金申请");
                }
                // approvedAmount decided at final stage
            } else {
                application.setStatus("REJECTED");
                application.setApprovalStatus(ApprovalStatus.REJECTED.name());
                isFinal = true;
            }
        } else if (com.abajin.innovation.common.Constants.ROLE_SCHOOL_ADMIN.equals(reviewerRole)) {
            // 学校管理员可以直接审核待学院审核（SUBMITTED）或学院已通过（APPROVED）的申请
            if (!"SUBMITTED".equals(application.getStatus()) && !"APPROVED".equals(application.getStatus())) {
                throw new RuntimeException("只能审核待审批的申请");
            }
            if (ApprovalStatus.APPROVED.equals(status)) {
                application.setStatus("FUNDED");
                application.setApprovalStatus(ApprovalStatus.APPROVED.name());
                if (approvedAmount != null) {
                    application.setApprovedAmount(approvedAmount);
                } else {
                    application.setApprovedAmount(application.getApplicationAmount());
                }
            } else {
                application.setStatus("REJECTED");
                application.setApprovalStatus(ApprovalStatus.REJECTED.name());
            }
            isFinal = true;
        } else {
            throw new RuntimeException("无权审核申请");
        }

        application.setUpdateTime(LocalDateTime.now());
        fundApplicationMapper.update(application);

        if (isFinal) {
            if (emailService != null) {
                emailService.notifyApplicant(application.getApplicantId(), "基金申请",
                        application.getTitle() != null ? application.getTitle() : "基金申请",
                        "FUNDED".equals(application.getStatus()), reviewComment);
            }
        }
        return application;
    }

    /**
     * 根据ID查询基金申请
     */
    public FundApplication getFundApplicationById(Long id) {
        return fundApplicationMapper.selectById(id);
    }

    /**
     * 查询所有基金申请
     */
    public List<FundApplication> getAllFundApplications() {
        return fundApplicationMapper.selectAll();
    }

    /**
     * 根据申请人ID查询申请列表
     */
    public List<FundApplication> getFundApplicationsByApplicantId(Long applicantId) {
        return fundApplicationMapper.selectByApplicantId(applicantId);
    }

    public List<FundApplication> getFundApplicationsByApplicantIdAndApprovalStatus(Long applicantId, String approvalStatus) {
        return fundApplicationMapper.selectByApplicantIdAndApprovalStatus(applicantId, approvalStatus);
    }

    /**
     * 根据审批状态查询申请列表
     */
    public List<FundApplication> getFundApplicationsByApprovalStatus(String approvalStatus) {
        return fundApplicationMapper.selectByApprovalStatus(approvalStatus);
    }

    public List<FundApplication> getFundApplicationsByStatusAndApprovalStatus(String status, String approvalStatus) {
        return fundApplicationMapper.selectByStatusAndApprovalStatus(status, approvalStatus);
    }
}

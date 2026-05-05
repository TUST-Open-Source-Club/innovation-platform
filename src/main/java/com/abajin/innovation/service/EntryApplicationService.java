package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.entity.EntryApplication;
import com.abajin.innovation.entity.Team;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.enums.EntryStatus;
import com.abajin.innovation.mapper.EntryApplicationMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 入驻管理服务类
 */
@Service
public class EntryApplicationService {
    @Autowired
    private EntryApplicationMapper entryApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private EmailService emailService;

    /**
     * 创建入驻申请（草稿状态）
     */
    @Transactional
    public EntryApplication createEntryApplication(EntryApplication application, Long applicantId) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new RuntimeException("申请人不存在");
        }

        application.setApplicantId(applicantId);
        application.setApplicantName(applicant.getRealName());
        application.setStatus(EntryStatus.DRAFT.name());
        application.setApprovalStatus(ApprovalStatus.PENDING.name());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        entryApplicationMapper.insert(application);
        return application;
    }

    /**
     * 提交入驻申请（待审核状态）
     */
    @Transactional
    public EntryApplication submitEntryApplication(Long applicationId, Long applicantId) {
        EntryApplication application = entryApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("入驻申请不存在");
        }
        if (!application.getApplicantId().equals(applicantId)) {
            throw new RuntimeException("无权提交此申请");
        }
        if (!EntryStatus.DRAFT.name().equals(application.getStatus())) {
            throw new RuntimeException("只能提交草稿状态的申请");
        }

        // 验证必填字段
        if (application.getTeamName() == null || application.getTeamName().trim().isEmpty()) {
            throw new RuntimeException("创新团队名称不能为空");
        }
        if (application.getApplicantStudentId() == null || application.getApplicantStudentId().trim().isEmpty()) {
            throw new RuntimeException("发起人学号不能为空");
        }
        if (application.getApplicantPhone() == null || application.getApplicantPhone().trim().isEmpty()) {
            throw new RuntimeException("发起人联系方式不能为空");
        }
        if (application.getProjectName() == null || application.getProjectName().trim().isEmpty()) {
            throw new RuntimeException("项目名称不能为空");
        }
        if (application.getTeamPositioning() == null || application.getTeamPositioning().trim().isEmpty()) {
            throw new RuntimeException("创新团队定位与建设思路不能为空");
        }
        if (application.getContactPhone() == null || application.getContactPhone().trim().isEmpty()) {
            throw new RuntimeException("联系电话不能为空");
        }
        if (application.getRecruitmentRequirements() == null || application.getRecruitmentRequirements().trim().isEmpty()) {
            throw new RuntimeException("招募人员的要求不能为空");
        }

        application.setStatus(EntryStatus.PENDING.name());
        application.setApprovalStatus(ApprovalStatus.PENDING.name());
        application.setUpdateTime(LocalDateTime.now());
        entryApplicationMapper.update(application);

        if (emailService != null) {
            emailService.notifyCollegeAdmins("入驻申请", application.getTeamName());
        }
        return application;
    }

    /**
     * 审核入驻申请（学校管理员）
     */
    @Transactional
    public EntryApplication reviewEntryApplication(Long applicationId, String approvalStatus, 
                                                   String reviewComment, Long reviewerId) {
        EntryApplication application = entryApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("入驻申请不存在");
        }
        User reviewer = userMapper.selectById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("审核人不存在");
        }
        String reviewerRole = reviewer.getRole();

        // 验证审批状态
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
        if (Constants.ROLE_COLLEGE_ADMIN.equals(reviewerRole)) {
            // 学院管理员初审：只能审核待学院审核的申请
            if (!EntryStatus.PENDING.name().equals(application.getStatus())) {
                throw new RuntimeException("只能审核待学院审核的申请");
            }
            if (ApprovalStatus.APPROVED.equals(status)) {
                // 学院通过，等待学校终审
                application.setStatus(EntryStatus.APPROVED.name());
                application.setApprovalStatus(ApprovalStatus.PENDING.name());
                if (emailService != null) {
                    emailService.notifySchoolAdminsAfterCollegeApproval("入驻申请", application.getTeamName());
                }
            } else {
                application.setStatus(EntryStatus.REJECTED.name());
                application.setApprovalStatus(ApprovalStatus.REJECTED.name());
                isFinal = true;
            }
        } else if (Constants.ROLE_SCHOOL_ADMIN.equals(reviewerRole)) {
            // 学校管理员可以直接审核待学院审核（PENDING）或学院已通过（APPROVED）的申请
            if (!EntryStatus.PENDING.name().equals(application.getStatus())
                    && !EntryStatus.APPROVED.name().equals(application.getStatus())) {
                throw new RuntimeException("只能审核待审批的申请");
            }
            if (ApprovalStatus.APPROVED.equals(status)) {
                // 终审通过后直接视为“已入驻”，并创建团队记录
                application.setStatus(EntryStatus.ENTERED.name());
                application.setApprovalStatus(ApprovalStatus.APPROVED.name());
                application.setEntryTime(LocalDateTime.now());

                // 基于入驻申请创建团队
                Team team = new Team();
                team.setName(application.getTeamName());
                team.setTeamType(application.getTeamType());
                team.setDescription(application.getTeamDescription());
                team.setInstructorName(application.getInstructorName());
                team.setRecruitmentRequirement(application.getRecruitmentRequirements());
                team.setRecruiting(application.getRecruitmentRequirements() != null && !application.getRecruitmentRequirements().trim().isEmpty());
                team.setIsPublic(true);
                teamService.createTeam(team, application.getApplicantId());
            } else {
                application.setStatus(EntryStatus.REJECTED.name());
                application.setApprovalStatus(ApprovalStatus.REJECTED.name());
            }
            isFinal = true;
        } else {
            throw new RuntimeException("无权审核申请");
        }

        application.setUpdateTime(LocalDateTime.now());
        entryApplicationMapper.update(application);

        if (isFinal && emailService != null) {
            emailService.notifyApplicant(application.getApplicantId(), "入驻申请", application.getTeamName(),
                    EntryStatus.ENTERED.name().equals(application.getStatus()), reviewComment);
        }
        return application;
    }

    /**
     * 确认入驻（审核通过后，正式入驻）
     */
    @Transactional
    public EntryApplication confirmEntry(Long applicationId) {
        EntryApplication application = entryApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("入驻申请不存在");
        }
        if (!EntryStatus.APPROVED.name().equals(application.getStatus())) {
            throw new RuntimeException("只能确认已通过审核的申请");
        }

        application.setStatus(EntryStatus.ENTERED.name());
        application.setEntryTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        entryApplicationMapper.update(application);
        return application;
    }

    /**
     * 退出入驻
     */
    @Transactional
    public EntryApplication exitEntry(Long applicationId, String exitReason) {
        EntryApplication application = entryApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("入驻申请不存在");
        }
        if (!EntryStatus.ENTERED.name().equals(application.getStatus())) {
            throw new RuntimeException("只能退出已入驻状态的申请");
        }

        application.setStatus(EntryStatus.EXITED.name());
        application.setExitTime(LocalDateTime.now());
        application.setExitReason(exitReason);
        application.setUpdateTime(LocalDateTime.now());
        entryApplicationMapper.update(application);
        return application;
    }

    /**
     * 分页查询入驻申请
     */
    public List<EntryApplication> getEntryApplications(int pageNum, int pageSize, String teamName,
                                                        String status, String approvalStatus,
                                                        String applicationType, Long applicantId) {
        int offset = (pageNum - 1) * pageSize;
        return entryApplicationMapper.selectPage(offset, pageSize, teamName, status, 
                                                 approvalStatus, applicationType, applicantId);
    }

    /**
     * 统计入驻申请总数
     */
    public int countEntryApplications(String teamName, String status, String approvalStatus,
                                      String applicationType, Long applicantId) {
        return entryApplicationMapper.count(teamName, status, approvalStatus, applicationType, applicantId);
    }

    /**
     * 查询入驻申请详情
     */
    public EntryApplication getEntryApplicationById(Long id) {
        return entryApplicationMapper.selectById(id);
    }

    /**
     * 更新入驻申请（仅申请人可更新草稿状态）
     */
    @Transactional
    public EntryApplication updateEntryApplication(Long applicationId, EntryApplication application, Long applicantId) {
        EntryApplication existing = entryApplicationMapper.selectById(applicationId);
        if (existing == null) {
            throw new RuntimeException("入驻申请不存在");
        }
        if (!existing.getApplicantId().equals(applicantId)) {
            throw new RuntimeException("无权更新此申请");
        }
        if (!EntryStatus.DRAFT.name().equals(existing.getStatus())) {
            throw new RuntimeException("只能更新草稿状态的申请");
        }

        // 更新字段
        if (application.getTeamName() != null) existing.setTeamName(application.getTeamName());
        if (application.getPartnerCompany() != null) existing.setPartnerCompany(application.getPartnerCompany());
        if (application.getApplicantStudentId() != null) existing.setApplicantStudentId(application.getApplicantStudentId());
        if (application.getApplicantPhone() != null) existing.setApplicantPhone(application.getApplicantPhone());
        if (application.getTeamType() != null) existing.setTeamType(application.getTeamType());
        if (application.getInnovationDirection() != null) existing.setInnovationDirection(application.getInnovationDirection());
        if (application.getTeamDescription() != null) existing.setTeamDescription(application.getTeamDescription());
        if (application.getTeamPositioning() != null) existing.setTeamPositioning(application.getTeamPositioning());
        if (application.getTeamSize() != null) existing.setTeamSize(application.getTeamSize());
        if (application.getRecruitmentRequirements() != null) existing.setRecruitmentRequirements(application.getRecruitmentRequirements());
        if (application.getInstructorName() != null) existing.setInstructorName(application.getInstructorName());
        if (application.getInstructorContact() != null) existing.setInstructorContact(application.getInstructorContact());
        if (application.getCampusMentorName() != null) existing.setCampusMentorName(application.getCampusMentorName());
        if (application.getCampusMentorContact() != null) existing.setCampusMentorContact(application.getCampusMentorContact());
        if (application.getEnterpriseMentorName() != null) existing.setEnterpriseMentorName(application.getEnterpriseMentorName());
        if (application.getEnterpriseMentorContact() != null) existing.setEnterpriseMentorContact(application.getEnterpriseMentorContact());
        if (application.getProjectName() != null) existing.setProjectName(application.getProjectName());
        if (application.getProjectDescription() != null) existing.setProjectDescription(application.getProjectDescription());
        if (application.getProjectAchievements() != null) existing.setProjectAchievements(application.getProjectAchievements());
        if (application.getExpectedOutcomes() != null) existing.setExpectedOutcomes(application.getExpectedOutcomes());
        if (application.getIsCompetitionRegistered() != null) existing.setIsCompetitionRegistered(application.getIsCompetitionRegistered());
        if (application.getCompetitionName() != null) existing.setCompetitionName(application.getCompetitionName());
        if (application.getTeamMembers() != null) existing.setTeamMembers(application.getTeamMembers());
        if (application.getAttachments() != null) existing.setAttachments(application.getAttachments());
        if (application.getContactPhone() != null) existing.setContactPhone(application.getContactPhone());
        if (application.getContactEmail() != null) existing.setContactEmail(application.getContactEmail());

        existing.setUpdateTime(LocalDateTime.now());
        entryApplicationMapper.update(existing);
        return existing;
    }

    /**
     * 删除入驻申请（仅申请人可删除草稿状态）
     */
    @Transactional
    public void deleteEntryApplication(Long applicationId, Long applicantId) {
        EntryApplication application = entryApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("入驻申请不存在");
        }
        if (!application.getApplicantId().equals(applicantId)) {
            throw new RuntimeException("无权删除此申请");
        }
        if (!EntryStatus.DRAFT.name().equals(application.getStatus())) {
            throw new RuntimeException("只能删除草稿状态的申请");
        }

        entryApplicationMapper.deleteById(applicationId);
    }

    /**
     * 查询申请人的所有申请
     */
    public List<EntryApplication> getMyApplications(Long applicantId) {
        return entryApplicationMapper.selectByApplicantId(applicantId);
    }
}

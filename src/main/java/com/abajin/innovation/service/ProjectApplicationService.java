package com.abajin.innovation.service;

import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.dto.ProjectApplicationDTO;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.ProjectApplication;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.MemberStatus;
import com.abajin.innovation.mapper.ProjectApplicationMapper;
import com.abajin.innovation.mapper.ProjectMapper;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 项目申请Service
 * 处理学生申请加入团队、教师申请接管项目
 */
@Service
public class ProjectApplicationService {
    @Autowired
    private ProjectApplicationMapper applicationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 学生申请加入项目团队
     */
    @Transactional
    public ProjectApplication applyToJoinTeam(ProjectApplicationDTO dto, Long applicantId) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new RuntimeException("申请人不存在");
        }

        if (!Constants.ROLE_STUDENT.equals(applicant.getRole())
                && !Constants.ROLE_STUDENT_ADMIN.equals(applicant.getRole())
                && !Constants.ROLE_COLLEGE_ADMIN.equals(applicant.getRole())
                && !Constants.ROLE_SCHOOL_ADMIN.equals(applicant.getRole())) {
            throw new RuntimeException("只有学生或管理员可以申请加入团队");
        }

        Project project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        // 检查是否已是团队成员
        TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(
            project.getTeamId(), applicantId);
        if (existingMember != null && MemberStatus.ACTIVE.getCode().equals(existingMember.getStatus())) {
            throw new RuntimeException("您已是该项目的团队成员");
        }

        // 检查是否已有待审批的申请
        List<ProjectApplication> pendingApplications = applicationMapper.selectByProjectId(dto.getProjectId());
        for (ProjectApplication app : pendingApplications) {
            if (app.getApplicantId().equals(applicantId) && 
                app.getApplicationType().equals("JOIN_TEAM") &&
                ApprovalStatus.PENDING.getCode().equals(app.getApprovalStatus()) &&
                app.getStatus() == 1) {
                throw new RuntimeException("您已有待审批的申请");
            }
        }

        // 获取项目负责人信息
        User projectLeader = userMapper.selectById(project.getLeaderId());
        if (projectLeader == null) {
            throw new RuntimeException("项目负责人不存在");
        }

        ProjectApplication application = new ProjectApplication();
        application.setApplicationNo(generateApplicationNo());
        application.setProjectId(dto.getProjectId());
        application.setProjectTitle(project.getTitle());
        application.setApplicantId(applicantId);
        application.setApplicantName(applicant.getRealName());
        application.setApplicantRole(applicant.getRole());
        application.setApplicationType("JOIN_TEAM");
        application.setApplicationContent(dto.getApplicationContent());
        application.setQualifications(dto.getQualifications());
        application.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone() : applicant.getPhone());
        application.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail() : applicant.getEmail());
        application.setApprovalStatus(ApprovalStatus.PENDING.getCode());
        application.setApproverRole("PROJECT_LEADER"); // 由项目负责人审批
        application.setStatus(1);
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        applicationMapper.insert(application);
        return application;
    }

    /**
     * 教师申请接管无人管理项目
     */
    @Transactional
    public ProjectApplication applyToTakeOverProject(ProjectApplicationDTO dto, Long applicantId) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new RuntimeException("申请人不存在");
        }

        if (!Constants.ROLE_TEACHER.equals(applicant.getRole())
                && !Constants.ROLE_STUDENT_ADMIN.equals(applicant.getRole())
                && !Constants.ROLE_COLLEGE_ADMIN.equals(applicant.getRole())
                && !Constants.ROLE_SCHOOL_ADMIN.equals(applicant.getRole())) {
            throw new RuntimeException("只有教师或管理员可以申请接管项目");
        }

        Project project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        // 检查项目是否有负责人
        if (project.getLeaderId() != null) {
            throw new RuntimeException("该项目已有负责人，无法申请接管");
        }

        // 检查是否已有待审批的接管申请
        List<ProjectApplication> pendingApplications = applicationMapper.selectByProjectId(dto.getProjectId());
        for (ProjectApplication app : pendingApplications) {
            if (app.getApplicationType().equals("TAKE_OVER") &&
                ApprovalStatus.PENDING.getCode().equals(app.getApprovalStatus()) &&
                app.getStatus() == 1) {
                throw new RuntimeException("该项目已有待审批的接管申请");
            }
        }

        ProjectApplication application = new ProjectApplication();
        application.setApplicationNo(generateApplicationNo());
        application.setProjectId(dto.getProjectId());
        application.setProjectTitle(project.getTitle());
        application.setApplicantId(applicantId);
        application.setApplicantName(applicant.getRealName());
        application.setApplicantRole(applicant.getRole());
        application.setApplicationType("TAKE_OVER");
        application.setApplicationContent(dto.getApplicationContent());
        application.setQualifications(dto.getQualifications());
        application.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone() : applicant.getPhone());
        application.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail() : applicant.getEmail());
        application.setApprovalStatus(ApprovalStatus.PENDING.getCode());
        application.setApproverRole("COLLEGE_ADMIN"); // 由学院管理员审批
        application.setStatus(1);
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        applicationMapper.insert(application);
        return application;
    }

    /**
     * 项目负责人审批学生加入团队申请
     */
    @Transactional
    public void approveJoinTeamApplication(Long applicationId, String approvalStatus, String comment, Long approverId) {
        ProjectApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"JOIN_TEAM".equals(application.getApplicationType())) {
            throw new RuntimeException("该申请不是加入团队申请");
        }

        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该申请已处理，无法重复审批");
        }

        Project project = projectMapper.selectById(application.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        // 验证审批人是否为项目负责人
        if (!project.getLeaderId().equals(approverId)) {
            throw new RuntimeException("只有项目负责人可以审批此申请");
        }

        User approver = userMapper.selectById(approverId);
        ApprovalStatus status = ApprovalStatus.fromCode(approvalStatus);

        application.setApprovalStatus(status.getCode());
        application.setApproverId(approverId);
        application.setApproverName(approver.getRealName());
        application.setApproverRole("PROJECT_LEADER");
        application.setApprovalTime(LocalDateTime.now());
        application.setApprovalComment(comment);
        application.setUpdateTime(LocalDateTime.now());

        applicationMapper.update(application);

        // 如果审批通过，将学生加入团队
        if (ApprovalStatus.APPROVED.equals(status)) {
            // 检查团队是否存在
            if (project.getTeamId() == null) {
                throw new RuntimeException("项目没有关联团队，无法加入");
            }

            // 检查是否已是成员
            TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(
                project.getTeamId(), application.getApplicantId());
            if (existingMember == null) {
                TeamMember member = new TeamMember();
                member.setTeamId(project.getTeamId());
                member.setUserId(application.getApplicantId());
                member.setUserName(application.getApplicantName());
                member.setRole("MEMBER");
                member.setStatus(MemberStatus.ACTIVE.getCode());
                member.setJoinTime(LocalDateTime.now());
                teamMemberMapper.insert(member);
            } else {
                existingMember.setStatus(MemberStatus.ACTIVE.getCode());
                existingMember.setJoinTime(LocalDateTime.now());
                teamMemberMapper.update(existingMember);
            }
        }
    }

    /**
     * 学院管理员审批教师接管项目申请
     */
    @Transactional
    public void approveTakeOverApplication(Long applicationId, String approvalStatus, String comment, Long approverId) {
        ProjectApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"TAKE_OVER".equals(application.getApplicationType())) {
            throw new RuntimeException("该申请不是接管项目申请");
        }

        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该申请已处理，无法重复审批");
        }

        User approver = userMapper.selectById(approverId);
        if (approver == null || !Constants.ROLE_COLLEGE_ADMIN.equals(approver.getRole())) {
            throw new RuntimeException("只有学院管理员可以审批此申请");
        }

        ApprovalStatus status = ApprovalStatus.fromCode(approvalStatus);

        application.setApprovalStatus(status.getCode());
        application.setApproverId(approverId);
        application.setApproverName(approver.getRealName());
        application.setApproverRole("COLLEGE_ADMIN");
        application.setApprovalTime(LocalDateTime.now());
        application.setApprovalComment(comment);
        application.setUpdateTime(LocalDateTime.now());

        applicationMapper.update(application);

        // 如果审批通过，将教师设置为项目负责人
        if (ApprovalStatus.APPROVED.equals(status)) {
            Project project = projectMapper.selectById(application.getProjectId());
            if (project != null) {
                project.setLeaderId(application.getApplicantId());
                project.setLeaderName(application.getApplicantName());
                project.setUpdateTime(LocalDateTime.now());
                projectMapper.update(project);
            }
        }
    }

    /**
     * 获取我的申请列表
     */
    public List<ProjectApplication> getMyApplications(Long applicantId) {
        return applicationMapper.selectByApplicantId(applicantId);
    }

    /**
     * 获取项目申请列表
     */
    public List<ProjectApplication> getProjectApplications(Long projectId) {
        return applicationMapper.selectByProjectId(projectId);
    }

    /**
     * 获取待审批列表（项目负责人）
     */
    public List<ProjectApplication> getPendingJoinTeamApplications(Long projectLeaderId) {
        // 查询该负责人管理的所有项目的待审批申请
        return applicationMapper.selectPendingApprovals("PROJECT_LEADER");
    }

    /**
     * 获取待审批列表（学院管理员）
     */
    public List<ProjectApplication> getPendingTakeOverApplications() {
        return applicationMapper.selectPendingApprovals("COLLEGE_ADMIN");
    }

    /**
     * 生成申请编号
     */
    private String generateApplicationNo() {
        return "PROJ_APP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

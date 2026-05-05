package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.dto.ProjectApplicationDTO;
import com.abajin.innovation.dto.ProjectDockingReviewDTO;
import com.abajin.innovation.dto.ProjectRecruitmentApplicationDTO;
import com.abajin.innovation.dto.ProjectRecruitmentDTO;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.ProjectApplication;
import com.abajin.innovation.entity.ProjectRecruitment;
import com.abajin.innovation.entity.ProjectRecruitmentApplication;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.enums.MemberStatus;
import com.abajin.innovation.mapper.ProjectApplicationMapper;
import com.abajin.innovation.mapper.ProjectMapper;
import com.abajin.innovation.mapper.ProjectRecruitmentApplicationMapper;
import com.abajin.innovation.mapper.ProjectRecruitmentMapper;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 项目对接服务
 */
@Service
public class ProjectDockingService {

    private static final String APPLICATION_TYPE_JOIN_TEAM = "JOIN_TEAM";
    private static final String RECRUITMENT_STATUS_OPEN = "OPEN";

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectApplicationMapper projectApplicationMapper;

    @Autowired
    private ProjectRecruitmentMapper projectRecruitmentMapper;

    @Autowired
    private ProjectRecruitmentApplicationMapper projectRecruitmentApplicationMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public ProjectApplication applyToProject(Long projectId, ProjectApplicationDTO dto, Long applicantId) {
        User applicant = getExistingUser(applicantId);
        ensureProjectApplicantRole(applicant.getRole());

        Project project = getExistingProject(projectId);
        if (project.getLeaderId() == null) {
            throw new RuntimeException("项目当前没有负责人，暂不支持主动申请");
        }
        if (project.getLeaderId().equals(applicantId)) {
            throw new RuntimeException("项目负责人不能申请加入自己的项目");
        }
        ensureNotActiveTeamMember(project, applicantId);

        List<ProjectApplication> existingApplications =
                projectApplicationMapper.selectByProjectIdAndApplicantIdAndType(projectId, applicantId, APPLICATION_TYPE_JOIN_TEAM);
        for (ProjectApplication application : existingApplications) {
            if (ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
                throw new RuntimeException("您已有待审批的主动申请");
            }
            if (ApprovalStatus.APPROVED.getCode().equals(application.getApprovalStatus())) {
                throw new RuntimeException("您的主动申请已通过，无需重复提交");
            }
        }

        ProjectApplication application = new ProjectApplication();
        application.setApplicationNo(generateNo("PROJ_APP"));
        application.setProjectId(projectId);
        application.setProjectTitle(project.getTitle());
        application.setApplicantId(applicantId);
        application.setApplicantName(applicant.getRealName());
        application.setApplicantRole(applicant.getRole());
        application.setApplicationType(APPLICATION_TYPE_JOIN_TEAM);
        application.setDesiredPosition(dto.getDesiredPosition());
        application.setApplicantCollegeId(applicant.getCollegeId());
        application.setApplicantCollegeName(applicant.getCollegeName());
        application.setApplicantMajor(dto.getApplicantMajor());
        application.setApplicationContent(dto.getApplicationContent());
        application.setQualifications(dto.getQualifications());
        application.setResumeUrl(dto.getResumeUrl());
        application.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone() : applicant.getPhone());
        application.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail() : applicant.getEmail());
        application.setApprovalStatus(ApprovalStatus.PENDING.getCode());
        application.setApproverRole("PROJECT_LEADER");
        application.setStatus(1);
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        projectApplicationMapper.insert(application);
        return application;
    }

    public List<ProjectApplication> getProjectApplications(Long projectId, Long userId, String role) {
        Project project = getExistingProject(projectId);
        if (canManageProjectApplications(project, userId, role)) {
            return projectApplicationMapper.selectByProjectIdAndType(projectId, APPLICATION_TYPE_JOIN_TEAM);
        }
        return projectApplicationMapper.selectByProjectIdAndApplicantIdAndType(projectId, userId, APPLICATION_TYPE_JOIN_TEAM);
    }

    @Transactional
    public ProjectApplication withdrawProjectApplication(Long applicationId, Long userId) {
        ProjectApplication application = projectApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        if (!application.getApplicantId().equals(userId)) {
            throw new RuntimeException("只能撤回自己的申请");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("当前申请无法撤回");
        }
        application.setApprovalStatus(ApprovalStatus.WITHDRAWN.getCode());
        application.setUpdateTime(LocalDateTime.now());
        projectApplicationMapper.update(application);
        return projectApplicationMapper.selectById(applicationId);
    }

    @Transactional
    public ProjectApplication reviewProjectApplication(Long applicationId, ProjectDockingReviewDTO dto, Long approverId) {
        ProjectApplication application = projectApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        if (!APPLICATION_TYPE_JOIN_TEAM.equals(application.getApplicationType())) {
            throw new RuntimeException("该申请不是主动加入项目申请");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该申请已处理");
        }

        Project project = getExistingProject(application.getProjectId());
        if (project.getLeaderId() == null || !project.getLeaderId().equals(approverId)) {
            throw new RuntimeException("只有项目负责人可以审批该申请");
        }

        ApprovalStatus approvalStatus = ApprovalStatus.fromCode(dto.getApprovalStatus());
        if (approvalStatus != ApprovalStatus.APPROVED && approvalStatus != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审批结果只能是 APPROVED 或 REJECTED");
        }

        User approver = getExistingUser(approverId);
        application.setApprovalStatus(approvalStatus.getCode());
        application.setApproverId(approverId);
        application.setApproverName(approver.getRealName());
        application.setApproverRole("PROJECT_LEADER");
        application.setApprovalComment(dto.getApprovalComment());
        application.setApprovalTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        projectApplicationMapper.update(application);

        if (approvalStatus == ApprovalStatus.APPROVED) {
            attachApplicantToProjectTeam(project, application.getApplicantId(), application.getApplicantName());
        }

        return projectApplicationMapper.selectById(applicationId);
    }

    @Transactional
    public ProjectRecruitment createRecruitment(Long projectId, ProjectRecruitmentDTO dto, Long publisherId) {
        Project project = getExistingProject(projectId);
        if (project.getLeaderId() == null || !project.getLeaderId().equals(publisherId)) {
            throw new RuntimeException("只有项目负责人可以发布招募");
        }

        User publisher = getExistingUser(publisherId);
        ProjectRecruitment recruitment = new ProjectRecruitment();
        recruitment.setProjectId(projectId);
        recruitment.setProjectTitle(project.getTitle());
        recruitment.setPublisherId(publisherId);
        recruitment.setPublisherName(publisher.getRealName());
        recruitment.setPositionName(dto.getPositionName());
        recruitment.setTaskDescription(dto.getTaskDescription());
        recruitment.setCollegePreference(dto.getCollegePreference());
        recruitment.setMajorPreference(dto.getMajorPreference());
        recruitment.setQuestionContent(dto.getQuestionContent());
        recruitment.setStatus(RECRUITMENT_STATUS_OPEN);
        recruitment.setCreateTime(LocalDateTime.now());
        recruitment.setUpdateTime(LocalDateTime.now());
        projectRecruitmentMapper.insert(recruitment);
        return recruitment;
    }

    public List<ProjectRecruitment> getRecruitments(Long projectId) {
        getExistingProject(projectId);
        return projectRecruitmentMapper.selectByProjectId(projectId);
    }

    @Transactional
    public void deleteRecruitment(Long recruitmentId, Long userId) {
        ProjectRecruitment recruitment = projectRecruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        Project project = getExistingProject(recruitment.getProjectId());
        if (project.getLeaderId() == null || !project.getLeaderId().equals(userId)) {
            throw new RuntimeException("只有项目负责人可以删除招募");
        }
        projectRecruitmentMapper.deleteById(recruitmentId);
    }

    @Transactional
    public ProjectRecruitmentApplication applyToRecruitment(Long recruitmentId,
                                                            ProjectRecruitmentApplicationDTO dto,
                                                            Long applicantId) {
        User applicant = getExistingUser(applicantId);
        ensureProjectApplicantRole(applicant.getRole());

        ProjectRecruitment recruitment = projectRecruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        if (!RECRUITMENT_STATUS_OPEN.equals(recruitment.getStatus())) {
            throw new RuntimeException("该招募已关闭");
        }

        Project project = getExistingProject(recruitment.getProjectId());
        if (project.getLeaderId() != null && project.getLeaderId().equals(applicantId)) {
            throw new RuntimeException("项目负责人不能申请自己的招募");
        }
        ensureNotActiveTeamMember(project, applicantId);

        if (recruitment.getQuestionContent() != null
                && !recruitment.getQuestionContent().isBlank()
                && (dto.getAnswerContent() == null || dto.getAnswerContent().isBlank())) {
            throw new RuntimeException("请先回答招募问题");
        }

        List<ProjectRecruitmentApplication> existingApplications =
                projectRecruitmentApplicationMapper.selectByRecruitmentIdAndApplicantId(recruitmentId, applicantId);
        for (ProjectRecruitmentApplication application : existingApplications) {
            if (ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
                throw new RuntimeException("您已有待审批的招募申请");
            }
            if (ApprovalStatus.APPROVED.getCode().equals(application.getApprovalStatus())) {
                throw new RuntimeException("您的招募申请已通过，无需重复提交");
            }
        }

        ProjectRecruitmentApplication application = new ProjectRecruitmentApplication();
        application.setRecruitmentId(recruitmentId);
        application.setProjectId(recruitment.getProjectId());
        application.setApplicantId(applicantId);
        application.setApplicantName(applicant.getRealName());
        application.setApplicantRole(applicant.getRole());
        application.setDesiredPosition(dto.getDesiredPosition());
        application.setApplicantCollegeId(applicant.getCollegeId());
        application.setApplicantCollegeName(applicant.getCollegeName());
        application.setApplicantMajor(dto.getApplicantMajor());
        application.setQualifications(dto.getQualifications());
        application.setAnswerContent(dto.getAnswerContent());
        application.setResumeUrl(dto.getResumeUrl());
        application.setRemark(dto.getRemark());
        application.setApprovalStatus(ApprovalStatus.PENDING.getCode());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        projectRecruitmentApplicationMapper.insert(application);
        return projectRecruitmentApplicationMapper.selectById(application.getId());
    }

    public List<ProjectRecruitmentApplication> getRecruitmentApplications(Long projectId, Long userId, String role) {
        Project project = getExistingProject(projectId);
        if (canManageProjectApplications(project, userId, role)) {
            return projectRecruitmentApplicationMapper.selectByProjectId(projectId);
        }
        return projectRecruitmentApplicationMapper.selectByProjectIdAndApplicantId(projectId, userId);
    }

    @Transactional
    public ProjectRecruitmentApplication withdrawRecruitmentApplication(Long applicationId, Long userId) {
        ProjectRecruitmentApplication application = projectRecruitmentApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("招募申请不存在");
        }
        if (!application.getApplicantId().equals(userId)) {
            throw new RuntimeException("只能撤回自己的招募申请");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("当前招募申请无法撤回");
        }
        application.setApprovalStatus(ApprovalStatus.WITHDRAWN.getCode());
        application.setUpdateTime(LocalDateTime.now());
        projectRecruitmentApplicationMapper.update(application);
        return projectRecruitmentApplicationMapper.selectById(applicationId);
    }

    @Transactional
    public ProjectRecruitmentApplication reviewRecruitmentApplication(Long applicationId,
                                                                      ProjectDockingReviewDTO dto,
                                                                      Long approverId) {
        ProjectRecruitmentApplication application = projectRecruitmentApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("招募申请不存在");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该招募申请已处理");
        }

        ProjectRecruitment recruitment = projectRecruitmentMapper.selectById(application.getRecruitmentId());
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        Project project = getExistingProject(recruitment.getProjectId());
        if (project.getLeaderId() == null || !project.getLeaderId().equals(approverId)) {
            throw new RuntimeException("只有项目负责人可以审批招募申请");
        }

        ApprovalStatus approvalStatus = ApprovalStatus.fromCode(dto.getApprovalStatus());
        if (approvalStatus != ApprovalStatus.APPROVED && approvalStatus != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审批结果只能是 APPROVED 或 REJECTED");
        }

        User approver = getExistingUser(approverId);
        application.setApprovalStatus(approvalStatus.getCode());
        application.setApproverId(approverId);
        application.setApproverName(approver.getRealName());
        application.setApprovalComment(dto.getApprovalComment());
        application.setApprovalTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        projectRecruitmentApplicationMapper.update(application);

        if (approvalStatus == ApprovalStatus.APPROVED) {
            attachApplicantToProjectTeam(project, application.getApplicantId(), application.getApplicantName());
        }

        return projectRecruitmentApplicationMapper.selectById(applicationId);
    }

    private Project getExistingProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        return project;
    }

    private User getExistingUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    private void ensureProjectApplicantRole(String role) {
        if (!Constants.ROLE_STUDENT.equals(role) && !Constants.ROLE_STUDENT_ADMIN.equals(role)) {
            throw new RuntimeException("只有学生和学生管理员可以发起申请");
        }
    }

    private boolean canManageProjectApplications(Project project, Long userId, String role) {
        return isAdminRole(role) || (project.getLeaderId() != null && project.getLeaderId().equals(userId));
    }

    private boolean isAdminRole(String role) {
        return Constants.ROLE_COLLEGE_ADMIN.equals(role) || Constants.ROLE_SCHOOL_ADMIN.equals(role);
    }

    private void ensureNotActiveTeamMember(Project project, Long applicantId) {
        if (project.getTeamId() == null) {
            return;
        }
        TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(project.getTeamId(), applicantId);
        if (existingMember != null && MemberStatus.ACTIVE.getCode().equals(existingMember.getStatus())) {
            throw new RuntimeException("您已是该项目的团队成员");
        }
    }

    private void attachApplicantToProjectTeam(Project project, Long applicantId, String applicantName) {
        if (project.getTeamId() == null) {
            return;
        }
        TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(project.getTeamId(), applicantId);
        if (existingMember == null) {
            TeamMember member = new TeamMember();
            member.setTeamId(project.getTeamId());
            member.setUserId(applicantId);
            member.setUserName(applicantName);
            member.setRole("MEMBER");
            member.setStatus(MemberStatus.ACTIVE.getCode());
            member.setApprovalStatus(ApprovalStatus.APPROVED.getCode());
            member.setJoinTime(LocalDateTime.now());
            teamMemberMapper.insert(member);
            return;
        }
        existingMember.setStatus(MemberStatus.ACTIVE.getCode());
        existingMember.setApprovalStatus(ApprovalStatus.APPROVED.getCode());
        existingMember.setJoinTime(LocalDateTime.now());
        teamMemberMapper.update(existingMember);
    }

    private String generateNo(String prefix) {
        return prefix + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

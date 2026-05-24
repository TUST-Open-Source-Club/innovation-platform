package com.abajin.innovation.service;

import com.abajin.innovation.dto.TeamRecruitmentApplicationDTO;
import com.abajin.innovation.dto.TeamRecruitmentDTO;
import com.abajin.innovation.entity.Team;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.TeamRecruitment;
import com.abajin.innovation.entity.TeamRecruitmentApplication;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.enums.MemberStatus;
import com.abajin.innovation.mapper.TeamMapper;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.mapper.TeamRecruitmentApplicationMapper;
import com.abajin.innovation.mapper.TeamRecruitmentMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团队招募服务
 */
@Service
public class TeamRecruitmentService {

    private static final String RECRUITMENT_STATUS_OPEN = "OPEN";

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TeamRecruitmentMapper teamRecruitmentMapper;

    @Autowired
    private TeamRecruitmentApplicationMapper teamRecruitmentApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Transactional
    public TeamRecruitment createRecruitment(Long teamId, TeamRecruitmentDTO dto, Long publisherId) {
        Team team = getExistingTeam(teamId);
        if (!canManageTeamRecruitment(team, publisherId)) {
            throw new RuntimeException("只有团队负责人可以发布招募");
        }

        User publisher = getExistingUser(publisherId);
        TeamRecruitment recruitment = new TeamRecruitment();
        recruitment.setTeamId(teamId);
        recruitment.setTeamName(team.getName());
        recruitment.setPublisherId(publisherId);
        recruitment.setPublisherName(publisher.getRealName());
        recruitment.setPositionName(dto.getPositionName());
        recruitment.setTaskDescription(dto.getTaskDescription());
        recruitment.setCollegePreference(dto.getCollegePreference());
        recruitment.setMajorPreference(dto.getMajorPreference());
        recruitment.setQuestionContent(dto.getQuestionContent());
        recruitment.setRecruitCount(dto.getRecruitCount());
        recruitment.setStatus(RECRUITMENT_STATUS_OPEN);
        recruitment.setCreateTime(LocalDateTime.now());
        recruitment.setUpdateTime(LocalDateTime.now());
        teamRecruitmentMapper.insert(recruitment);

        if (publisher != null && publisher.getEmail() != null && !publisher.getEmail().isBlank()) {
            String subject = "【创新平台】您已成功发布团队招募";
            String content = "您好，\n\n您已成功发布团队《" + team.getName() + "》的招募职位：" + dto.getPositionName() + "。\n\n请登录创新平台查看详情。\n";
            emailService.sendMail(publisher.getEmail(), subject, content);
        }

        return recruitment;
    }

    @Transactional
    public TeamRecruitment closeRecruitment(Long recruitmentId, Long userId) {
        TeamRecruitment recruitment = teamRecruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        Team team = getExistingTeam(recruitment.getTeamId());
        if (!canManageTeamRecruitment(team, userId)) {
            throw new RuntimeException("只有团队负责人可以停止招募");
        }
        recruitment.setStatus("CLOSED");
        recruitment.setUpdateTime(LocalDateTime.now());
        teamRecruitmentMapper.update(recruitment);
        return teamRecruitmentMapper.selectById(recruitmentId);
    }

    @Transactional
    public TeamRecruitment reopenRecruitment(Long recruitmentId, Long userId) {
        TeamRecruitment recruitment = teamRecruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        Team team = getExistingTeam(recruitment.getTeamId());
        if (!canManageTeamRecruitment(team, userId)) {
            throw new RuntimeException("只有团队负责人可以重新开启招募");
        }
        recruitment.setStatus(RECRUITMENT_STATUS_OPEN);
        recruitment.setUpdateTime(LocalDateTime.now());
        teamRecruitmentMapper.update(recruitment);
        return teamRecruitmentMapper.selectById(recruitmentId);
    }

    public List<TeamRecruitment> getRecruitments(Long teamId) {
        getExistingTeam(teamId);
        return teamRecruitmentMapper.selectByTeamId(teamId);
    }

    @Transactional
    public void deleteRecruitment(Long recruitmentId, Long userId) {
        TeamRecruitment recruitment = teamRecruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        Team team = getExistingTeam(recruitment.getTeamId());
        if (!canManageTeamRecruitment(team, userId)) {
            throw new RuntimeException("只有团队负责人可以删除招募");
        }
        teamRecruitmentMapper.deleteById(recruitmentId);
    }

    public List<TeamRecruitment> getOpenRecruitments() {
        return teamRecruitmentMapper.selectByStatus(RECRUITMENT_STATUS_OPEN);
    }

    @Transactional
    public TeamRecruitmentApplication applyToRecruitment(Long recruitmentId,
                                                         TeamRecruitmentApplicationDTO dto,
                                                         Long applicantId) {
        User applicant = getExistingUser(applicantId);

        TeamRecruitment recruitment = teamRecruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        if (!RECRUITMENT_STATUS_OPEN.equals(recruitment.getStatus())) {
            throw new RuntimeException("该招募已关闭");
        }

        Team team = getExistingTeam(recruitment.getTeamId());
        if (team.getLeaderId() != null && team.getLeaderId().equals(applicantId)) {
            throw new RuntimeException("团队负责人不能申请自己的招募");
        }
        ensureNotActiveTeamMember(team, applicantId);

        if (recruitment.getQuestionContent() != null
                && !recruitment.getQuestionContent().isBlank()
                && (dto.getAnswerContent() == null || dto.getAnswerContent().isBlank())) {
            throw new RuntimeException("请先回答招募问题");
        }

        List<TeamRecruitmentApplication> existingApplications =
                teamRecruitmentApplicationMapper.selectByRecruitmentIdAndApplicantId(recruitmentId, applicantId);
        for (TeamRecruitmentApplication application : existingApplications) {
            if (ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
                throw new RuntimeException("您已有待审批的招募申请");
            }
            if (ApprovalStatus.APPROVED.getCode().equals(application.getApprovalStatus())) {
                throw new RuntimeException("您的招募申请已通过，无需重复提交");
            }
        }

        TeamRecruitmentApplication application = new TeamRecruitmentApplication();
        application.setRecruitmentId(recruitmentId);
        application.setTeamId(recruitment.getTeamId());
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
        teamRecruitmentApplicationMapper.insert(application);
        return teamRecruitmentApplicationMapper.selectById(application.getId());
    }

    public List<TeamRecruitmentApplication> getRecruitmentApplications(Long teamId, Long userId, String role) {
        Team team = getExistingTeam(teamId);
        if (canViewTeamApplications(team, userId, role)) {
            return teamRecruitmentApplicationMapper.selectByTeamId(teamId);
        }
        return teamRecruitmentApplicationMapper.selectByTeamIdAndApplicantId(teamId, userId);
    }

    @Transactional
    public TeamRecruitmentApplication updateRecruitmentApplication(Long applicationId, TeamRecruitmentApplicationDTO dto, Long userId) {
        TeamRecruitmentApplication application = teamRecruitmentApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("招募申请不存在");
        }
        if (!application.getApplicantId().equals(userId)) {
            throw new RuntimeException("只能修改自己的招募申请");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("当前招募申请无法修改");
        }
        if (dto.getDesiredPosition() != null) {
            application.setDesiredPosition(dto.getDesiredPosition());
        }
        if (dto.getApplicantMajor() != null) {
            application.setApplicantMajor(dto.getApplicantMajor());
        }
        if (dto.getQualifications() != null) {
            application.setQualifications(dto.getQualifications());
        }
        if (dto.getAnswerContent() != null) {
            application.setAnswerContent(dto.getAnswerContent());
        }
        if (dto.getResumeUrl() != null) {
            application.setResumeUrl(dto.getResumeUrl());
        }
        if (dto.getRemark() != null) {
            application.setRemark(dto.getRemark());
        }
        application.setUpdateTime(LocalDateTime.now());
        teamRecruitmentApplicationMapper.update(application);
        return teamRecruitmentApplicationMapper.selectById(applicationId);
    }

    public List<TeamRecruitmentApplication> getMyRecruitmentApplications(Long applicantId) {
        return teamRecruitmentApplicationMapper.selectByApplicantId(applicantId);
    }

    @Transactional
    public TeamRecruitmentApplication withdrawRecruitmentApplication(Long applicationId, Long userId) {
        TeamRecruitmentApplication application = teamRecruitmentApplicationMapper.selectById(applicationId);
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
        teamRecruitmentApplicationMapper.update(application);
        return teamRecruitmentApplicationMapper.selectById(applicationId);
    }

    @Transactional
    public TeamRecruitmentApplication reviewRecruitmentApplication(Long applicationId,
                                                                   com.abajin.innovation.dto.ProjectDockingReviewDTO dto,
                                                                   Long approverId) {
        TeamRecruitmentApplication application = teamRecruitmentApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("招募申请不存在");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该招募申请已处理");
        }

        TeamRecruitment recruitment = teamRecruitmentMapper.selectById(application.getRecruitmentId());
        if (recruitment == null) {
            throw new RuntimeException("招募不存在");
        }
        Team team = getExistingTeam(recruitment.getTeamId());
        if (!canManageTeamRecruitment(team, approverId)) {
            throw new RuntimeException("只有团队负责人可以审批招募申请");
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
        teamRecruitmentApplicationMapper.update(application);

        if (approvalStatus == ApprovalStatus.APPROVED) {
            attachApplicantToTeam(team, application.getApplicantId(), application.getApplicantName());
        }

        User applicant = userMapper.selectById(application.getApplicantId());
        if (applicant != null && applicant.getEmail() != null && !applicant.getEmail().isBlank()) {
            String result = approvalStatus == ApprovalStatus.APPROVED ? "已通过" : "未通过";
            String subject = "【创新平台】您的团队招募申请审批结果：" + result;
            StringBuilder content = new StringBuilder();
            content.append("您好，\n\n您提交加入团队《").append(team.getName()).append("》的招募申请审批结果：").append(result).append("\n");
            if (dto.getApprovalComment() != null && !dto.getApprovalComment().isBlank()) {
                content.append("审批意见：").append(dto.getApprovalComment()).append("\n");
            }
            content.append("\n请登录创新平台查看详情。\n");
            emailService.sendMail(applicant.getEmail(), subject, content.toString());
        }

        return teamRecruitmentApplicationMapper.selectById(applicationId);
    }

    private Team getExistingTeam(Long teamId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        return team;
    }

    private User getExistingUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    private boolean canManageTeamRecruitment(Team team, Long userId) {
        return team.getLeaderId() != null && team.getLeaderId().equals(userId);
    }

    private boolean canViewTeamApplications(Team team, Long userId, String role) {
        return canManageTeamRecruitment(team, userId);
    }

    private void ensureNotActiveTeamMember(Team team, Long applicantId) {
        TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(team.getId(), applicantId);
        if (existingMember != null && MemberStatus.ACTIVE.getCode().equals(existingMember.getStatus())) {
            throw new RuntimeException("您已是该团队成员");
        }
    }

    private void attachApplicantToTeam(Team team, Long applicantId, String applicantName) {
        TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(team.getId(), applicantId);
        if (existingMember == null) {
            TeamMember member = new TeamMember();
            member.setTeamId(team.getId());
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
}

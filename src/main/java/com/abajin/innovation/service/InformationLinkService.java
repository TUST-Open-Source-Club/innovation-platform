package com.abajin.innovation.service;

import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.entity.FundApplication;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.Team;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.FundApplicationMapper;
import com.abajin.innovation.mapper.ProjectMapper;
import com.abajin.innovation.mapper.TeamMapper;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 信息对接服务类
 */
@Service
public class InformationLinkService {
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private FundApplicationMapper fundApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 学生和教师申请加入团队（需要队长审批），可携带申请信息
     */
    @Transactional
    public TeamMember applyJoinTeam(Long teamId, Long userId, Map<String, Object> applyData) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查是否已经是成员或已有申请
        TeamMember existing = teamMemberMapper.selectByTeamIdAndUserId(teamId, userId);
        if (existing != null) {
            if ("APPROVED".equals(existing.getApprovalStatus())) {
                throw new RuntimeException("您已经是该团队成员");
            } else if ("PENDING".equals(existing.getApprovalStatus())) {
                throw new RuntimeException("您已提交申请，请等待队长审批");
            } else if ("REJECTED".equals(existing.getApprovalStatus())) {
                // 如果之前被拒绝，可以重新申请，更新申请信息
                existing.setApprovalStatus("PENDING");
                existing.setJoinTime(LocalDateTime.now());
                fillApplyData(existing, user.getRealName(), applyData);
                teamMemberMapper.update(existing);
                return existing;
            }
        }

        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setUserName(user.getRealName());
        member.setRole("MEMBER");
        member.setApprovalStatus("PENDING");
        member.setStatus("ACTIVE");
        member.setJoinTime(LocalDateTime.now());
        fillApplyData(member, user.getRealName(), applyData);
        teamMemberMapper.insert(member);
        return member;
    }

    private void fillApplyData(TeamMember member, String defaultName, Map<String, Object> applyData) {
        if (applyData == null) return;
        if (applyData.get("realName") != null) member.setUserName(String.valueOf(applyData.get("realName")));
        else if (member.getUserName() == null) member.setUserName(defaultName);
        if (applyData.get("studentId") != null) member.setStudentId(String.valueOf(applyData.get("studentId")));
        if (applyData.get("grade") != null) member.setGrade(String.valueOf(applyData.get("grade")));
        if (applyData.get("major") != null) member.setMajor(String.valueOf(applyData.get("major")));
        if (applyData.get("competitionExperience") != null) member.setCompetitionExperience(String.valueOf(applyData.get("competitionExperience")));
        if (applyData.get("awards") != null) member.setAwards(String.valueOf(applyData.get("awards")));
        if (applyData.get("contactPhone") != null) member.setContactPhone(String.valueOf(applyData.get("contactPhone")));
        if (applyData.get("resumeAttachment") != null) member.setResumeAttachment(String.valueOf(applyData.get("resumeAttachment")));
    }

    /**
     * 教师申请接管无人管理项目（学院管理员审核）
     */
    @Transactional
    public Project applyTakeoverProject(Long projectId, Long teacherId, Long collegeAdminId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (project.getLeaderId() != null) {
            throw new RuntimeException("项目已有负责人，不能接管");
        }

        User teacher = userMapper.selectById(teacherId);
        if (teacher == null || (
                !"TEACHER".equals(teacher.getRole())
                && !"STUDENT_ADMIN".equals(teacher.getRole())
                && !"COLLEGE_ADMIN".equals(teacher.getRole())
                && !"SCHOOL_ADMIN".equals(teacher.getRole()))) {
            throw new RuntimeException("只有教师或管理员可以申请接管项目");
        }

        // 设置申请接管的教师为待审核负责人，由学院/学校管理员在项目审核中审批
        project.setLeaderId(teacherId);
        project.setLeaderName(teacher.getRealName());
        // 项目状态进入待审核
        project.setStatus("PENDING");
        project.setApprovalStatus(ApprovalStatus.PENDING.name());
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.clearPreviousLeader(projectId);
        projectMapper.update(project);

        return project;
    }

    /**
     * 项目负责人发起基金申请（学校管理员审核）
     */
    @Transactional
    public FundApplication createFundApplication(FundApplication application, Long projectLeaderId) {
        Project project = projectMapper.selectById(application.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getLeaderId().equals(projectLeaderId)) {
            throw new RuntimeException("只有项目负责人可以申请基金");
        }

        User applicant = userMapper.selectById(projectLeaderId);
        application.setApplicantId(projectLeaderId);
        application.setApplicantName(applicant.getRealName());
        application.setApplicantType("PROJECT");
        application.setApplicantEntityId(application.getProjectId());
        application.setStatus("SUBMITTED");
        application.setApprovalStatus(ApprovalStatus.PENDING.name());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        fundApplicationMapper.insert(application);
        return application;
    }

    /**
     * 学校管理员审核基金申请
     */
    @Transactional
    public FundApplication reviewFundApplication(Long applicationId, String approvalStatus, String reviewComment, Long reviewerId) {
        FundApplication application = fundApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("基金申请不存在");
        }
        if (!ApprovalStatus.PENDING.name().equals(application.getApprovalStatus())) {
            throw new RuntimeException("该申请已审核");
        }

        application.setApprovalStatus(approvalStatus);
        application.setReviewComment(reviewComment);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());

        if (ApprovalStatus.APPROVED.name().equals(approvalStatus)) {
            application.setStatus("FUNDED");
        } else {
            application.setStatus("REJECTED");
        }

        application.setUpdateTime(LocalDateTime.now());
        fundApplicationMapper.update(application);
        return application;
    }

    /**
     * 项目负责人招募成员
     */
    @Transactional
    public TeamMember recruitMember(Long projectId, Long userId, Long leaderId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getLeaderId().equals(leaderId)) {
            throw new RuntimeException("只有项目负责人可以招募成员");
        }

        Long teamId = project.getTeamId();
        if (teamId == null) {
            throw new RuntimeException("项目未关联团队");
        }

        // 检查是否已经是成员
        TeamMember existing = teamMemberMapper.selectByTeamIdAndUserId(teamId, userId);
        if (existing != null) {
            throw new RuntimeException("该用户已经是团队成员");
        }

        User user = userMapper.selectById(userId);
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setUserName(user.getRealName());
        member.setRole("MEMBER");
        member.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(member);

        // 更新团队成员数量
        Team team = teamMapper.selectById(teamId);
        team.setMemberCount(team.getMemberCount() + 1);
        teamMapper.update(team);

        return member;
    }
}

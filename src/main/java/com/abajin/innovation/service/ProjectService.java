package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.ProjectMapper;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目服务类
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Transactional
    public Project createProject(Project project, Long leaderId) {
        User leader = userMapper.selectById(leaderId);
        if (leader == null) {
            throw new RuntimeException("用户不存在");
        }

        project.setLeaderId(leaderId);
        project.setLeaderName(leader.getRealName());
        project.setStatus(Constants.PROJECT_STATUS_DRAFT);
        LocalDateTime now = LocalDateTime.now();
        project.setCreateTime(now);
        project.setUpdateTime(LocalDateTime.now());
        if (project.getApprovalStatus() == null) {
            project.setApprovalStatus("PENDING");
        }
        if (project.getStartTime() != null && project.getEndTime() != null && project.getEndTime().isBefore(project.getStartTime())) {
            throw new RuntimeException("结束时间不得早于开始时间");
        }
        if (project.getEndTime() != null && project.getEndTime().isBefore(now)) {
            throw new RuntimeException("结束时间不能小于创建时间");
        }

        projectMapper.insert(project);
        return project;
    }

    @Transactional
    public Project updateProject(Long id, Project project, Long userId) {
        Project existingProject = projectMapper.selectById(id);
        if (existingProject == null) {
            throw new RuntimeException("项目不存在");
        }
        if (existingProject.getLeaderId() == null || !existingProject.getLeaderId().equals(userId)) {
            throw new RuntimeException("无权修改此项目");
        }
        // 非草稿状态下不允许通过此接口修改状态
        if (!Constants.PROJECT_STATUS_DRAFT.equals(existingProject.getStatus())) {
            project.setStatus(null);
        }
        // 编辑基本信息时不允许改动负责人与上一任负责人信息，保持原值
        project.setLeaderId(existingProject.getLeaderId());
        project.setLeaderName(existingProject.getLeaderName());
        project.setPreviousLeaderId(existingProject.getPreviousLeaderId());
        project.setPreviousLeaderName(existingProject.getPreviousLeaderName());
        project.setPreviousLeaderPhone(existingProject.getPreviousLeaderPhone());
        if (project.getStartTime() != null && project.getEndTime() != null && project.getEndTime().isBefore(project.getStartTime())) {
            throw new RuntimeException("结束时间不得早于开始时间");
        }
        if (project.getEndTime() != null && existingProject.getCreateTime() != null && project.getEndTime().isBefore(existingProject.getCreateTime())) {
            throw new RuntimeException("结束时间不能小于创建时间");
        }
        project.setId(id);
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.update(project);
        return projectMapper.selectById(id);
    }

    @Transactional
    public Project submitProject(Long id, Long userId) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (project.getLeaderId() == null || !project.getLeaderId().equals(userId)) {
            throw new RuntimeException("无权提交此项目");
        }
        if (!Constants.PROJECT_STATUS_DRAFT.equals(project.getStatus())) {
            throw new RuntimeException("只能提交草稿状态的项目");
        }

        project.setStatus(Constants.PROJECT_STATUS_PENDING);
        project.setApprovalStatus("PENDING");
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.update(project);
        return project;
    }

    @Transactional
    public Project reviewProject(Long id, String status, String reviewComment, Long reviewerId) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        User reviewer = userMapper.selectById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("审核人不存在");
        }
        String reviewerRole = reviewer.getRole();

        if (!"PENDING".equals(project.getApprovalStatus())) {
            throw new RuntimeException("该项目已审核");
        }

        if (!Constants.ROLE_COLLEGE_ADMIN.equals(reviewerRole) && !Constants.ROLE_SCHOOL_ADMIN.equals(reviewerRole)) {
            throw new RuntimeException("无权审核项目");
        }

        if (!"APPROVED".equals(status) && !Constants.PROJECT_STATUS_REJECTED.equals(status)) {
            throw new RuntimeException("审核操作只能设置为 APPROVED 或 REJECTED");
        }

        project.setReviewComment(reviewComment);
        project.setReviewerId(reviewerId);
        project.setReviewTime(LocalDateTime.now());

        if (Constants.ROLE_COLLEGE_ADMIN.equals(reviewerRole)) {
            if (!Constants.PROJECT_STATUS_PENDING.equals(project.getStatus())) {
                throw new RuntimeException("只能审核待学院审核的项目");
            }
            if (Constants.PROJECT_STATUS_APPROVED.equals(status)) {
                project.setStatus(Constants.PROJECT_STATUS_APPROVED);
                project.setApprovalStatus("PENDING");
            } else {
                project.setStatus(Constants.PROJECT_STATUS_REJECTED);
                project.setApprovalStatus("REJECTED");
            }
        } else {
            if (!Constants.PROJECT_STATUS_APPROVED.equals(project.getStatus()) || !"PENDING".equals(project.getApprovalStatus())) {
                throw new RuntimeException("只能审核学院已通过、待学校审核的项目");
            }
            if (Constants.PROJECT_STATUS_APPROVED.equals(status)) {
                project.setStatus(Constants.PROJECT_STATUS_APPROVED);
                project.setApprovalStatus("APPROVED");
                project.setStartTime(LocalDateTime.now());
            } else {
                project.setStatus(Constants.PROJECT_STATUS_REJECTED);
                project.setApprovalStatus("REJECTED");
            }
        }

        project.setUpdateTime(LocalDateTime.now());
        projectMapper.update(project);
        return project;
    }

    public Project getProjectById(Long id) {
        return projectMapper.selectById(id);
    }

    public List<Project> getProjectsByLeaderId(Long leaderId) {
        return projectMapper.selectByLeaderId(leaderId);
    }

    public List<Project> getProjectsByStatus(String status) {
        return projectMapper.selectByStatus(status);
    }

    public List<Project> getProjectsByStatusAndApprovalStatus(String status, String approvalStatus) {
        return projectMapper.selectByStatusAndApprovalStatus(status, approvalStatus);
    }

    public List<Project> getAllProjects() {
        return projectMapper.selectAll();
    }

    /**
     * 根据团队ID查询关联的项目（项目负责人与团队负责人关联）
     */
    public List<Project> getProjectsByTeamId(Long teamId) {
        return projectMapper.selectByTeamId(teamId);
    }

    /**
     * 无人接管项目列表（负责人虚位以待）
     */
    public List<Project> getUnclaimedProjects() {
        return projectMapper.selectUnclaimed();
    }

    /**
     * 更换负责人：将负责人身份转给项目关联团队的成员
     */
    @Transactional
    public Project transferLeader(Long projectId, Long newLeaderUserId, Long currentLeaderId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (project.getLeaderId() == null || !project.getLeaderId().equals(currentLeaderId)) {
            throw new RuntimeException("只有项目负责人可以更换负责人");
        }
        if (project.getTeamId() == null) {
            throw new RuntimeException("项目未关联团队，无法从成员中更换负责人");
        }
        TeamMember member = teamMemberMapper.selectByTeamIdAndUserId(project.getTeamId(), newLeaderUserId);
        if (member == null || !"APPROVED".equals(member.getApprovalStatus())) {
            throw new RuntimeException("只能将负责人转给已通过审批的团队成员");
        }
        User newLeader = userMapper.selectById(newLeaderUserId);
        if (newLeader == null) {
            throw new RuntimeException("新负责人用户不存在");
        }
        project.setLeaderId(newLeaderUserId);
        project.setLeaderName(newLeader.getRealName());
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.clearPreviousLeader(projectId);
        projectMapper.update(project);
        return projectMapper.selectById(projectId);
    }

    /**
     * 招募负责人：负责人虚位以待，保留上一任联系方式，项目进入无人接管列表
     */
    @Transactional
    public Project vacateLeader(Long projectId, Long currentLeaderId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (project.getLeaderId() == null || !project.getLeaderId().equals(currentLeaderId)) {
            throw new RuntimeException("只有项目负责人可以发起招募负责人");
        }
        User currentLeader = userMapper.selectById(currentLeaderId);
        project.setPreviousLeaderId(currentLeaderId);
        project.setPreviousLeaderName(currentLeader.getRealName());
        project.setPreviousLeaderPhone(currentLeader.getPhone() != null ? currentLeader.getPhone() : "");
        project.setLeaderId(null);
        project.setLeaderName(null);
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.update(project);
        return projectMapper.selectById(projectId);
    }

    /**
     * 逻辑删除项目：设置 is_deleted = 1，保留原状态不变
     * 仅系统管理员可删除
     */
    @Transactional
    public void logicalDeleteProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        // 进行中或已完成的项目不允许删除
        if (Constants.PROJECT_STATUS_IN_PROGRESS.equals(project.getStatus()) ||
            Constants.PROJECT_STATUS_COMPLETED.equals(project.getStatus())) {
            throw new RuntimeException("进行中或已完成的项目不允许删除");
        }

        // 软删除：设置 is_deleted = 1，保留原状态不变
        project.setIsDeleted(1);
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.update(project);
    }
}

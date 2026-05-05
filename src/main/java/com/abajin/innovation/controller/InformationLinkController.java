package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.ProjectApplicationDTO;
import com.abajin.innovation.dto.ProjectDockingReviewDTO;
import com.abajin.innovation.dto.ProjectRecruitmentApplicationDTO;
import com.abajin.innovation.dto.ProjectRecruitmentDTO;
import com.abajin.innovation.entity.FundApplication;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.ProjectApplication;
import com.abajin.innovation.entity.ProjectRecruitment;
import com.abajin.innovation.entity.ProjectRecruitmentApplication;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.service.InformationLinkService;
import com.abajin.innovation.service.ProjectDockingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 信息对接控制器
 */
@RestController
@RequestMapping("/information-link")
public class InformationLinkController {
    @Autowired
    private InformationLinkService informationLinkService;

    @Autowired
    private ProjectDockingService projectDockingService;

    /**
     * 学生和教师申请加入团队
     * POST /api/information-link/teams/{teamId}/apply
     */
    @PostMapping("/teams/{teamId}/apply")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER, Constants.ROLE_STUDENT_ADMIN})
    public Result<TeamMember> applyJoinTeam(
            @PathVariable Long teamId,
            @RequestAttribute("userId") Long userId,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            TeamMember member = informationLinkService.applyJoinTeam(teamId, userId, body);
            return Result.success("申请已提交，请等待队长审批", member);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 教师申请接管无人管理项目
     * POST /api/information-link/projects/{projectId}/takeover
     */
    @PostMapping("/projects/{projectId}/takeover")
    @RequiresRole(value = {Constants.ROLE_TEACHER})
    public Result<Project> applyTakeoverProject(
            @PathVariable Long projectId,
            @RequestAttribute("userId") Long userId) {
        try {
            // 这里简化处理，实际应该由学院管理员审核
            Project project = informationLinkService.applyTakeoverProject(projectId, userId, null);
            return Result.success("申请接管项目成功", project);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 项目负责人发起基金申请
     * POST /api/information-link/fund-applications
     */
    @PostMapping("/fund-applications")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER, Constants.ROLE_STUDENT_ADMIN})
    public Result<FundApplication> createFundApplication(
            @Valid @RequestBody FundApplication application,
            @RequestAttribute("userId") Long userId) {
        try {
            FundApplication created = informationLinkService.createFundApplication(application, userId);
            return Result.success("基金申请提交成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 学校管理员审核基金申请
     * POST /api/information-link/fund-applications/{id}/review
     */
    @PostMapping("/fund-applications/{id}/review")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = false)
    public Result<FundApplication> reviewFundApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            String reviewComment = reviewData.get("reviewComment");
            FundApplication reviewed = informationLinkService.reviewFundApplication(id, approvalStatus, reviewComment, userId);
            return Result.success("审核完成", reviewed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 项目负责人招募成员
     * POST /api/information-link/projects/{projectId}/recruit
     */
    @PostMapping("/projects/{projectId}/recruit")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER, Constants.ROLE_STUDENT_ADMIN})
    public Result<TeamMember> recruitMember(
            @PathVariable Long projectId,
            @RequestBody Map<String, Long> data,
            @RequestAttribute("userId") Long userId) {
        try {
            Long memberId = data.get("userId");
            TeamMember member = informationLinkService.recruitMember(projectId, memberId, userId);
            return Result.success("招募成员成功", member);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 主动申请加入项目
     * POST /api/information-link/projects/{projectId}/applications
     */
    @PostMapping("/projects/{projectId}/applications")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN})
    public Result<ProjectApplication> applyToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectApplicationDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            dto.setProjectId(projectId);
            ProjectApplication application = projectDockingService.applyToProject(projectId, dto, userId);
            return Result.success("申请已提交", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看项目主动申请
     * GET /api/information-link/projects/{projectId}/applications
     */
    @GetMapping("/projects/{projectId}/applications")
    public Result<List<ProjectApplication>> getProjectApplications(
            @PathVariable Long projectId,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            List<ProjectApplication> applications = projectDockingService.getProjectApplications(projectId, userId, role);
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 撤回主动申请
     * POST /api/information-link/project-applications/{applicationId}/withdraw
     */
    @PostMapping("/project-applications/{applicationId}/withdraw")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN})
    public Result<ProjectApplication> withdrawProjectApplication(
            @PathVariable Long applicationId,
            @RequestAttribute("userId") Long userId) {
        try {
            ProjectApplication application = projectDockingService.withdrawProjectApplication(applicationId, userId);
            return Result.success("撤回成功", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审批主动申请
     * POST /api/information-link/project-applications/{applicationId}/review
     */
    @PostMapping("/project-applications/{applicationId}/review")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<ProjectApplication> reviewProjectApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ProjectDockingReviewDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            ProjectApplication application = projectDockingService.reviewProjectApplication(applicationId, dto, userId);
            return Result.success("审批完成", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布项目招募
     * POST /api/information-link/projects/{projectId}/recruitments
     */
    @PostMapping("/projects/{projectId}/recruitments")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<ProjectRecruitment> createRecruitment(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRecruitmentDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            ProjectRecruitment recruitment = projectDockingService.createRecruitment(projectId, dto, userId);
            return Result.success("发布招募成功", recruitment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看项目招募
     * GET /api/information-link/projects/{projectId}/recruitments
     */
    @GetMapping("/projects/{projectId}/recruitments")
    public Result<List<ProjectRecruitment>> getRecruitments(@PathVariable Long projectId) {
        try {
            return Result.success(projectDockingService.getRecruitments(projectId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除项目招募
     * DELETE /api/information-link/recruitments/{recruitmentId}
     */
    @DeleteMapping("/recruitments/{recruitmentId}")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<Void> deleteRecruitment(
            @PathVariable Long recruitmentId,
            @RequestAttribute("userId") Long userId) {
        try {
            projectDockingService.deleteRecruitment(recruitmentId, userId);
            return Result.success("删除招募成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据招募申请加入
     * POST /api/information-link/recruitments/{recruitmentId}/applications
     */
    @PostMapping("/recruitments/{recruitmentId}/applications")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN})
    public Result<ProjectRecruitmentApplication> applyToRecruitment(
            @PathVariable Long recruitmentId,
            @Valid @RequestBody ProjectRecruitmentApplicationDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            ProjectRecruitmentApplication application =
                    projectDockingService.applyToRecruitment(recruitmentId, dto, userId);
            return Result.success("招募申请已提交", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看项目下的招募申请
     * GET /api/information-link/projects/{projectId}/recruitment-applications
     */
    @GetMapping("/projects/{projectId}/recruitment-applications")
    public Result<List<ProjectRecruitmentApplication>> getRecruitmentApplications(
            @PathVariable Long projectId,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            List<ProjectRecruitmentApplication> applications =
                    projectDockingService.getRecruitmentApplications(projectId, userId, role);
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 撤回招募申请
     * POST /api/information-link/recruitment-applications/{applicationId}/withdraw
     */
    @PostMapping("/recruitment-applications/{applicationId}/withdraw")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN})
    public Result<ProjectRecruitmentApplication> withdrawRecruitmentApplication(
            @PathVariable Long applicationId,
            @RequestAttribute("userId") Long userId) {
        try {
            ProjectRecruitmentApplication application =
                    projectDockingService.withdrawRecruitmentApplication(applicationId, userId);
            return Result.success("撤回成功", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审批招募申请
     * POST /api/information-link/recruitment-applications/{applicationId}/review
     */
    @PostMapping("/recruitment-applications/{applicationId}/review")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<ProjectRecruitmentApplication> reviewRecruitmentApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ProjectDockingReviewDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            ProjectRecruitmentApplication application =
                    projectDockingService.reviewRecruitmentApplication(applicationId, dto, userId);
            return Result.success("审批完成", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

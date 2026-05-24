package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.ProjectDockingReviewDTO;
import com.abajin.innovation.dto.TeamRecruitmentApplicationDTO;
import com.abajin.innovation.dto.TeamRecruitmentDTO;
import com.abajin.innovation.entity.TeamRecruitment;
import com.abajin.innovation.entity.TeamRecruitmentApplication;
import com.abajin.innovation.service.TeamRecruitmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 团队招募控制器
 */
@RestController
@RequestMapping("/team-recruitments")
public class TeamRecruitmentController {

    @Autowired
    private TeamRecruitmentService teamRecruitmentService;

    /**
     * 发布团队招募
     * POST /api/team-recruitments/teams/{teamId}/recruitments
     */
    @PostMapping("/teams/{teamId}/recruitments")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitment> createRecruitment(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamRecruitmentDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitment recruitment = teamRecruitmentService.createRecruitment(teamId, dto, userId);
            return Result.success("发布招募成功", recruitment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看团队招募
     * GET /api/team-recruitments/teams/{teamId}/recruitments
     */
    @GetMapping("/teams/{teamId}/recruitments")
    public Result<List<TeamRecruitment>> getRecruitments(@PathVariable Long teamId) {
        try {
            return Result.success(teamRecruitmentService.getRecruitments(teamId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除团队招募
     * DELETE /api/team-recruitments/recruitments/{recruitmentId}
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
            teamRecruitmentService.deleteRecruitment(recruitmentId, userId);
            return Result.success("删除招募成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 停止团队招募
     * POST /api/team-recruitments/recruitments/{recruitmentId}/close
     */
    @PostMapping("/recruitments/{recruitmentId}/close")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitment> closeRecruitment(
            @PathVariable Long recruitmentId,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitment recruitment = teamRecruitmentService.closeRecruitment(recruitmentId, userId);
            return Result.success("招募已停止", recruitment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重新开启团队招募
     * POST /api/team-recruitments/recruitments/{recruitmentId}/reopen
     */
    @PostMapping("/recruitments/{recruitmentId}/reopen")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitment> reopenRecruitment(
            @PathVariable Long recruitmentId,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitment recruitment = teamRecruitmentService.reopenRecruitment(recruitmentId, userId);
            return Result.success("招募已重新开启", recruitment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看所有开放的团队招募
     * GET /api/team-recruitments/recruitments/open
     */
    @GetMapping("/recruitments/open")
    public Result<List<TeamRecruitment>> getOpenRecruitments() {
        try {
            return Result.success(teamRecruitmentService.getOpenRecruitments());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据团队招募申请加入
     * POST /api/team-recruitments/recruitments/{recruitmentId}/applications
     */
    @PostMapping("/recruitments/{recruitmentId}/applications")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitmentApplication> applyToRecruitment(
            @PathVariable Long recruitmentId,
            @Valid @RequestBody TeamRecruitmentApplicationDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitmentApplication application =
                    teamRecruitmentService.applyToRecruitment(recruitmentId, dto, userId);
            return Result.success("招募申请已提交", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看团队下的招募申请
     * GET /api/team-recruitments/teams/{teamId}/applications
     */
    @GetMapping("/teams/{teamId}/applications")
    public Result<List<TeamRecruitmentApplication>> getRecruitmentApplications(
            @PathVariable Long teamId,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            List<TeamRecruitmentApplication> applications =
                    teamRecruitmentService.getRecruitmentApplications(teamId, userId, role);
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看我的所有团队招募申请
     * GET /api/team-recruitments/my/applications
     */
    @GetMapping("/my/applications")
    public Result<List<TeamRecruitmentApplication>> getMyRecruitmentApplications(
            @RequestAttribute("userId") Long userId) {
        try {
            return Result.success(teamRecruitmentService.getMyRecruitmentApplications(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改团队招募申请
     * PUT /api/team-recruitments/applications/{applicationId}
     */
    @PutMapping("/applications/{applicationId}")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitmentApplication> updateRecruitmentApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody TeamRecruitmentApplicationDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitmentApplication application = teamRecruitmentService.updateRecruitmentApplication(applicationId, dto, userId);
            return Result.success("修改成功", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 撤回团队招募申请
     * POST /api/team-recruitments/applications/{applicationId}/withdraw
     */
    @PostMapping("/applications/{applicationId}/withdraw")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitmentApplication> withdrawRecruitmentApplication(
            @PathVariable Long applicationId,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitmentApplication application =
                    teamRecruitmentService.withdrawRecruitmentApplication(applicationId, userId);
            return Result.success("撤回成功", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审批团队招募申请
     * POST /api/team-recruitments/applications/{applicationId}/review
     */
    @PostMapping("/applications/{applicationId}/review")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<TeamRecruitmentApplication> reviewRecruitmentApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ProjectDockingReviewDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TeamRecruitmentApplication application =
                    teamRecruitmentService.reviewRecruitmentApplication(applicationId, dto, userId);
            return Result.success("审批完成", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

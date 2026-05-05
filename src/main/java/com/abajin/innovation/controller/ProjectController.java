package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN, Constants.ROLE_TEACHER})
    public Result<Project> createProject(
            @Valid @RequestBody Project project,
            @RequestAttribute("userId") Long userId) {
        try {
            Project created = projectService.createProject(project, userId);
            return Result.success("创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN, Constants.ROLE_TEACHER})
    public Result<Project> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody Project project,
            @RequestAttribute("userId") Long userId) {
        try {
            Project updated = projectService.updateProject(id, project, userId);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/submit")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN, Constants.ROLE_TEACHER})
    public Result<Project> submitProject(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            Project submitted = projectService.submitProject(id, userId);
            return Result.success("提交成功", submitted);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/review")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Project> reviewProject(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String status = reviewData.get("status");
            String reviewComment = reviewData.get("reviewComment");
            Project reviewed = projectService.reviewProject(id, status, reviewComment, userId);
            return Result.success("审核成功", reviewed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Project> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id);
            if (project == null) {
                return Result.error("项目不存在");
            }
            return Result.success(project);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<Project>> getMyProjects(@RequestAttribute("userId") Long userId) {
        try {
            List<Project> projects = projectService.getProjectsByLeaderId(userId);
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<List<Project>> getAllProjects(
            @RequestParam(required = false) String status,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            List<Project> projects;
            if (status != null && !status.isEmpty()) {
                if ("PENDING".equals(status) && (Constants.ROLE_COLLEGE_ADMIN.equals(role) || Constants.ROLE_SCHOOL_ADMIN.equals(role))) {
                    if (Constants.ROLE_COLLEGE_ADMIN.equals(role)) {
                        projects = projectService.getProjectsByStatusAndApprovalStatus("PENDING", "PENDING");
                    } else {
                        // 学校管理员：查看所有待审核的项目（status=PENDING 或 status=APPROVED 且 approvalStatus=PENDING）
                        projects = projectService.getProjectsByApprovalStatus("PENDING");
                    }
                } else {
                    projects = projectService.getProjectsByStatus(status);
                }
            } else {
                projects = projectService.getAllProjects();
            }
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 无人接管项目列表（负责人虚位以待）
     * GET /api/projects/unclaimed
     */
    @GetMapping("/unclaimed")
    public Result<List<Project>> getUnclaimedProjects() {
        try {
            List<Project> projects = projectService.getUnclaimedProjects();
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更换负责人：将负责人转给项目关联团队的成员
     * POST /api/projects/{id}/transfer-leader
     */
    @PostMapping("/{id}/transfer-leader")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN, Constants.ROLE_TEACHER})
    public Result<Project> transferLeader(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body,
            @RequestAttribute("userId") Long userId) {
        try {
            Long newLeaderUserId = body.get("newLeaderUserId");
            if (newLeaderUserId == null) {
                return Result.error("请选择新负责人");
            }
            Project project = projectService.transferLeader(id, newLeaderUserId, userId);
            return Result.success("更换负责人成功", project);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 招募负责人：负责人虚位以待，项目进入无人接管列表
     * POST /api/projects/{id}/vacate-leader
     */
    @PostMapping("/{id}/vacate-leader")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_STUDENT_ADMIN, Constants.ROLE_TEACHER})
    public Result<Project> vacateLeader(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            Project project = projectService.vacateLeader(id, userId);
            return Result.success("已设为虚位以待，项目已进入无人接管列表", project);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 逻辑删除项目（软删除）
     * 仅系统管理员可删除
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = Constants.ROLE_SCHOOL_ADMIN)
    public Result<Void> deleteProject(@PathVariable Long id) {
        try {
            projectService.logicalDeleteProject(id);
            return Result.success("项目已删除", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("系统繁忙，请稍后重试");
        }
    }
}

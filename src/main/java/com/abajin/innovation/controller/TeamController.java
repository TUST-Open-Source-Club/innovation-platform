package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.Team;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.service.ProjectService;
import com.abajin.innovation.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Team> createTeam(
            @Valid @RequestBody Team team,
            @RequestAttribute("userId") Long userId) {
        try {
            Team created = teamService.createTeam(team, userId);
            return Result.success("创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Team> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody Team team,
            @RequestAttribute("userId") Long userId) {
        try {
            Team updated = teamService.updateTeam(id, team, userId);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/members")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Void> addMember(
            @PathVariable Long id,
            @RequestBody Map<String, Long> data,
            @RequestAttribute("userId") Long userId) {
        try {
            Long memberId = data.get("userId");
            teamService.addMember(id, memberId, userId);
            return Result.success("添加成员成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @RequestAttribute("userId") Long userId) {
        try {
            teamService.removeMember(id, memberId, userId);
            return Result.success("移除成员成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Team> getTeamById(
            @PathVariable Long id,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        try {
            Team team = teamService.getTeamById(id, userId);
            if (team == null) {
                return Result.error("团队不存在或无权查看");
            }
            return Result.success(team);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/members")
    public Result<List<TeamMember>> getTeamMembers(@PathVariable Long id) {
        try {
            List<TeamMember> members = teamService.getTeamMembers(id);
            return Result.success(members);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有团队成员（包括待审批的）
     * GET /api/teams/{id}/members/all
     */
    @GetMapping("/{id}/members/all")
    public Result<List<TeamMember>> getAllTeamMembers(@PathVariable Long id) {
        try {
            List<TeamMember> members = teamService.getAllTeamMembers(id);
            return Result.success(members);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<Team>> getMyTeams(@RequestAttribute("userId") Long userId) {
        try {
            List<Team> teams = teamService.getTeamsByLeaderId(userId);
            return Result.success(teams);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<List<Team>> getAllTeams(
            @RequestAttribute(value = "userId", required = false) Long userId) {
        try {
            List<Team> teams = teamService.getAllTeams(userId);
            return Result.success(teams);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出团队列表为 Excel（学院/学校管理员）
     * GET /api/teams/export
     */
    @GetMapping("/export")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public ResponseEntity<byte[]> exportTeamsExcel() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            teamService.exportTeamsToExcel(out);
            byte[] bytes = out.toByteArray();
            String filename = "团队列表_" + System.currentTimeMillis() + ".xlsx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", encodedFilename);
            headers.setContentLength(bytes.length);
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 上传 Excel 批量添加团队（学院/学校管理员）
     * POST /api/teams/import
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Integer> importTeamsExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择 Excel 文件");
            }
            String name = file.getOriginalFilename();
            if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
                return Result.error("请上传 .xlsx 或 .xls 格式的 Excel 文件");
            }
            int count = teamService.importTeamsFromExcel(file.getInputStream());
            return Result.success("成功导入 " + count + " 个团队", count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查用户是否是团队成员
     * GET /api/teams/{id}/members/check?userId={userId}
     */

    /**
     * 获取团队关联的项目列表（team_id 关联）
     * GET /api/teams/{id}/projects
     */
    @GetMapping("/{id}/projects")
    public Result<List<Project>> getTeamProjects(@PathVariable Long id) {
        try {
            List<Project> projects = projectService.getProjectsByTeamId(id);
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/members/check")
    public Result<Boolean> checkIsMember(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            boolean isMember = teamService.isMember(id, userId);
            return Result.success(isMember);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 队长审批团队成员申请
     * POST /api/teams/{id}/members/{memberId}/review
     */
    @PostMapping("/{id}/members/{memberId}/review")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<TeamMember> reviewMemberApplication(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            if (!"APPROVED".equals(approvalStatus) && !"REJECTED".equals(approvalStatus)) {
                return Result.error("审批状态无效");
            }
            TeamMember member = teamService.reviewMemberApplication(id, memberId, approvalStatus, userId);
            String message = "APPROVED".equals(approvalStatus) ? "已通过申请" : "已拒绝申请";
            return Result.success(message, member);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取待审批的成员申请列表
     * GET /api/teams/{id}/members/pending
     */
    @GetMapping("/{id}/members/pending")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<List<TeamMember>> getPendingApplications(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            List<TeamMember> applications = teamService.getPendingApplications(id, userId);
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除团队（管理员）
     * DELETE /api/teams/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Void> deleteTeam(@PathVariable Long id) {
        try {
            teamService.softDeleteTeam(id);
            return Result.success("团队删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除团队（管理员）
     * DELETE /api/teams/batch
     */
    @DeleteMapping("/batch")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Integer> batchDeleteTeams(@RequestBody Map<String, List<Long>> data) {
        try {
            List<Long> ids = data.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的团队");
            }
            int count = teamService.softDeleteTeams(ids);
            return Result.success("成功删除 " + count + " 个团队", count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

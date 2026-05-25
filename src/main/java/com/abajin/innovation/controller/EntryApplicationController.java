package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.EntryApplication;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.service.EntryApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 入驻管理控制器
 */
@RestController
@RequestMapping("/entry-applications")
public class EntryApplicationController {
    @Autowired
    private EntryApplicationService entryApplicationService;

    /**
     * 创建入驻申请（草稿状态）
     * POST /api/entry-applications
     */
    @PostMapping
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<EntryApplication> createEntryApplication(
            @Valid @RequestBody EntryApplication application,
            @RequestAttribute("userId") Long userId) {
        try {
            EntryApplication created = entryApplicationService.createEntryApplication(application, userId);
            return Result.success("入驻申请创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交入驻申请（待审核状态）
     * POST /api/entry-applications/{id}/submit
     */
    @PostMapping("/{id}/submit")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<EntryApplication> submitEntryApplication(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            EntryApplication application = entryApplicationService.submitEntryApplication(id, userId);
            return Result.success("入驻申请已提交", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核入驻申请（学院管理员 + 学校管理员分级审核）
     * POST /api/entry-applications/{id}/review
     */
    @PostMapping("/{id}/review")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<EntryApplication> reviewEntryApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            String reviewComment = reviewData.get("reviewComment");

            // 验证审批状态
            ApprovalStatus status;
            try {
                status = ApprovalStatus.valueOf(approvalStatus);
            } catch (IllegalArgumentException e) {
                return Result.error(400, "审批状态无效，必须为：APPROVED 或 REJECTED");
            }

            if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
                return Result.error(400, "审核操作只能设置为 APPROVED 或 REJECTED");
            }

            EntryApplication reviewed = entryApplicationService.reviewEntryApplication(
                    id, approvalStatus, reviewComment, userId);
            return Result.success("审核完成", reviewed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认入驻（审核通过后，正式入驻）
     * POST /api/entry-applications/{id}/confirm-entry
     */
    @PostMapping("/{id}/confirm-entry")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = false)
    public Result<EntryApplication> confirmEntry(@PathVariable Long id) {
        try {
            EntryApplication application = entryApplicationService.confirmEntry(id);
            return Result.success("入驻确认成功", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出入驻
     * POST /api/entry-applications/{id}/exit
     */
    @PostMapping("/{id}/exit")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = false)
    public Result<EntryApplication> exitEntry(
            @PathVariable Long id,
            @RequestBody Map<String, String> exitData) {
        try {
            String exitReason = exitData.get("exitReason");
            EntryApplication application = entryApplicationService.exitEntry(id, exitReason);
            return Result.success("退出入驻成功", application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询入驻申请列表
     * GET /api/entry-applications?pageNum=1&pageSize=10&teamName=xxx&status=PENDING
     */
    @GetMapping
    public Result<PageResult<EntryApplication>> getEntryApplications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) String applicationType,
            @RequestParam(required = false) Long applicantId) {
        try {
            List<EntryApplication> applications = entryApplicationService.getEntryApplications(
                    pageNum, pageSize, teamName, status, approvalStatus, applicationType, applicantId);
            Long total = (long) entryApplicationService.countEntryApplications(
                    teamName, status, approvalStatus, applicationType, applicantId);
            PageResult<EntryApplication> pageResult = PageResult.of(pageNum, pageSize, total, applications);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询入驻申请详情
     * GET /api/entry-applications/{id}
     */
    @GetMapping("/{id}")
    public Result<EntryApplication> getEntryApplicationById(@PathVariable Long id) {
        try {
            EntryApplication application = entryApplicationService.getEntryApplicationById(id);
            if (application == null) {
                return Result.error(404, "入驻申请不存在");
            }
            return Result.success(application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新入驻申请（仅申请人可更新草稿状态）
     * PUT /api/entry-applications/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<EntryApplication> updateEntryApplication(
            @PathVariable Long id,
            @Valid @RequestBody EntryApplication application,
            @RequestAttribute("userId") Long userId) {
        try {
            EntryApplication updated = entryApplicationService.updateEntryApplication(id, application, userId);
            return Result.success("入驻申请更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除入驻申请（仅申请人可删除草稿状态）
     * DELETE /api/entry-applications/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = {
            Constants.ROLE_STUDENT,
            Constants.ROLE_STUDENT_ADMIN,
            Constants.ROLE_TEACHER,
            Constants.ROLE_COLLEGE_ADMIN,
            Constants.ROLE_SCHOOL_ADMIN
    }, allowAdmin = true)
    public Result<Void> deleteEntryApplication(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            entryApplicationService.deleteEntryApplication(id, userId);
            return Result.success("入驻申请删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询我的入驻申请列表
     * GET /api/entry-applications/my
     */
    @GetMapping("/my")
    public Result<List<EntryApplication>> getMyApplications(@RequestAttribute("userId") Long userId) {
        try {
            List<EntryApplication> applications = entryApplicationService.getMyApplications(userId);
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

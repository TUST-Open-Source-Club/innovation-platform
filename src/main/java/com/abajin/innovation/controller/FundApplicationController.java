package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.FundApplication;
import com.abajin.innovation.service.FundApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 基金申请控制器
 */
@RestController
@RequestMapping("/fund-applications")
public class FundApplicationController {
    @Autowired
    private FundApplicationService fundApplicationService;

    /**
     * 创建基金申请
     * POST /api/fund-applications
     */
    @PostMapping
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<FundApplication> createFundApplication(
            @Valid @RequestBody FundApplication application,
            @RequestAttribute("userId") Long userId) {
        try {
            FundApplication created = fundApplicationService.createFundApplication(application, userId);
            return Result.success("基金申请创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交基金申请
     * POST /api/fund-applications/{id}/submit
     */
    @PostMapping("/{id}/submit")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<FundApplication> submitFundApplication(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            FundApplication submitted = fundApplicationService.submitFundApplication(id, userId);
            return Result.success("基金申请提交成功", submitted);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核基金申请
     * POST /api/fund-applications/{id}/review
     */
    @PostMapping("/{id}/review")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<FundApplication> reviewFundApplication(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = (String) reviewData.get("approvalStatus");
            String reviewComment = (String) reviewData.get("reviewComment");
            BigDecimal approvedAmount = null;
            if (reviewData.get("approvedAmount") != null) {
                if (reviewData.get("approvedAmount") instanceof Number) {
                    approvedAmount = BigDecimal.valueOf(((Number) reviewData.get("approvedAmount")).doubleValue());
                } else if (reviewData.get("approvedAmount") instanceof String) {
                    approvedAmount = new BigDecimal((String) reviewData.get("approvedAmount"));
                }
            }
            FundApplication reviewed = fundApplicationService.reviewFundApplication(
                    id, approvalStatus, reviewComment, approvedAmount, userId);
            return Result.success("审核完成", reviewed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询基金申请详情
     * GET /api/fund-applications/{id}
     */
    @GetMapping("/{id}")
    public Result<FundApplication> getFundApplicationById(@PathVariable Long id) {
        try {
            FundApplication application = fundApplicationService.getFundApplicationById(id);
            if (application == null) {
                return Result.error("基金申请不存在");
            }
            return Result.success(application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询所有基金申请
     * GET /api/fund-applications
     */
    @GetMapping
    public Result<List<FundApplication>> getAllFundApplications(
            @RequestParam(required = false) String approvalStatus,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            List<FundApplication> applications;
            if (!isSchoolOrCollegeAdmin(role)) {
                // 非学校/学院管理员：只能看到自己的基金申请
                if (userId == null) {
                    return Result.error(401, "未登录");
                }
                if (approvalStatus != null && !approvalStatus.isEmpty()) {
                    applications = fundApplicationService.getFundApplicationsByApplicantIdAndApprovalStatus(userId, approvalStatus);
                } else {
                    applications = fundApplicationService.getFundApplicationsByApplicantId(userId);
                }
            } else {
                // 管理员查看：PENDING 需要区分学院待审/学校待审
                if (approvalStatus != null && !approvalStatus.isEmpty()) {
                    if ("PENDING".equals(approvalStatus)) {
                        if (Constants.ROLE_COLLEGE_ADMIN.equals(role)) {
                            applications = fundApplicationService.getFundApplicationsByStatusAndApprovalStatus("SUBMITTED", "PENDING");
                        } else if (Constants.ROLE_SCHOOL_ADMIN.equals(role)) {
                            // 学校管理员查看所有待审核的申请（包括学院待审和学校待审）
                            applications = fundApplicationService.getFundApplicationsByApprovalStatus("PENDING");
                        } else {
                            applications = List.of();
                        }
                    } else {
                        applications = fundApplicationService.getFundApplicationsByApprovalStatus(approvalStatus);
                    }
                } else {
                    applications = fundApplicationService.getAllFundApplications();
                }
            }
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private boolean isSchoolOrCollegeAdmin(String role) {
        return Constants.ROLE_SCHOOL_ADMIN.equals(role) || Constants.ROLE_COLLEGE_ADMIN.equals(role);
    }

    /**
     * 查询我的基金申请
     * GET /api/fund-applications/my
     */
    @GetMapping("/my")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<List<FundApplication>> getMyFundApplications(@RequestAttribute("userId") Long userId) {
        try {
            List<FundApplication> applications = fundApplicationService.getFundApplicationsByApplicantId(userId);
            return Result.success(applications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

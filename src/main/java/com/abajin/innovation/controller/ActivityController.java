package com.abajin.innovation.controller;

import com.abajin.innovation.dto.ActivityDTO;
import com.abajin.innovation.service.ActivityService;
import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.Activity;
import com.abajin.innovation.entity.ActivityRegistration;
import com.abajin.innovation.entity.ActivitySummary;
import com.abajin.innovation.util.MinioUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 活动管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private MinioUtils minioUtils;

    @Value("${minio.bucket:first}")
    private String bucketName;

    @Value("${minio.base-url:http://192.168.147.110:9000}")
    private String minioBaseUrl; // 保留配置兼容，目前不直接拼接 URL

    /**
     * 创建活动申报
     * POST /api/activities
     */
    @PostMapping
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Activity> createActivity(
            @Valid @RequestBody ActivityDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            Activity activity = activityService.createActivity(dto, userId);
            return Result.success("活动申报创建成功", activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新活动申报（组织者或管理员）
     * PUT /api/activities/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER, Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN})
    public Result<Activity> updateActivity(
            @PathVariable Long id,
            @RequestBody ActivityDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            Activity activity = activityService.updateActivity(id, dto, userId);
            return Result.success("活动已更新", activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传活动海报（仅学校管理员、学院管理员）
     * POST /api/activities/{id}/poster
     */
    @PostMapping("/{id}/poster")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN})
    public Result<Activity> uploadPoster(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("userId") Long userId) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择文件");
            }
            String dir = "activity-poster";
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : "";
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;

            // 确保桶存在并上传到 MinIO
            minioUtils.createBucket(bucketName);
            // 路径：second/activity-poster/uuid.ext
            String objectName = "second/" + dir + "/" + filename;
            try (InputStream in = file.getInputStream()) {
                minioUtils.uploadFile(in, bucketName, objectName);
            }
            // 使用预签名 URL 供前端预览
            String posterUrl = minioUtils.getPreviewFileUrl(bucketName, objectName);
            Activity activity = activityService.updateActivityPoster(id, posterUrl, userId);
            return Result.success("海报上传成功", activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交活动申报（学院管理员初审）
     * POST /api/activities/{id}/submit
     */
    @PostMapping("/{id}/submit")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Activity> submitActivity(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            Activity activity = activityService.submitActivity(id, userId);
            return Result.success("活动申报已提交", activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 学院管理员初审
     * POST /api/activities/{id}/college-review
     */
    @PostMapping("/{id}/college-review")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Activity> collegeReview(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            String reviewComment = reviewData.get("reviewComment");
            Activity activity = activityService.collegeReview(id, approvalStatus, reviewComment, userId);
            return Result.success("初审完成", activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 学校管理员终审并发布
     * POST /api/activities/{id}/school-review
     */
    @PostMapping("/{id}/school-review")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN, Constants.ROLE_COLLEGE_ADMIN}, allowAdmin = true)
    public Result<Activity> schoolReview(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            String reviewComment = reviewData.get("reviewComment");
            Activity activity = activityService.schoolReviewAndPublish(id, approvalStatus, reviewComment, userId);
            return Result.success("终审完成", activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询活动
     * GET /api/activities?pageNum=1&pageSize=10&status=PUBLISHED&keyword=创新
     */
    @GetMapping
    public Result<PageResult<Activity>> getActivities(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) Long activityTypeId,
            @RequestParam(required = false) String keyword,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            List<Activity> activities;
            Long total;

            // 非学校/学院管理员：只能看到 已通过 + 自己相关（自己创建/自己报名）
            if (!isSchoolOrCollegeAdmin(role)) {
                if (userId == null) {
                    return Result.error(401, "未登录");
                }
                activities = activityService.getActivitiesVisibleToUser(pageNum, pageSize, status, activityTypeId, keyword, userId);
                total = (long) activityService.countActivitiesVisibleToUser(status, activityTypeId, keyword, userId);
            } else {
                activities = activityService.getActivities(pageNum, pageSize, status, approvalStatus, activityTypeId, keyword);
                total = (long) activityService.countActivities(status, approvalStatus, activityTypeId, keyword);
            }
            PageResult<Activity> pageResult = PageResult.of(pageNum, pageSize, total, activities);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private boolean isSchoolOrCollegeAdmin(String role) {
        return Constants.ROLE_SCHOOL_ADMIN.equals(role) || Constants.ROLE_COLLEGE_ADMIN.equals(role);
    }

    /**
     * 查询活动详情
     * GET /api/activities/{id}
     */
    @GetMapping("/{id}")
    public Result<Activity> getActivityById(@PathVariable Long id) {
        try {
            Activity activity = activityService.getActivityById(id);
            if (activity == null) {
                return Result.error("活动不存在");
            }
            return Result.success(activity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 活动报名
     * POST /api/activities/{id}/register
     */
    @PostMapping("/{id}/register")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<ActivityRegistration> registerActivity(
            @PathVariable Long id,
            @RequestBody Map<String, String> registrationData,
            @RequestAttribute("userId") Long userId) {
        try {
            String contactPhone = registrationData.get("contactPhone");
            String email = registrationData.get("email");
            String remark = registrationData.get("remark");
            ActivityRegistration registration = activityService.registerActivity(id, userId, contactPhone, email, remark);
            return Result.success("报名成功", registration);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交活动总结
     * POST /api/activities/{id}/summary
     */
    @PostMapping("/{id}/summary")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<ActivitySummary> submitSummary(
            @PathVariable Long id,
            @RequestBody ActivitySummary summary,
            @RequestAttribute("userId") Long userId) {
        try {
            ActivitySummary result = activityService.submitSummary(id, summary, userId);
            return Result.success("总结提交成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据活动ID获取总结（组织者或学院管理员，用于判断是否可提交/再提交）
     * GET /api/activities/{id}/summary
     */
    @GetMapping("/{id}/summary")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER, Constants.ROLE_COLLEGE_ADMIN})
    public Result<ActivitySummary> getActivitySummary(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            ActivitySummary summary = activityService.getSummaryByActivityId(id, userId, role);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 学院管理员：审批活动总结（通过/驳回）
     * POST /api/activities/summaries/{summaryId}/review
     */
    @PostMapping("/summaries/{summaryId}/review")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN})
    public Result<ActivitySummary> reviewActivitySummary(
            @PathVariable Long summaryId,
            @RequestBody Map<String, String> body,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = body.get("approvalStatus");
            String reviewComment = body.get("reviewComment");
            if (approvalStatus == null || approvalStatus.isEmpty()) {
                return Result.error("请选择通过或驳回");
            }
            ActivitySummary result = activityService.reviewSummary(summaryId, approvalStatus, reviewComment, userId);
            return Result.success("审批完成", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我报名的活动列表
     * GET /api/activities/my-registrations
     */
    @GetMapping("/my-registrations")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<List<ActivityRegistration>> getMyRegistrations(@RequestAttribute("userId") Long userId) {
        try {
            List<ActivityRegistration> registrations = activityService.getMyRegistrations(userId);
            return Result.success(registrations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 学院管理员：分页获取所有活动总结
     * GET /api/activities/summaries?pageNum=1&pageSize=10
     */
    @GetMapping("/summaries")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN})
    public Result<PageResult<ActivitySummary>> getActivitySummaries(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<ActivitySummary> list = activityService.getActivitySummariesForCollegeAdmin(pageNum, pageSize);
            long total = activityService.countActivitySummariesForCollegeAdmin();
            PageResult<ActivitySummary> pageResult = PageResult.of(pageNum, pageSize, total, list);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消报名
     * DELETE /api/activities/registrations/{id}
     */
    @DeleteMapping("/registrations/{id}")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Void> cancelRegistration(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            activityService.cancelRegistration(id, userId);
            return Result.success("取消报名成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 逻辑删除活动（软删除）
     * 仅系统管理员可删除
     * DELETE /api/activities/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = Constants.ROLE_SCHOOL_ADMIN)
    public Result<Void> deleteActivity(
            @PathVariable Long id,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestAttribute(value = "role", required = false) String role) {
        log.info("删除活动请求: id={}, userId={}, role={}", id, userId, role);
        if (userId == null) {
            return Result.error("用户未登录或登录已过期");
        }
        try {
            activityService.logicalDeleteActivity(id, userId, role);
            return Result.success("活动已删除", null);
        } catch (RuntimeException e) {
            log.warn("删除活动业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除活动系统异常: ", e);
            return Result.error("系统繁忙，请稍后重试");
        }
    }
}

package com.abajin.innovation.service;

import com.abajin.innovation.dto.ActivityDTO;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.enums.ActivityStatus;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.mapper.ActivityRegistrationMapper;
import com.abajin.innovation.entity.Activity;
import com.abajin.innovation.entity.ActivityRegistration;
import com.abajin.innovation.entity.ActivitySummary;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.ActivityMapper;
import com.abajin.innovation.mapper.ActivitySummaryMapper;
import com.abajin.innovation.mapper.SpaceReservationMapper;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.entity.SpaceReservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动管理服务类
 */
@Slf4j
@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRegistrationMapper registrationMapper;

    @Autowired
    private ActivitySummaryMapper summaryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SpaceReservationMapper spaceReservationMapper;

    /**
     * 创建活动申报
     */
    @Transactional
    public Activity createActivity(ActivityDTO dto, Long organizerId) {
        User organizer = userMapper.selectById(organizerId);
        if (organizer == null) {
            throw new RuntimeException("组织者不存在");
        }

        Activity activity = new Activity();
        activity.setTitle(dto.getTitle());
        activity.setActivityTypeId(dto.getActivityTypeId());
        activity.setActivitySeries(dto.getActivitySeries());
        activity.setActivityTypeOther(dto.getActivityTypeOther());
        activity.setOrganizerId(organizerId);
        activity.setOrganizerName(organizer.getRealName());
        activity.setOrganizerType(dto.getOrganizerType() != null ? dto.getOrganizerType() : "USER");
        activity.setOrganizerEntityId(dto.getOrganizerEntityId());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setSpaceId(dto.getSpaceId());
        activity.setLocation(dto.getLocation());
        if (dto.getSpaceId() != null) {
            List<SpaceReservation> resConflicts = spaceReservationMapper.selectBySpaceAndTime(
                    dto.getSpaceId(),
                    dto.getStartTime().toLocalDate(),
                    dto.getStartTime().toLocalTime().toString(),
                    dto.getEndTime().toLocalTime().toString());
            if (!resConflicts.isEmpty()) {
                throw new RuntimeException("该空间该时间段已被预约，请选择其他时间或地点");
            }
            List<Activity> actConflicts = activityMapper.selectBySpaceIdAndDateTimeOverlap(
                    dto.getSpaceId(), dto.getStartTime(), dto.getEndTime(), null);
            if (!actConflicts.isEmpty()) {
                throw new RuntimeException("该空间该时间段已被活动占用，请选择其他时间或地点");
            }
        }
        activity.setDescription(dto.getDescription());
        activity.setContent(dto.getContent());
        activity.setRegistrationLink(dto.getRegistrationLink());
        activity.setQrCodeUrl(dto.getQrCodeUrl());
        activity.setHostUnitId(dto.getHostUnitId());
        activity.setCoOrganizerIds(dto.getCoOrganizerIds());
        activity.setOtherUnits(dto.getOtherUnits());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setRegistrationDeadline(null); // 报名截止已废弃
        activity.setStatus(ActivityStatus.DRAFT.name());
        activity.setApprovalStatus(ApprovalStatus.PENDING.name());
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());

        activityMapper.insert(activity);
        return activity;
    }

    /**
     * 更新活动（组织者或管理员）
     */
    @Transactional
    public Activity updateActivity(Long id, ActivityDTO dto, Long userId) {
        Activity existing = activityMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!existing.getOrganizerId().equals(userId)) {
            // 非组织者需要是管理员
            User user = userMapper.selectById(userId);
            if (user == null || (!Constants.ROLE_COLLEGE_ADMIN.equals(user.getRole()) && !Constants.ROLE_SCHOOL_ADMIN.equals(user.getRole()))) {
                throw new RuntimeException("无权修改此活动");
            }
        }
        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getActivityTypeId() != null) existing.setActivityTypeId(dto.getActivityTypeId());
        if (dto.getActivitySeries() != null) existing.setActivitySeries(dto.getActivitySeries());
        if (dto.getActivityTypeOther() != null) existing.setActivityTypeOther(dto.getActivityTypeOther());
        if (dto.getStartTime() != null) existing.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) existing.setEndTime(dto.getEndTime());
        if (dto.getSpaceId() != null) existing.setSpaceId(dto.getSpaceId());
        if (dto.getLocation() != null) existing.setLocation(dto.getLocation());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getContent() != null) existing.setContent(dto.getContent());
        if (dto.getRegistrationLink() != null) existing.setRegistrationLink(dto.getRegistrationLink());
        if (dto.getQrCodeUrl() != null) existing.setQrCodeUrl(dto.getQrCodeUrl());
        if (dto.getHostUnitId() != null) existing.setHostUnitId(dto.getHostUnitId());
        if (dto.getCoOrganizerIds() != null) existing.setCoOrganizerIds(dto.getCoOrganizerIds());
        if (dto.getOtherUnits() != null) existing.setOtherUnits(dto.getOtherUnits());
        if (dto.getMaxParticipants() != null) existing.setMaxParticipants(dto.getMaxParticipants());
        existing.setUpdateTime(LocalDateTime.now());
        activityMapper.update(existing);
        return activityMapper.selectById(id);
    }

    /**
     * 上传活动海报（仅学校管理员、学院管理员可操作）
     */
    @Transactional
    public Activity updateActivityPoster(Long id, String posterUrl, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || (!Constants.ROLE_COLLEGE_ADMIN.equals(user.getRole()) && !Constants.ROLE_SCHOOL_ADMIN.equals(user.getRole()))) {
            throw new RuntimeException("仅学校管理员和学院管理员可上传活动海报");
        }
        Activity existing = activityMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("活动不存在");
        }
        existing.setPosterUrl(posterUrl);
        existing.setUpdateTime(LocalDateTime.now());
        activityMapper.update(existing);
        return activityMapper.selectById(id);
    }

    /**
     * 提交活动申报（学院管理员初审）
     */
    @Transactional
    public Activity submitActivity(Long activityId, Long organizerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("无权提交此活动");
        }
        if (!ActivityStatus.DRAFT.name().equals(activity.getStatus())) {
            throw new RuntimeException("只能提交草稿状态的活动");
        }

        activity.setStatus(ActivityStatus.SUBMITTED.name());
        activity.setApprovalStatus(ApprovalStatus.PENDING.name());
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.update(activity);
        return activity;
    }

    /**
     * 学院管理员初审
     */
    @Transactional
    public Activity collegeReview(Long activityId, String approvalStatus, String reviewComment, Long reviewerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!ActivityStatus.SUBMITTED.name().equals(activity.getStatus())) {
            throw new RuntimeException("只能审核已提交状态的活动");
        }

        // 学院初审：approvalStatus 仅代表“最终（学校）审批结果”，因此学院通过时不设置为 APPROVED
        ApprovalStatus status;
        try {
            status = ApprovalStatus.valueOf(approvalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("审批状态无效");
        }
        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审核操作只能设置为 APPROVED 或 REJECTED");
        }

        activity.setReviewComment(reviewComment);
        activity.setReviewerId(reviewerId);
        activity.setReviewTime(LocalDateTime.now());

        if (ApprovalStatus.APPROVED.equals(status)) {
            // 学院通过，等待学校终审
            activity.setStatus(ActivityStatus.APPROVED.name());
            activity.setApprovalStatus(ApprovalStatus.PENDING.name());
        } else {
            activity.setStatus(ActivityStatus.REJECTED.name());
            activity.setApprovalStatus(ApprovalStatus.REJECTED.name());
        }

        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.update(activity);
        return activity;
    }

    /**
     * 学校管理员终审并发布
     */
    @Transactional
    public Activity schoolReviewAndPublish(Long activityId, String approvalStatus, String reviewComment, Long reviewerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!ActivityStatus.APPROVED.name().equals(activity.getStatus())) {
            throw new RuntimeException("只能审核学院已通过的活动");
        }

        activity.setApprovalStatus(approvalStatus);
        activity.setReviewComment(reviewComment);
        activity.setReviewerId(reviewerId);
        activity.setReviewTime(LocalDateTime.now());

        if (ApprovalStatus.APPROVED.name().equals(approvalStatus)) {
            activity.setStatus(ActivityStatus.PUBLISHED.name());
        } else {
            activity.setStatus(ActivityStatus.REJECTED.name());
        }

        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.update(activity);
        return activity;
    }

    /**
     * 分页查询活动
     * 统一使用 pageNum / pageSize 作为分页参数
     */
    public List<Activity> getActivities(int pageNum, int pageSize, String status, String approvalStatus, Long activityTypeId, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        return activityMapper.selectPage(offset, pageSize, status, approvalStatus, activityTypeId, keyword);
    }

    public List<Activity> getActivitiesVisibleToUser(int pageNum, int pageSize, String status, Long activityTypeId, String keyword, Long viewerUserId) {
        int offset = (pageNum - 1) * pageSize;
        return activityMapper.selectPageVisibleToUser(offset, pageSize, status, activityTypeId, keyword, viewerUserId);
    }

    /**
     * 统计活动总数
     */
    public int countActivities(String status, String approvalStatus, Long activityTypeId, String keyword) {
        return activityMapper.count(status, approvalStatus, activityTypeId, keyword);
    }

    public int countActivitiesVisibleToUser(String status, Long activityTypeId, String keyword, Long viewerUserId) {
        return activityMapper.countVisibleToUser(status, activityTypeId, keyword, viewerUserId);
    }

    /**
     * 查询活动详情
     */
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }

    /**
     * 活动报名
     */
    @Transactional
    public ActivityRegistration registerActivity(Long activityId, Long userId, String contactPhone, String email, String remark) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!ActivityStatus.PUBLISHED.name().equals(activity.getStatus())) {
            throw new RuntimeException("活动未发布，不能报名");
        }
        if (activity.getRegistrationDeadline() != null
                && LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            throw new RuntimeException("报名已截止");
        }

        // 检查是否已报名
        ActivityRegistration existing = registrationMapper.selectByActivityIdAndUserId(activityId, userId);
        if (existing != null) {
            throw new RuntimeException("您已报名此活动");
        }

        // 检查报名人数
        List<ActivityRegistration> registrations = registrationMapper.selectByActivityId(activityId);
        long approvedCount = registrations.stream()
                .filter(r -> ApprovalStatus.APPROVED.name().equals(r.getApprovalStatus()))
                .count();
        if (activity.getMaxParticipants() != null && approvedCount >= activity.getMaxParticipants()) {
            throw new RuntimeException("报名人数已满");
        }

        User user = userMapper.selectById(userId);
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setUserName(user.getRealName());
        registration.setContactPhone(contactPhone);
        registration.setEmail(email);
        registration.setRemark(remark);
        // 学生和教师报名直接通过，不需要审核
        registration.setStatus("APPROVED");
        registration.setApprovalStatus(ApprovalStatus.APPROVED.name());
        registration.setCreateTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());

        registrationMapper.insert(registration);
        return registration;
    }

    /**
     * 提交活动总结
     */
    @Transactional
    public ActivitySummary submitSummary(Long activityId, ActivitySummary summary, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getOrganizerId().equals(userId)) {
            throw new RuntimeException("只有活动组织者可以提交总结");
        }

        ActivitySummary existing = summaryMapper.selectByActivityId(activityId);
        if (existing != null) {
            if (ApprovalStatus.APPROVED.name().equals(existing.getApprovalStatus())) {
                throw new RuntimeException("该活动总结已通过，不可重复提交");
            }
            if (ApprovalStatus.PENDING.name().equals(existing.getApprovalStatus())) {
                throw new RuntimeException("已提交总结，待学院管理员审批，驳回后可再次提交");
            }
            // REJECTED：允许再次提交，更新原记录
            summary.setId(existing.getId());
            summary.setStatus("SUBMITTED");
            summary.setApprovalStatus(ApprovalStatus.PENDING.name());
            summary.setReviewComment(null);
            summary.setReviewerId(null);
            summary.setReviewTime(null);
            summary.setUpdateTime(LocalDateTime.now());
            summaryMapper.update(summary);
            return summary;
        }

        summary.setActivityId(activityId);
        summary.setStatus("SUBMITTED");
        summary.setApprovalStatus(ApprovalStatus.PENDING.name());
        summary.setCreateTime(LocalDateTime.now());
        summary.setUpdateTime(LocalDateTime.now());
        summaryMapper.insert(summary);
        return summary;
    }

    /**
     * 获取我报名的活动列表（包含活动信息）
     */
    public List<ActivityRegistration> getMyRegistrations(Long userId) {
        return registrationMapper.selectByUserIdWithActivity(userId);
    }

    /**
     * 取消报名
     */
    @Transactional
    public void cancelRegistration(Long registrationId, Long userId) {
        ActivityRegistration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!registration.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消此报名");
        }
        if ("CANCELLED".equals(registration.getStatus())) {
            throw new RuntimeException("报名已取消");
        }

        registration.setStatus("CANCELLED");
        registration.setUpdateTime(LocalDateTime.now());
        registrationMapper.update(registration);
    }

    /**
     * 学院管理员：分页查询所有活动总结
     */
    public List<ActivitySummary> getActivitySummariesForCollegeAdmin(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return summaryMapper.selectPageForAdmin(offset, pageSize);
    }

    /**
     * 学院管理员：统计活动总结总数
     */
    public int countActivitySummariesForCollegeAdmin() {
        return summaryMapper.countForAdmin();
    }

    /**
     * 学院管理员：审批活动总结（通过/驳回）
     */
    @Transactional
    public ActivitySummary reviewSummary(Long summaryId, String approvalStatus, String reviewComment, Long reviewerId) {
        ActivitySummary summary = summaryMapper.selectById(summaryId);
        if (summary == null) {
            throw new RuntimeException("活动总结不存在");
        }
        if (!ApprovalStatus.PENDING.name().equals(summary.getApprovalStatus())) {
            throw new RuntimeException("该总结已审批，无法重复操作");
        }
        ApprovalStatus status;
        try {
            status = ApprovalStatus.valueOf(approvalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("审批状态无效");
        }
        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审批操作只能为通过或驳回");
        }
        summary.setApprovalStatus(status.name());
        summary.setReviewerId(reviewerId);
        summary.setReviewComment(reviewComment);
        summary.setReviewTime(LocalDateTime.now());
        summary.setUpdateTime(LocalDateTime.now());
        summaryMapper.update(summary);
        return summary;
    }

    /**
     * 根据活动ID查询总结（组织者或管理员可查，用于判断是否可提交/再提交）
     */
    public ActivitySummary getSummaryByActivityId(Long activityId, Long userId, String role) {
        ActivitySummary summary = summaryMapper.selectByActivityId(activityId);
        if (summary == null) return null;
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) return null;
        boolean isOrganizer = activity.getOrganizerId().equals(userId);
        boolean isCollegeAdmin = Constants.ROLE_COLLEGE_ADMIN.equals(role);
        if (isOrganizer || isCollegeAdmin) {
            return summary;
        }
        return null;
    }

    /**
     * 逻辑删除活动：设置 is_deleted = 1，保留原状态不变
     */
    @Transactional
    public void logicalDeleteActivity(Long activityId, Long userId, String role) {
        log.info("逻辑删除活动: activityId={}, userId={}, role={}", activityId, userId, role);

        if (activityId == null) {
            throw new RuntimeException("活动ID不能为空");
        }

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        log.info("找到活动: id={}, title={}, currentStatus={}, organizerId={}",
                activity.getId(), activity.getTitle(), activity.getStatus(), activity.getOrganizerId());

        // 仅系统管理员可删除
        if (!Constants.ROLE_SCHOOL_ADMIN.equals(role)) {
            throw new RuntimeException("只有系统管理员才能删除活动");
        }

        ActivityStatus currentStatus;
        try {
            currentStatus = ActivityStatus.valueOf(activity.getStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            currentStatus = null;
        }
        if (currentStatus == ActivityStatus.ONGOING || currentStatus == ActivityStatus.COMPLETED) {
            throw new RuntimeException("进行中或已完成的活动不允许删除");
        }

        // 软删除：设置 is_deleted = 1，保留原状态不变
        activity.setIsDeleted(1);
        activity.setUpdateTime(LocalDateTime.now());

        log.info("更新活动 is_deleted=1: id={}, 原状态保留为: {}", activity.getId(), activity.getStatus());
        int affectedRows = activityMapper.update(activity);
        log.info("更新完成: affectedRows={}", affectedRows);
    }
}

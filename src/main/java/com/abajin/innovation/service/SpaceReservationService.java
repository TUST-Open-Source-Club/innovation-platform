package com.abajin.innovation.service;

import com.abajin.innovation.dto.OccupiedSlotDTO;
import com.abajin.innovation.entity.Activity;
import com.abajin.innovation.entity.Space;
import com.abajin.innovation.entity.SpaceReservation;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.enums.ReservationStatus;
import com.abajin.innovation.enums.SpaceStatus;
import com.abajin.innovation.mapper.ActivityMapper;
import com.abajin.innovation.mapper.SpaceMapper;
import com.abajin.innovation.mapper.SpaceReservationMapper;
import com.abajin.innovation.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 空间预定服务类
 */
@Service
public class SpaceReservationService {
    @Autowired
    private SpaceMapper spaceMapper;

    @Autowired
    private SpaceReservationMapper reservationMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    /**
     * 查询所有可用空间
     */
    public List<Space> getAvailableSpaces() {
        return spaceMapper.selectByStatus(SpaceStatus.AVAILABLE.name());
    }

    /**
     * 查询空间列表，支持按状态筛选
     * @param status 可选，不传则返回所有空间
     */
    public List<Space> getSpaces(String status) {
        if (status != null && !status.isEmpty()) {
            return spaceMapper.selectByStatus(status);
        }
        return spaceMapper.selectAll();
    }

    /**
     * 查询空间详情
     */
    public Space getSpaceById(Long id) {
        return spaceMapper.selectById(id);
    }

    /**
     * 管理员修改空间状态
     */
    @Transactional
    public Space updateSpaceStatus(Long spaceId, String status) {
        if (status == null || status.isEmpty()) {
            throw new RuntimeException("状态不能为空");
        }
        SpaceStatus newStatus;
        try {
            newStatus = SpaceStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的空间状态");
        }
        Space space = spaceMapper.selectById(spaceId);
        if (space == null) {
            throw new RuntimeException("空间不存在");
        }
        space.setStatus(newStatus.name());
        space.setUpdateTime(LocalDateTime.now());
        spaceMapper.update(space);
        return space;
    }

    /**
     * 查询某空间在某日的占用时段（空间预约 + 活动，合并用于前端灰显）
     */
    public List<OccupiedSlotDTO> getSpaceOccupiedSlots(Long spaceId, LocalDate date) {
        List<OccupiedSlotDTO> list = new ArrayList<>();
        List<SpaceReservation> reservations = getSpaceReservations(spaceId, date);
        for (SpaceReservation r : reservations) {
            if (ApprovalStatus.PENDING.name().equals(r.getApprovalStatus()) || ApprovalStatus.APPROVED.name().equals(r.getApprovalStatus())) {
                list.add(new OccupiedSlotDTO(r.getStartTime().toString(), r.getEndTime().toString()));
            }
        }
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Activity> activities = activityMapper.selectBySpaceIdAndDateTimeOverlap(spaceId, dayStart, dayEnd, null);
        for (Activity a : activities) {
            list.add(new OccupiedSlotDTO(
                    a.getStartTime().toLocalTime().toString(),
                    a.getEndTime().toLocalTime().toString()));
        }
        return list;
    }

    /**
     * 查询空间的实时预约状态（指定日期）
     */
    public List<SpaceReservation> getSpaceReservations(Long spaceId, LocalDate date) {
        if (date == null) {
            return reservationMapper.selectBySpaceId(spaceId);
        }
        List<SpaceReservation> allReservations = reservationMapper.selectBySpaceId(spaceId);
        LocalDate today = LocalDate.now();
        java.time.LocalTime nowTime = java.time.LocalTime.now();
        return allReservations.stream()
                .filter(r -> r.getReservationDate().equals(date))
                .filter(r -> !ReservationStatus.CANCELLED.name().equals(r.getStatus())
                        && !ReservationStatus.REJECTED.name().equals(r.getStatus()))
                // 对于当天的预约，已结束的时段不再视为占用，便于重新预约
                .filter(r -> {
                    if (!r.getReservationDate().isEqual(today)) {
                        return true;
                    }
                    return r.getEndTime().isAfter(nowTime);
                })
                .toList();
    }

    /**
     * 提交预约申请
     */
    @Transactional
    public SpaceReservation createReservation(SpaceReservation reservation, Long applicantId) {
        boolean isOtherSpace = reservation.getSpaceId() == null
                && reservation.getCustomSpaceName() != null
                && !reservation.getCustomSpaceName().isBlank();

        if (isOtherSpace) {
            reservation.setSpaceId(null);
            // “其他”空间不做冲突校验
        } else {
            // 验证空间是否存在且可用
            Space space = spaceMapper.selectById(reservation.getSpaceId());
            if (space == null) {
                throw new RuntimeException("空间不存在");
            }
            if (!SpaceStatus.AVAILABLE.name().equals(space.getStatus())) {
                throw new RuntimeException("空间当前不可用");
            }
            // 检查与空间预约的时间冲突（仅考虑尚未结束的预约）
            List<SpaceReservation> conflicts = reservationMapper.selectBySpaceAndTime(
                    reservation.getSpaceId(),
                    reservation.getReservationDate(),
                    reservation.getStartTime().toString(),
                    reservation.getEndTime().toString()
            );
            LocalDate today = LocalDate.now();
            java.time.LocalTime nowTime = java.time.LocalTime.now();
            conflicts = conflicts.stream()
                    .filter(r -> {
                        if (!r.getReservationDate().isEqual(today)) {
                            return true;
                        }
                        return r.getEndTime().isAfter(nowTime);
                    })
                    .toList();
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("该时间段已被预约，请选择其他时间");
            }
            // 检查与活动占用的时间冲突
            LocalDateTime resStart = LocalDateTime.of(reservation.getReservationDate(), reservation.getStartTime());
            LocalDateTime resEnd = LocalDateTime.of(reservation.getReservationDate(), reservation.getEndTime());
            List<Activity> activityConflicts = activityMapper.selectBySpaceIdAndDateTimeOverlap(
                    reservation.getSpaceId(), resStart, resEnd, null);
            if (!activityConflicts.isEmpty()) {
                throw new RuntimeException("该时间段已被活动占用，请选择其他时间");
            }
        }

        // 验证时间合理性
        if (reservation.getStartTime().isAfter(reservation.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }

        // 设置申请人信息
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new RuntimeException("申请人不存在");
        }

        reservation.setApplicantId(applicantId);
        reservation.setApplicantName(applicant.getRealName());
        reservation.setStatus(ReservationStatus.PENDING.name());
        reservation.setApprovalStatus(ApprovalStatus.PENDING.name());
        reservation.setCreateTime(LocalDateTime.now());
        reservation.setUpdateTime(LocalDateTime.now());

        reservationMapper.insert(reservation);

        // 通知学校管理员有新的空间预约
        String spaceName = reservation.getCustomSpaceName();
        if (spaceName == null || spaceName.isBlank()) {
            Space space = spaceMapper.selectById(reservation.getSpaceId());
            if (space != null) {
                spaceName = space.getName();
            } else {
                spaceName = "未知空间";
            }
        }
        if (emailService != null) {
            emailService.notifySchoolAdmins("空间预约",
                    spaceName + " " + reservation.getReservationDate() + " " + reservation.getStartTime() + "-" + reservation.getEndTime());
        }

        return reservation;
    }

    /**
     * 取消预约
     */
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        SpaceReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!reservation.getApplicantId().equals(userId)) {
            throw new RuntimeException("无权取消此预约");
        }
        if (ReservationStatus.COMPLETED.name().equals(reservation.getStatus())) {
            throw new RuntimeException("已完成的预约不能取消");
        }

        reservation.setStatus(ReservationStatus.CANCELLED.name());
        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.update(reservation);
    }

    /**
     * 审核预约申请（仅学校管理员可审批，空间预约跳过学院审批）
     */
    @Transactional
    public SpaceReservation reviewReservation(Long reservationId, String approvalStatus, String reviewComment, Long reviewerId) {
        SpaceReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        User reviewer = userMapper.selectById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("审核人不存在");
        }
        String reviewerRole = reviewer.getRole();

        if (!com.abajin.innovation.common.Constants.ROLE_SCHOOL_ADMIN.equals(reviewerRole)) {
            throw new RuntimeException("空间预约仅学校管理员可审批");
        }

        ApprovalStatus status;
        try {
            status = ApprovalStatus.valueOf(approvalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("审批状态无效");
        }
        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审核操作只能设置为 APPROVED 或 REJECTED");
        }

        if (!ApprovalStatus.PENDING.name().equals(reservation.getApprovalStatus())) {
            throw new RuntimeException("该预约已审核，不能重复审核");
        }

        reservation.setReviewComment(reviewComment);
        reservation.setReviewerId(reviewerId);
        reservation.setReviewTime(LocalDateTime.now());

        if (com.abajin.innovation.common.Constants.ROLE_COLLEGE_ADMIN.equals(reviewerRole)) {
            // First stage: only PENDING reservations can be reviewed by college admin
            if (!ReservationStatus.PENDING.name().equals(reservation.getStatus())) {
                throw new RuntimeException("只能审核待学院审核的预约");
            }
            if (ApprovalStatus.APPROVED.equals(status)) {
                // College approved -> waiting for school review
                reservation.setStatus(ReservationStatus.APPROVED.name());
                reservation.setApprovalStatus(ApprovalStatus.PENDING.name());
            } else {
                reservation.setStatus(ReservationStatus.REJECTED.name());
                reservation.setApprovalStatus(ApprovalStatus.REJECTED.name());
            }
        } else if (com.abajin.innovation.common.Constants.ROLE_SCHOOL_ADMIN.equals(reviewerRole)
                || com.abajin.innovation.common.Constants.ROLE_STUDENT_ADMIN.equals(reviewerRole)) {
            // School admin or Student admin can review both college-pending and school-pending reservations
            if (ReservationStatus.PENDING.name().equals(reservation.getStatus())
                    && ApprovalStatus.PENDING.name().equals(reservation.getApprovalStatus())) {
                // Direct review: school admin bypasses college review
                if (ApprovalStatus.APPROVED.equals(status)) {
                    reservation.setStatus(ReservationStatus.APPROVED.name());
                    reservation.setApprovalStatus(ApprovalStatus.APPROVED.name());
                } else {
                    reservation.setStatus(ReservationStatus.REJECTED.name());
                    reservation.setApprovalStatus(ApprovalStatus.REJECTED.name());
                }
            } else if (ReservationStatus.APPROVED.name().equals(reservation.getStatus())
                    && ApprovalStatus.PENDING.name().equals(reservation.getApprovalStatus())) {
                // Final review: college already approved
                if (ApprovalStatus.APPROVED.equals(status)) {
                    reservation.setStatus(ReservationStatus.APPROVED.name());
                    reservation.setApprovalStatus(ApprovalStatus.APPROVED.name());
                } else {
                    reservation.setStatus(ReservationStatus.REJECTED.name());
                    reservation.setApprovalStatus(ApprovalStatus.REJECTED.name());
                }
            } else {
                throw new RuntimeException("只能审核待审批的预约");
            }
        } else {
            throw new RuntimeException("无权审核预约");

        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.update(reservation);

        // 通知申请人审批结果
        String spaceName = reservation.getCustomSpaceName();
        if (spaceName == null || spaceName.isBlank()) {
            Space space = spaceMapper.selectById(reservation.getSpaceId());
            if (space != null) {
                spaceName = space.getName();
            } else {
                spaceName = "未知空间";
            }
        }
        if (emailService != null) {
            emailService.notifyApplicant(reservation.getApplicantId(), "空间预约",
                    spaceName + " " + reservation.getReservationDate() + " " + reservation.getStartTime() + "-" + reservation.getEndTime(),
                    ApprovalStatus.APPROVED.equals(status), reviewComment);
        }

        return reservation;
    }

    /**
     * 查询我的预约列表
     */
    public List<SpaceReservation> getMyReservations(Long applicantId) {
        return reservationMapper.selectByApplicantId(applicantId);
    }

    /**
     * 查询待审核的预约列表（管理员）
     * 空间预约跳过学院审批，仅学校管理员可查看和审批
     * 学院管理员：查看待学院审核（status=PENDING）
     * 学校管理员/学生管理员：查看所有待审核（status=PENDING 或 status=APPROVED且approvalStatus=PENDING）
     */
    public List<SpaceReservation> getPendingReservations(String role) {
        // 空间预约不经过学院审批
        if (com.abajin.innovation.common.Constants.ROLE_COLLEGE_ADMIN.equals(role)) {
            return List.of();
        }
        // School admin: 查看所有待审核的预约
        if (com.abajin.innovation.common.Constants.ROLE_SCHOOL_ADMIN.equals(role)) {
            return reservationMapper.selectByStatus(ReservationStatus.PENDING.name());
        }
        return List.of();
    }

    /**
     * 按状态查询预约列表（管理员）
     * @param status 可选，PENDING/APPROVED/REJECTED，不传则返回所有
     */
    public List<SpaceReservation> getReservationsByStatus(String status) {
        if (status != null && !status.isEmpty()) {
            return reservationMapper.selectByStatus(status);
        }
        return reservationMapper.selectAll();
    }

    public int createSpace(@Valid Space space) {
        String originalName = space.getName();
        String currentName = originalName;
        int count = 1;

        // 循环检查数据库中是否存在同名
        while (spaceMapper.selectCountByName(currentName) > 0) {
            currentName = originalName + "(" + count + ")";
            count++;
        }
        // 将最终确定的唯一名称赋给对象
        space.setName(currentName);

        spaceMapper.insertSpace(space);
        return space.getCapacity();
    }

    /**
     * 管理员更新空间基本信息
     */
    @Transactional
    public Space updateSpace(Long spaceId, Space space) {
        Space existing = spaceMapper.selectById(spaceId);
        if (existing == null) {
            throw new RuntimeException("空间不存在");
        }
        if (space.getName() != null && !space.getName().isEmpty()) {
            existing.setName(space.getName());
        }
        if (space.getLocation() != null) {
            existing.setLocation(space.getLocation());
        }
        if (space.getCapacity() != null) {
            existing.setCapacity(space.getCapacity());
        }
        if (space.getFacilities() != null) {
            existing.setFacilities(space.getFacilities());
        }
        if (space.getDescription() != null) {
            existing.setDescription(space.getDescription());
        }
        if (space.getStatus() != null && !space.getStatus().isEmpty()) {
            existing.setStatus(space.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        spaceMapper.update(existing);
        return existing;
    }
}

package com.abajin.innovation.controller;

import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.service.SpaceReservationService;
import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.OccupiedSlotDTO;
import com.abajin.innovation.entity.Space;
import com.abajin.innovation.entity.SpaceReservation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 空间预定控制器
 */
@RestController
@RequestMapping("/spaces")
public class SpaceController {
    @Autowired
    private SpaceReservationService spaceReservationService;

    /**
     * 查询空间列表
     * GET /api/spaces?status=AVAILABLE
     * @param status 可选，空间状态：AVAILABLE-可用, MAINTENANCE-维护中, DISABLED-已禁用。不传则返回所有
     */
    @GetMapping
    public Result<List<Space>> getSpaces(@RequestParam(required = false) String status) {
        try {
            List<Space> spaces = spaceReservationService.getSpaces(status);
            return Result.success(spaces);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询空间详情
     * GET /api/spaces/{id}
     */
    @GetMapping("/{id}")
    public Result<Space> getSpaceById(@PathVariable Long id) {
        try {
            Space space = spaceReservationService.getSpaceById(id);
            if (space == null) {
                return Result.error("空间不存在");
            }
            return Result.success(space);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改空间状态（仅学院管理员、学校管理员）
     * PUT /api/spaces/{id}/status
     */
    @PutMapping("/{id}/status")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Space> updateSpaceStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Space updated = spaceReservationService.updateSpaceStatus(id, status);
            return Result.success("空间状态已更新", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询空间的实时预约状态
     * GET /api/spaces/{id}/reservations?date=2024-01-01
     */
    @GetMapping("/{id}/reservations")
    public Result<List<SpaceReservation>> getSpaceReservations(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<SpaceReservation> reservations = spaceReservationService.getSpaceReservations(id, date);
            return Result.success(reservations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询空间在指定日期的占用时段（空间预约 + 活动合并，用于前端时段灰显）
     * GET /api/spaces/{id}/occupied-slots?date=2024-01-01
     */
    @GetMapping("/{id}/occupied-slots")
    public Result<List<OccupiedSlotDTO>> getSpaceOccupiedSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<OccupiedSlotDTO> slots = spaceReservationService.getSpaceOccupiedSlots(id, date);
            return Result.success(slots);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交预约申请
     * POST /api/spaces/reservations
     * 学生和教师都可以使用
     */
    @PostMapping("/reservations")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER, Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN})
    public Result<SpaceReservation> createReservation(
            @Valid @RequestBody SpaceReservation reservation,
            @RequestAttribute("userId") Long userId) {
        try {
            SpaceReservation created = spaceReservationService.createReservation(reservation, userId);
            return Result.success("预约申请提交成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消预约
     * DELETE /api/spaces/reservations/{id}
     */
    @DeleteMapping("/reservations/{id}")
    @RequiresRole(value = {Constants.ROLE_STUDENT, Constants.ROLE_TEACHER})
    public Result<Void> cancelReservation(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            spaceReservationService.cancelReservation(id, userId);
            return Result.success("预约已取消", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询我的预约列表
     * GET /api/spaces/reservations/my
     */
    @GetMapping("/reservations/my")
    public Result<List<SpaceReservation>> getMyReservations(@RequestAttribute("userId") Long userId) {
        try {
            List<SpaceReservation> reservations = spaceReservationService.getMyReservations(userId);
            return Result.success(reservations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核预约申请（管理员）
     * POST /api/spaces/reservations/{id}/review
     */
    @PostMapping("/reservations/{id}/review")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<SpaceReservation> reviewReservation(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            String reviewComment = reviewData.get("reviewComment");

            if (!ApprovalStatus.APPROVED.name().equals(approvalStatus)
                    && !ApprovalStatus.REJECTED.name().equals(approvalStatus)) {
                return Result.error("审批状态无效");
            }

            SpaceReservation reviewed = spaceReservationService.reviewReservation(
                    id, approvalStatus, reviewComment, userId);
            return Result.success("审核完成", reviewed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询待审核预约列表（管理员）
     * GET /api/spaces/reservations/pending
     */
    @GetMapping("/reservations/pending")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<List<SpaceReservation>> getPendingReservations(@RequestAttribute(value = "role", required = false) String role) {
        try {
            List<SpaceReservation> reservations = spaceReservationService.getPendingReservations(role);
            return Result.success(reservations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 按状态查询预约列表（管理员）
     * GET /api/spaces/reservations/admin?status=PENDING
     */
    @GetMapping("/reservations/admin")
    @RequiresRole(
            value = {
                    Constants.ROLE_STUDENT,
                    Constants.ROLE_TEACHER,
                    Constants.ROLE_COLLEGE_ADMIN,
                    Constants.ROLE_SCHOOL_ADMIN
            },
            allowAdmin = true
    )
    public Result<List<SpaceReservation>> getReservationsByStatus(@RequestParam(required = false) String status) {
        try {
            List<SpaceReservation> reservations = spaceReservationService.getReservationsByStatus(status);
            return Result.success(reservations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/create")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN}, allowAdmin = true)
    public Result<Integer> createSpace(@Valid @RequestBody Space space) {
        try {
            int created = spaceReservationService.createSpace(space);
            return Result.success("空间创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新空间基本信息（管理员）
     * PUT /api/spaces/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Space> updateSpace(
            @PathVariable Long id,
            @RequestBody Space space) {
        try {
            Space updated = spaceReservationService.updateSpace(id, space);
            return Result.success("空间更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

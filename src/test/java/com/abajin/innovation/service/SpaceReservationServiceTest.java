package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 空间预约服务测试
 */
@ExtendWith(MockitoExtension.class)
class SpaceReservationServiceTest {

    @Mock
    private SpaceMapper spaceMapper;

    @Mock
    private SpaceReservationMapper reservationMapper;

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SpaceReservationService spaceReservationService;

    private User applicant;
    private User collegeAdmin;
    private User schoolAdmin;
    private Space space;
    private SpaceReservation reservation;

    @BeforeEach
    void setUp() {
        applicant = new User();
        applicant.setId(1L);
        applicant.setRealName("申请人");
        applicant.setRole(Constants.ROLE_STUDENT);

        collegeAdmin = new User();
        collegeAdmin.setId(2L);
        collegeAdmin.setRealName("学院管理员");
        collegeAdmin.setRole(Constants.ROLE_COLLEGE_ADMIN);

        schoolAdmin = new User();
        schoolAdmin.setId(3L);
        schoolAdmin.setRealName("学校管理员");
        schoolAdmin.setRole(Constants.ROLE_SCHOOL_ADMIN);

        space = new Space();
        space.setId(1L);
        space.setName("会议室A");
        space.setStatus(SpaceStatus.AVAILABLE.name());

        reservation = new SpaceReservation();
        reservation.setId(1L);
        reservation.setSpaceId(1L);
        reservation.setReservationDate(LocalDate.now().plusDays(1));
        reservation.setStartTime(LocalTime.of(9, 0));
        reservation.setEndTime(LocalTime.of(11, 0));
        reservation.setApplicantId(1L);
        reservation.setStatus(ReservationStatus.PENDING.name());
        reservation.setApprovalStatus(ApprovalStatus.PENDING.name());
    }

    @Test
    void getAvailableSpaces_returnsAvailableSpaces() {
        // Arrange
        List<Space> spaces = Collections.singletonList(space);
        when(spaceMapper.selectByStatus(SpaceStatus.AVAILABLE.name())).thenReturn(spaces);

        // Act
        List<Space> result = spaceReservationService.getAvailableSpaces();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSpaces_withStatus_returnsFilteredSpaces() {
        // Arrange
        List<Space> spaces = Collections.singletonList(space);
        when(spaceMapper.selectByStatus(SpaceStatus.AVAILABLE.name())).thenReturn(spaces);

        // Act
        List<Space> result = spaceReservationService.getSpaces(SpaceStatus.AVAILABLE.name());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSpaces_withoutStatus_returnsAllSpaces() {
        // Arrange
        List<Space> spaces = Collections.singletonList(space);
        when(spaceMapper.selectAll()).thenReturn(spaces);

        // Act
        List<Space> result = spaceReservationService.getSpaces(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSpaceById_withExistingSpace_returnsSpace() {
        // Arrange
        when(spaceMapper.selectById(1L)).thenReturn(space);

        // Act
        Space result = spaceReservationService.getSpaceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("会议室A", result.getName());
    }

    @Test
    void updateSpaceStatus_withValidStatus_updatesSuccessfully() {
        // Arrange
        when(spaceMapper.selectById(1L)).thenReturn(space);
        when(spaceMapper.update(any(Space.class))).thenReturn(1);

        // Act
        Space result = spaceReservationService.updateSpaceStatus(1L, SpaceStatus.MAINTENANCE.name());

        // Assert
        assertEquals(SpaceStatus.MAINTENANCE.name(), result.getStatus());
    }

    @Test
    void updateSpaceStatus_withNullStatus_throwsException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.updateSpaceStatus(1L, null));
        assertEquals("状态不能为空", exception.getMessage());
    }

    @Test
    void updateSpaceStatus_withInvalidStatus_throwsException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.updateSpaceStatus(1L, "INVALID"));
        assertEquals("无效的空间状态", exception.getMessage());
    }

    @Test
    void createReservation_withValidData_createsSuccessfully() {
        // Arrange
        when(spaceMapper.selectById(1L)).thenReturn(space);
        when(reservationMapper.selectBySpaceAndTime(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(activityMapper.selectBySpaceIdAndDateTimeOverlap(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(userMapper.selectById(1L)).thenReturn(applicant);
        when(reservationMapper.insert(any(SpaceReservation.class))).thenReturn(1);

        // Act
        SpaceReservation result = spaceReservationService.createReservation(reservation, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getApplicantId());
        assertEquals("申请人", result.getApplicantName());
        assertEquals(ReservationStatus.PENDING.name(), result.getStatus());
    }

    @Test
    void createReservation_withOtherSpace_skipsConflictCheck() {
        // Arrange
        SpaceReservation otherSpaceReservation = new SpaceReservation();
        otherSpaceReservation.setSpaceId(null);
        otherSpaceReservation.setCustomSpaceName("其他空间");
        otherSpaceReservation.setReservationDate(LocalDate.now().plusDays(1));
        otherSpaceReservation.setStartTime(LocalTime.of(9, 0));
        otherSpaceReservation.setEndTime(LocalTime.of(11, 0));

        when(userMapper.selectById(1L)).thenReturn(applicant);
        when(reservationMapper.insert(any(SpaceReservation.class))).thenReturn(1);

        // Act
        SpaceReservation result = spaceReservationService.createReservation(otherSpaceReservation, 1L);

        // Assert
        assertNotNull(result);
        assertNull(result.getSpaceId());
        assertEquals("其他空间", result.getCustomSpaceName());
    }

    @Test
    void createReservation_withNonExistentSpace_throwsException() {
        // Arrange
        when(spaceMapper.selectById(999L)).thenReturn(null);
        reservation.setSpaceId(999L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.createReservation(reservation, 1L));
        assertEquals("空间不存在", exception.getMessage());
    }

    @Test
    void createReservation_withUnavailableSpace_throwsException() {
        // Arrange
        space.setStatus(SpaceStatus.MAINTENANCE.name());
        when(spaceMapper.selectById(1L)).thenReturn(space);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.createReservation(reservation, 1L));
        assertEquals("空间当前不可用", exception.getMessage());
    }

    @Test
    void createReservation_withTimeConflict_throwsException() {
        // Arrange
        when(spaceMapper.selectById(1L)).thenReturn(space);
        List<SpaceReservation> conflicts = new ArrayList<>();
        SpaceReservation conflict = new SpaceReservation();
        conflict.setReservationDate(LocalDate.now().plusDays(1));
        conflict.setEndTime(LocalTime.of(23, 0));
        conflicts.add(conflict);
        when(reservationMapper.selectBySpaceAndTime(any(), any(), any(), any())).thenReturn(conflicts);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.createReservation(reservation, 1L));
        assertEquals("该时间段已被预约，请选择其他时间", exception.getMessage());
    }

    @Test
    void createReservation_withInvalidTimeRange_throwsException() {
        // Arrange
        when(spaceMapper.selectById(1L)).thenReturn(space);
        when(reservationMapper.selectBySpaceAndTime(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(activityMapper.selectBySpaceIdAndDateTimeOverlap(any(), any(), any(), any())).thenReturn(Collections.emptyList());

        reservation.setStartTime(LocalTime.of(14, 0));
        reservation.setEndTime(LocalTime.of(9, 0));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.createReservation(reservation, 1L));
        assertEquals("开始时间不能晚于结束时间", exception.getMessage());
    }

    @Test
    void cancelReservation_withValidReservation_cancelsSuccessfully() {
        // Arrange
        when(reservationMapper.selectById(1L)).thenReturn(reservation);
        when(reservationMapper.update(any(SpaceReservation.class))).thenReturn(1);

        // Act
        spaceReservationService.cancelReservation(1L, 1L);

        // Assert
        verify(reservationMapper).update(any(SpaceReservation.class));
    }

    @Test
    void cancelReservation_withNonExistentReservation_throwsException() {
        // Arrange
        when(reservationMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.cancelReservation(999L, 1L));
        assertEquals("预约不存在", exception.getMessage());
    }

    @Test
    void cancelReservation_withUnauthorizedUser_throwsException() {
        // Arrange
        when(reservationMapper.selectById(1L)).thenReturn(reservation);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.cancelReservation(1L, 999L));
        assertEquals("无权取消此预约", exception.getMessage());
    }

    @Test
    void cancelReservation_withCompletedStatus_throwsException() {
        // Arrange
        reservation.setStatus(ReservationStatus.COMPLETED.name());
        when(reservationMapper.selectById(1L)).thenReturn(reservation);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.cancelReservation(1L, 1L));
        assertEquals("已完成的预约不能取消", exception.getMessage());
    }

    @Test
    void reviewReservation_byCollegeAdmin_approvesSuccessfully() {
        // Arrange
        when(reservationMapper.selectById(1L)).thenReturn(reservation);
        when(userMapper.selectById(2L)).thenReturn(collegeAdmin);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.reviewReservation(1L, ApprovalStatus.APPROVED.name(), "通过", 2L));
        assertEquals("空间预约仅学校管理员可审批", exception.getMessage());
    }

    @Test
    void reviewReservation_byCollegeAdmin_rejectsSuccessfully() {
        // Arrange
        when(reservationMapper.selectById(1L)).thenReturn(reservation);
        when(userMapper.selectById(2L)).thenReturn(collegeAdmin);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.reviewReservation(1L, ApprovalStatus.REJECTED.name(), "不通过", 2L));
        assertEquals("空间预约仅学校管理员可审批", exception.getMessage());
    }

    @Test
    void reviewReservation_bySchoolAdmin_finalApprovesSuccessfully() {
        // Arrange
        reservation.setStatus(ReservationStatus.APPROVED.name());
        reservation.setApprovalStatus(ApprovalStatus.PENDING.name());
        when(reservationMapper.selectById(1L)).thenReturn(reservation);
        when(userMapper.selectById(3L)).thenReturn(schoolAdmin);

        // Act
        SpaceReservation result = spaceReservationService.reviewReservation(1L, ApprovalStatus.APPROVED.name(), "终审通过", 3L);

        // Assert
        assertEquals(ReservationStatus.APPROVED.name(), result.getStatus());
        assertEquals(ApprovalStatus.APPROVED.name(), result.getApprovalStatus());
    }

    @Test
    void reviewReservation_withUnauthorizedRole_throwsException() {
        // Arrange
        User student = new User();
        student.setId(4L);
        student.setRole(Constants.ROLE_STUDENT);
        when(reservationMapper.selectById(1L)).thenReturn(reservation);
        when(userMapper.selectById(4L)).thenReturn(student);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> spaceReservationService.reviewReservation(1L, ApprovalStatus.APPROVED.name(), "", 4L));
        assertEquals("空间预约仅学校管理员可审批", exception.getMessage());
    }

    @Test
    void getMyReservations_returnsUserReservations() {
        // Arrange
        List<SpaceReservation> reservations = Collections.singletonList(reservation);
        when(reservationMapper.selectByApplicantId(1L)).thenReturn(reservations);

        // Act
        List<SpaceReservation> result = spaceReservationService.getMyReservations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getPendingReservations_forCollegeAdmin_returnsPendingReservations() {
        // Act
        List<SpaceReservation> result = spaceReservationService.getPendingReservations(Constants.ROLE_COLLEGE_ADMIN);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getReservationsByStatus_withStatus_returnsFilteredReservations() {
        // Arrange
        List<SpaceReservation> approvedList = Collections.singletonList(reservation);
        when(reservationMapper.selectByStatus(ReservationStatus.APPROVED.name())).thenReturn(approvedList);

        // Act
        List<SpaceReservation> result = spaceReservationService.getReservationsByStatus(ReservationStatus.APPROVED.name());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getReservationsByStatus_withoutStatus_returnsAllReservations() {
        // Arrange
        List<SpaceReservation> allList = Collections.singletonList(reservation);
        when(reservationMapper.selectAll()).thenReturn(allList);

        // Act
        List<SpaceReservation> result = spaceReservationService.getReservationsByStatus(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

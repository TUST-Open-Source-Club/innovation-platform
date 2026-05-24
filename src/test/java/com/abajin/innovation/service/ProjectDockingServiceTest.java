package com.abajin.innovation.service;

import com.abajin.innovation.dto.ProjectApplicationDTO;
import com.abajin.innovation.dto.ProjectDockingReviewDTO;
import com.abajin.innovation.dto.ProjectRecruitmentApplicationDTO;
import com.abajin.innovation.dto.ProjectRecruitmentDTO;
import com.abajin.innovation.entity.Project;
import com.abajin.innovation.entity.ProjectApplication;
import com.abajin.innovation.entity.ProjectRecruitment;
import com.abajin.innovation.entity.ProjectRecruitmentApplication;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.ProjectApplicationMapper;
import com.abajin.innovation.mapper.ProjectMapper;
import com.abajin.innovation.mapper.ProjectRecruitmentApplicationMapper;
import com.abajin.innovation.mapper.ProjectRecruitmentMapper;
import com.abajin.innovation.mapper.TeamMapper;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 项目对接服务测试
 */
@ExtendWith(MockitoExtension.class)
class ProjectDockingServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectApplicationMapper projectApplicationMapper;

    @Mock
    private ProjectRecruitmentMapper projectRecruitmentMapper;

    @Mock
    private ProjectRecruitmentApplicationMapper projectRecruitmentApplicationMapper;

    @Mock
    private TeamMemberMapper teamMemberMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProjectDockingService projectDockingService;

    private User leader;
    private User student;
    private User teacher;
    private Project project;
    private ProjectRecruitment recruitment;

    @BeforeEach
    void setUp() {
        leader = new User();
        leader.setId(1L);
        leader.setUsername("leader");
        leader.setRealName("负责人");
        leader.setRole("TEACHER");
        leader.setEmail("leader@example.com");
        leader.setCollegeId(1L);
        leader.setCollegeName("计算机学院");

        student = new User();
        student.setId(2L);
        student.setUsername("student");
        student.setRealName("学生");
        student.setRole("STUDENT");
        student.setEmail("student@example.com");
        student.setPhone("13800138000");
        student.setCollegeId(1L);
        student.setCollegeName("计算机学院");

        teacher = new User();
        teacher.setId(3L);
        teacher.setUsername("teacher");
        teacher.setRealName("教师");
        teacher.setRole("TEACHER");
        teacher.setEmail("teacher@example.com");

        project = new Project();
        project.setId(1L);
        project.setTitle("测试项目");
        project.setLeaderId(1L);
        project.setTeamId(1L);

        recruitment = new ProjectRecruitment();
        recruitment.setId(1L);
        recruitment.setProjectId(1L);
        recruitment.setProjectTitle("测试项目");
        recruitment.setPublisherId(1L);
        recruitment.setPositionName("前端开发");
        recruitment.setTaskDescription("负责前端页面开发");
        recruitment.setStatus("OPEN");
    }

    // ========== 主动申请相关测试 ==========

    @Test
    void applyToProject_withValidData_returnsApplication() {
        // Arrange
        ProjectApplicationDTO dto = new ProjectApplicationDTO();
        dto.setDesiredPosition("开发");
        dto.setApplicantMajor("计算机");

        when(userMapper.selectById(2L)).thenReturn(student);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(teamMemberMapper.selectByTeamIdAndUserId(1L, 2L)).thenReturn(null);
        when(projectApplicationMapper.selectByProjectIdAndApplicantIdAndType(1L, 2L, "JOIN_TEAM"))
                .thenReturn(Collections.emptyList());
        when(projectApplicationMapper.insert(any(ProjectApplication.class))).thenReturn(1);

        // Act
        ProjectApplication result = projectDockingService.applyToProject(1L, dto, 2L);

        // Assert
        assertNotNull(result);
        assertEquals("测试项目", result.getProjectTitle());
        assertEquals("PENDING", result.getApprovalStatus());
        verify(projectApplicationMapper).insert(any(ProjectApplication.class));
        verify(emailService, atLeastOnce()).sendMail(any(), any(), any());
    }

    @Test
    void applyToProject_withLeaderRole_throwsException() {
        // Arrange
        User studentLeader = new User();
        studentLeader.setId(1L);
        studentLeader.setUsername("leader");
        studentLeader.setRealName("负责人");
        studentLeader.setRole("STUDENT");
        studentLeader.setEmail("leader@example.com");
        studentLeader.setCollegeId(1L);
        studentLeader.setCollegeName("计算机学院");

        ProjectApplicationDTO dto = new ProjectApplicationDTO();
        when(userMapper.selectById(1L)).thenReturn(studentLeader);
        when(projectMapper.selectById(1L)).thenReturn(project);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectDockingService.applyToProject(1L, dto, 1L));
        assertEquals("项目负责人不能申请加入自己的项目", exception.getMessage());
    }

    @Test
    void applyToProject_withTeacherRole_returnsApplication() {
        // Arrange
        ProjectApplicationDTO dto = new ProjectApplicationDTO();
        dto.setDesiredPosition("开发");
        dto.setApplicantMajor("计算机");

        when(userMapper.selectById(3L)).thenReturn(teacher);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(teamMemberMapper.selectByTeamIdAndUserId(1L, 3L)).thenReturn(null);
        when(projectApplicationMapper.selectByProjectIdAndApplicantIdAndType(1L, 3L, "JOIN_TEAM"))
                .thenReturn(Collections.emptyList());
        when(projectApplicationMapper.insert(any(ProjectApplication.class))).thenReturn(1);

        // Act
        ProjectApplication result = projectDockingService.applyToProject(1L, dto, 3L);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getApprovalStatus());
        verify(projectApplicationMapper).insert(any(ProjectApplication.class));
    }

    @Test
    void updateProjectApplication_withValidData_returnsUpdated() {
        // Arrange
        ProjectApplication application = new ProjectApplication();
        application.setId(1L);
        application.setApplicantId(2L);
        application.setApprovalStatus("PENDING");

        ProjectApplicationDTO dto = new ProjectApplicationDTO();
        dto.setDesiredPosition("后端开发");
        dto.setApplicantMajor("软件工程");

        when(projectApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectApplicationMapper.update(any(ProjectApplication.class))).thenReturn(1);

        // Act
        ProjectApplication result = projectDockingService.updateProjectApplication(1L, dto, 2L);

        // Assert
        assertNotNull(result);
        verify(projectApplicationMapper).update(any(ProjectApplication.class));
    }

    @Test
    void updateProjectApplication_withNonOwner_throwsException() {
        // Arrange
        ProjectApplication application = new ProjectApplication();
        application.setId(1L);
        application.setApplicantId(2L);

        when(projectApplicationMapper.selectById(1L)).thenReturn(application);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectDockingService.updateProjectApplication(1L, new ProjectApplicationDTO(), 999L));
        assertEquals("只能修改自己的申请", exception.getMessage());
    }

    @Test
    void withdrawProjectApplication_withValidData_withdrawsSuccessfully() {
        // Arrange
        ProjectApplication application = new ProjectApplication();
        application.setId(1L);
        application.setApplicantId(2L);
        application.setApprovalStatus("PENDING");

        when(projectApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectApplicationMapper.update(any(ProjectApplication.class))).thenReturn(1);

        // Act
        ProjectApplication result = projectDockingService.withdrawProjectApplication(1L, 2L);

        // Assert
        assertEquals("WITHDRAWN", result.getApprovalStatus());
        verify(projectApplicationMapper).update(any(ProjectApplication.class));
    }

    @Test
    void reviewProjectApplication_withApproval_approvesAndAddsMember() {
        // Arrange
        ProjectApplication application = new ProjectApplication();
        application.setId(1L);
        application.setProjectId(1L);
        application.setApplicantId(2L);
        application.setApplicantName("学生");
        application.setApplicationType("JOIN_TEAM");
        application.setApprovalStatus("PENDING");

        ProjectDockingReviewDTO dto = new ProjectDockingReviewDTO();
        dto.setApprovalStatus("APPROVED");
        dto.setApprovalComment("欢迎加入");

        when(projectApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(userMapper.selectById(1L)).thenReturn(leader);
        when(userMapper.selectById(2L)).thenReturn(student);
        when(teamMemberMapper.selectByTeamIdAndUserId(1L, 2L)).thenReturn(null);
        when(teamMemberMapper.insert(any(TeamMember.class))).thenReturn(1);
        when(projectApplicationMapper.update(any(ProjectApplication.class))).thenReturn(1);

        // Act
        ProjectApplication result = projectDockingService.reviewProjectApplication(1L, dto, 1L);

        // Assert
        assertEquals("APPROVED", result.getApprovalStatus());
        assertEquals("欢迎加入", result.getApprovalComment());
        verify(teamMemberMapper).insert(any(TeamMember.class));
        verify(emailService, atLeastOnce()).sendMail(any(), any(), any());
    }

    @Test
    void reviewProjectApplication_withNonLeader_throwsException() {
        // Arrange
        ProjectApplication application = new ProjectApplication();
        application.setId(1L);
        application.setProjectId(1L);
        application.setApplicationType("JOIN_TEAM");
        application.setApprovalStatus("PENDING");

        when(projectApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(teamMapper.selectById(1L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectDockingService.reviewProjectApplication(1L, new ProjectDockingReviewDTO(), 999L));
        assertEquals("只有项目负责人或团队负责人可以审批该申请", exception.getMessage());
    }

    @Test
    void getMyProjectApplications_returnsApplications() {
        // Arrange
        List<ProjectApplication> applications = new ArrayList<>();
        applications.add(new ProjectApplication());
        when(projectApplicationMapper.selectByApplicantId(2L)).thenReturn(applications);

        // Act
        List<ProjectApplication> result = projectDockingService.getMyProjectApplications(2L);

        // Assert
        assertEquals(1, result.size());
    }

    // ========== 招募相关测试 ==========

    @Test
    void createRecruitment_withValidData_returnsRecruitment() {
        // Arrange
        ProjectRecruitmentDTO dto = new ProjectRecruitmentDTO();
        dto.setPositionName("后端开发");
        dto.setTaskDescription("负责后端接口开发");

        when(projectMapper.selectById(1L)).thenReturn(project);
        when(userMapper.selectById(1L)).thenReturn(leader);
        when(projectRecruitmentMapper.insert(any(ProjectRecruitment.class))).thenReturn(1);

        // Act
        ProjectRecruitment result = projectDockingService.createRecruitment(1L, dto, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("OPEN", result.getStatus());
        verify(projectRecruitmentMapper).insert(any(ProjectRecruitment.class));
        verify(emailService, atLeastOnce()).sendMail(any(), any(), any());
    }

    @Test
    void createRecruitment_withNonLeader_throwsException() {
        // Arrange
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(teamMapper.selectById(1L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectDockingService.createRecruitment(1L, new ProjectRecruitmentDTO(), 999L));
        assertEquals("只有项目负责人或团队负责人可以发布招募", exception.getMessage());
    }

    @Test
    void closeRecruitment_withValidData_closesSuccessfully() {
        // Arrange
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectRecruitmentMapper.update(any(ProjectRecruitment.class))).thenReturn(1);

        // Act
        ProjectRecruitment result = projectDockingService.closeRecruitment(1L, 1L);

        // Assert
        assertEquals("CLOSED", result.getStatus());
        verify(projectRecruitmentMapper).update(any(ProjectRecruitment.class));
    }

    @Test
    void reopenRecruitment_withValidData_reopensSuccessfully() {
        // Arrange
        recruitment.setStatus("CLOSED");
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectRecruitmentMapper.update(any(ProjectRecruitment.class))).thenReturn(1);

        // Act
        ProjectRecruitment result = projectDockingService.reopenRecruitment(1L, 1L);

        // Assert
        assertEquals("OPEN", result.getStatus());
        verify(projectRecruitmentMapper).update(any(ProjectRecruitment.class));
    }

    @Test
    void deleteRecruitment_withValidData_deletesSuccessfully() {
        // Arrange
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectRecruitmentMapper.deleteById(1L)).thenReturn(1);

        // Act
        projectDockingService.deleteRecruitment(1L, 1L);

        // Assert
        verify(projectRecruitmentMapper).deleteById(1L);
    }

    @Test
    void getOpenRecruitments_returnsOpenRecruitments() {
        // Arrange
        List<ProjectRecruitment> recruitments = new ArrayList<>();
        recruitments.add(recruitment);
        when(projectRecruitmentMapper.selectByStatus("OPEN")).thenReturn(recruitments);

        // Act
        List<ProjectRecruitment> result = projectDockingService.getOpenRecruitments();

        // Assert
        assertEquals(1, result.size());
        assertEquals("OPEN", result.get(0).getStatus());
    }

    // ========== 招募申请相关测试 ==========

    @Test
    void applyToRecruitment_withValidData_returnsApplication() {
        // Arrange
        ProjectRecruitmentApplicationDTO dto = new ProjectRecruitmentApplicationDTO();
        dto.setDesiredPosition("前端开发");
        dto.setApplicantMajor("计算机");

        when(userMapper.selectById(2L)).thenReturn(student);
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(teamMemberMapper.selectByTeamIdAndUserId(1L, 2L)).thenReturn(null);
        when(projectRecruitmentApplicationMapper.selectByRecruitmentIdAndApplicantId(1L, 2L))
                .thenReturn(Collections.emptyList());
        when(projectRecruitmentApplicationMapper.insert(any(ProjectRecruitmentApplication.class))).thenReturn(1);
        when(projectRecruitmentApplicationMapper.selectById(any())).thenAnswer(inv -> {
            ProjectRecruitmentApplication app = new ProjectRecruitmentApplication();
            app.setId(inv.getArgument(0));
            app.setRecruitmentId(1L);
            app.setProjectId(1L);
            app.setApplicantId(2L);
            app.setApprovalStatus("PENDING");
            return app;
        });

        // Act
        ProjectRecruitmentApplication result = projectDockingService.applyToRecruitment(1L, dto, 2L);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getApprovalStatus());
        verify(projectRecruitmentApplicationMapper).insert(any(ProjectRecruitmentApplication.class));
    }

    @Test
    void applyToRecruitment_withClosedRecruitment_throwsException() {
        // Arrange
        recruitment.setStatus("CLOSED");
        when(userMapper.selectById(2L)).thenReturn(student);
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectDockingService.applyToRecruitment(1L, new ProjectRecruitmentApplicationDTO(), 2L));
        assertEquals("该招募已关闭", exception.getMessage());
    }

    @Test
    void updateRecruitmentApplication_withValidData_returnsUpdated() {
        // Arrange
        ProjectRecruitmentApplication application = new ProjectRecruitmentApplication();
        application.setId(1L);
        application.setApplicantId(2L);
        application.setApprovalStatus("PENDING");

        ProjectRecruitmentApplicationDTO dto = new ProjectRecruitmentApplicationDTO();
        dto.setDesiredPosition("测试工程师");

        when(projectRecruitmentApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectRecruitmentApplicationMapper.update(any(ProjectRecruitmentApplication.class))).thenReturn(1);

        // Act
        ProjectRecruitmentApplication result = projectDockingService.updateRecruitmentApplication(1L, dto, 2L);

        // Assert
        assertNotNull(result);
        verify(projectRecruitmentApplicationMapper).update(any(ProjectRecruitmentApplication.class));
    }

    @Test
    void withdrawRecruitmentApplication_withValidData_withdrawsSuccessfully() {
        // Arrange
        ProjectRecruitmentApplication application = new ProjectRecruitmentApplication();
        application.setId(1L);
        application.setApplicantId(2L);
        application.setApprovalStatus("PENDING");

        when(projectRecruitmentApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectRecruitmentApplicationMapper.update(any(ProjectRecruitmentApplication.class))).thenReturn(1);

        // Act
        ProjectRecruitmentApplication result = projectDockingService.withdrawRecruitmentApplication(1L, 2L);

        // Assert
        assertEquals("WITHDRAWN", result.getApprovalStatus());
    }

    @Test
    void reviewRecruitmentApplication_withApproval_approvesAndAddsMember() {
        // Arrange
        ProjectRecruitmentApplication application = new ProjectRecruitmentApplication();
        application.setId(1L);
        application.setRecruitmentId(1L);
        application.setProjectId(1L);
        application.setApplicantId(2L);
        application.setApplicantName("学生");
        application.setApprovalStatus("PENDING");

        ProjectDockingReviewDTO dto = new ProjectDockingReviewDTO();
        dto.setApprovalStatus("APPROVED");

        when(projectRecruitmentApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(userMapper.selectById(1L)).thenReturn(leader);
        when(userMapper.selectById(2L)).thenReturn(student);
        when(teamMemberMapper.selectByTeamIdAndUserId(1L, 2L)).thenReturn(null);
        when(teamMemberMapper.insert(any(TeamMember.class))).thenReturn(1);
        when(projectRecruitmentApplicationMapper.update(any(ProjectRecruitmentApplication.class))).thenReturn(1);

        // Act
        ProjectRecruitmentApplication result = projectDockingService.reviewRecruitmentApplication(1L, dto, 1L);

        // Assert
        assertEquals("APPROVED", result.getApprovalStatus());
        verify(teamMemberMapper).insert(any(TeamMember.class));
        verify(emailService, atLeastOnce()).sendMail(any(), any(), any());
    }

    @Test
    void reviewRecruitmentApplication_withRejection_rejectsWithoutAddingMember() {
        // Arrange
        ProjectRecruitmentApplication application = new ProjectRecruitmentApplication();
        application.setId(1L);
        application.setRecruitmentId(1L);
        application.setProjectId(1L);
        application.setApplicantId(2L);
        application.setApprovalStatus("PENDING");

        ProjectDockingReviewDTO dto = new ProjectDockingReviewDTO();
        dto.setApprovalStatus("REJECTED");
        dto.setApprovalComment("不符合要求");

        when(projectRecruitmentApplicationMapper.selectById(1L)).thenReturn(application);
        when(projectRecruitmentMapper.selectById(1L)).thenReturn(recruitment);
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(userMapper.selectById(1L)).thenReturn(leader);
        when(userMapper.selectById(2L)).thenReturn(student);
        when(projectRecruitmentApplicationMapper.update(any(ProjectRecruitmentApplication.class))).thenReturn(1);

        // Act
        ProjectRecruitmentApplication result = projectDockingService.reviewRecruitmentApplication(1L, dto, 1L);

        // Assert
        assertEquals("REJECTED", result.getApprovalStatus());
        assertEquals("不符合要求", result.getApprovalComment());
        verify(teamMemberMapper, never()).insert(any(TeamMember.class));
        verify(emailService, atLeastOnce()).sendMail(any(), any(), any());
    }

    @Test
    void getMyRecruitmentApplications_returnsApplications() {
        // Arrange
        List<ProjectRecruitmentApplication> applications = new ArrayList<>();
        applications.add(new ProjectRecruitmentApplication());
        when(projectRecruitmentApplicationMapper.selectByApplicantId(2L)).thenReturn(applications);

        // Act
        List<ProjectRecruitmentApplication> result = projectDockingService.getMyRecruitmentApplications(2L);

        // Assert
        assertEquals(1, result.size());
    }
}

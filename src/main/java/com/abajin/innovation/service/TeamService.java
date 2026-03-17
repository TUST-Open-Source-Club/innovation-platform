package com.abajin.innovation.service;

import com.abajin.innovation.dto.TeamExportDTO;
import com.abajin.innovation.dto.TeamImportDTO;
import com.abajin.innovation.mapper.TeamMemberMapper;
import com.abajin.innovation.entity.Team;
import com.abajin.innovation.entity.TeamMember;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.TeamMapper;
import com.abajin.innovation.mapper.UserMapper;
import com.alibaba.excel.EasyExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abajin.innovation.listener.TeamImportListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 团队服务类
 */
@Service
public class TeamService {
    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public Team createTeam(Team team, Long leaderId) {
        User leader = userMapper.selectById(leaderId);
        if (leader == null) {
            throw new RuntimeException("用户不存在");
        }

        team.setLeaderId(leaderId);
        team.setLeaderName(leader.getRealName());
        team.setMemberCount(1);
        if (team.getRecruiting() == null) team.setRecruiting(true);
        if (team.getIsPublic() == null) team.setIsPublic(true);
        if (Boolean.TRUE.equals(team.getRecruiting()) && (team.getRecruitmentRequirement() == null || team.getRecruitmentRequirement().trim().isEmpty())) {
            throw new RuntimeException("开启招募时，招募内容不能为空");
        }
        if (team.getCollegeName() == null || team.getCollegeName().trim().isEmpty()) {
            team.setCollegeName(leader.getCollegeName());
        }
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());

        teamMapper.insert(team);

        // 添加队长为成员（队长无需审批，直接通过）
        TeamMember leaderMember = new TeamMember();
        leaderMember.setTeamId(team.getId());
        leaderMember.setUserId(leaderId);
        leaderMember.setUserName(leader.getRealName());
        leaderMember.setRole("LEADER");
        leaderMember.setApprovalStatus("APPROVED");
        leaderMember.setStatus("ACTIVE");
        leaderMember.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(leaderMember);

        return team;
    }

    @Transactional
    public Team updateTeam(Long id, Team team, Long userId) {
        Team existingTeam = teamMapper.selectById(id);
        if (existingTeam == null) {
            throw new RuntimeException("团队不存在");
        }
        if (!existingTeam.getLeaderId().equals(userId)) {
            throw new RuntimeException("无权修改此团队");
        }
        boolean recruiting = team.getRecruiting() != null ? team.getRecruiting() : existingTeam.getRecruiting();
        String requirement = team.getRecruitmentRequirement() != null ? team.getRecruitmentRequirement() : existingTeam.getRecruitmentRequirement();
        if (Boolean.TRUE.equals(recruiting) && (requirement == null || requirement.trim().isEmpty())) {
            throw new RuntimeException("开启招募时，招募内容不能为空");
        }
        team.setId(id);
        team.setUpdateTime(LocalDateTime.now());
        teamMapper.update(team);
        return teamMapper.selectById(id);
    }

    @Transactional
    public void addMember(Long teamId, Long userId, Long operatorId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        if (!team.getLeaderId().equals(operatorId)) {
            throw new RuntimeException("只有队长可以添加成员");
        }

        // 检查是否已经是成员
        TeamMember existingMember = teamMemberMapper.selectByTeamIdAndUserId(teamId, userId);
        if (existingMember != null) {
            throw new RuntimeException("该用户已经是团队成员");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setUserName(user.getRealName());
        member.setRole("MEMBER");
        member.setApprovalStatus("APPROVED");
        member.setStatus("ACTIVE");
        member.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(member);

        // 更新成员数量
        team.setMemberCount(team.getMemberCount() + 1);
        teamMapper.update(team);
    }

    @Transactional
    public void removeMember(Long teamId, Long memberId, Long operatorId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        if (!team.getLeaderId().equals(operatorId)) {
            throw new RuntimeException("只有队长可以移除成员");
        }

        TeamMember member = teamMemberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("成员记录不存在");
        }
        if (!member.getTeamId().equals(teamId)) {
            throw new RuntimeException("成员不属于该团队");
        }
        if ("LEADER".equals(member.getRole())) {
            throw new RuntimeException("不能移除队长");
        }

        // 如果成员已通过审批，需要更新成员数量
        boolean wasApproved = "APPROVED".equals(member.getApprovalStatus());
        
        teamMemberMapper.deleteByTeamIdAndUserId(teamId, member.getUserId());

        // 更新成员数量（只减少已通过审批的成员）
        if (wasApproved) {
            team.setMemberCount(team.getMemberCount() - 1);
            teamMapper.update(team);
        }
    }

    public Team getTeamById(Long id) {
        return teamMapper.selectById(id);
    }

    /**
     * 获取团队详情；若团队不公开则仅成员可见
     */
    public Team getTeamById(Long id, Long userId) {
        Team team = teamMapper.selectById(id);
        if (team == null) return null;
        if (Boolean.FALSE.equals(team.getIsPublic())) {
            if (userId == null) return null;
            if (!team.getLeaderId().equals(userId) && !isMember(id, userId)) {
                return null;
            }
        }
        return team;
    }

    public List<Team> getTeamsByLeaderId(Long leaderId) {
        return teamMapper.selectByLeaderId(leaderId);
    }

    public List<TeamMember> getTeamMembers(Long teamId) {
        // 只返回已通过审批的成员
        List<TeamMember> allMembers = teamMemberMapper.selectByTeamId(teamId);
        return allMembers.stream()
                .filter(m -> "APPROVED".equals(m.getApprovalStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 获取团队成员（包括待审批的）
     */
    public List<TeamMember> getAllTeamMembers(Long teamId) {
        return teamMemberMapper.selectByTeamId(teamId);
    }

    public List<Team> getAllTeams() {
        return teamMapper.selectAll();
    }

    /**
     * 获取团队列表；不公开的团队仅成员可见
     */
    public List<Team> getAllTeams(Long userId) {
        List<Team> all = teamMapper.selectAll();
        if (userId == null) {
            return all.stream().filter(t -> Boolean.TRUE.equals(t.getIsPublic())).collect(Collectors.toList());
        }
        return all.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsPublic()) || t.getLeaderId().equals(userId) || isMember(t.getId(), userId))
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否是团队成员
     */
    public boolean isMember(Long teamId, Long userId) {
        TeamMember member = teamMemberMapper.selectByTeamIdAndUserId(teamId, userId);
        return member != null && "APPROVED".equals(member.getApprovalStatus());
    }

    /**
     * 队长审批团队成员申请
     */
    @Transactional
    public TeamMember reviewMemberApplication(Long teamId, Long memberId, String approvalStatus, Long leaderId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        if (!team.getLeaderId().equals(leaderId)) {
            throw new RuntimeException("只有队长可以审批申请");
        }

        TeamMember member = teamMemberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("申请记录不存在");
        }
        if (!member.getTeamId().equals(teamId)) {
            throw new RuntimeException("申请记录不属于该团队");
        }
        if (!"PENDING".equals(member.getApprovalStatus())) {
            throw new RuntimeException("该申请已处理");
        }

        member.setApprovalStatus(approvalStatus);
        teamMemberMapper.update(member);

        // 如果通过，更新团队成员数量
        if ("APPROVED".equals(approvalStatus)) {
            team.setMemberCount(team.getMemberCount() + 1);
            teamMapper.update(team);
        }

        return member;
    }

    /**
     * 获取待审批的成员申请列表
     */
    public List<TeamMember> getPendingApplications(Long teamId, Long leaderId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        if (!team.getLeaderId().equals(leaderId)) {
            throw new RuntimeException("只有队长可以查看申请列表");
        }

        List<TeamMember> allMembers = teamMemberMapper.selectByTeamId(teamId);
        return allMembers.stream()
                .filter(m -> "PENDING".equals(m.getApprovalStatus()))
                .collect(Collectors.toList());
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出团队列表为 Excel（管理员）
     */
    public void exportTeamsToExcel(OutputStream outputStream) {
        List<Team> teams = teamMapper.selectAll();
        List<TeamExportDTO> rows = teams.stream().map(t -> {
            TeamExportDTO dto = new TeamExportDTO();
            dto.setName(t.getName());
            dto.setTeamType(t.getTeamType());
            dto.setDescription(t.getDescription());
            dto.setLeaderId(t.getLeaderId());
            dto.setLeaderName(t.getLeaderName());
            dto.setMemberCount(t.getMemberCount());
            dto.setLeaderStudentId(t.getLeaderStudentId());
            dto.setCollegeName(t.getCollegeName());
            dto.setInstructorName(t.getInstructorName());
            dto.setRecruiting(Boolean.TRUE.equals(t.getRecruiting()) ? "是" : "否");
            dto.setIsPublic(Boolean.TRUE.equals(t.getIsPublic()) ? "是" : "否");
            dto.setRecruitmentRequirement(t.getRecruitmentRequirement());
            dto.setHonors(t.getHonors());
            dto.setTags(t.getTags());
            dto.setCreateTime(t.getCreateTime() != null ? t.getCreateTime().format(DATE_FORMAT) : "");
            return dto;
        }).collect(Collectors.toList());
        EasyExcel.write(outputStream, TeamExportDTO.class).sheet("团队列表").doWrite(rows);
    }

    /**
     * 从 Excel 导入并批量添加团队（管理员）；负责人通过“负责人用户名”列指定
     */
    @Transactional
    public int importTeamsFromExcel(InputStream inputStream) {
        TeamImportListener listener = new TeamImportListener();
        EasyExcel.read(inputStream, TeamImportDTO.class, listener).sheet().doRead();
        List<TeamImportDTO> list = listener.getList();

        int count = 0;
        for (TeamImportDTO row : list) {
            String realName = row.getLeaderRealName();
            if (realName == null || realName.trim().isEmpty()) {
                continue;
            }
            List<User> candidates = userMapper.selectByRealName(realName.trim());
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("负责人姓名不存在：" + realName);
            }
            if (candidates.size() > 1) {
                throw new RuntimeException("负责人姓名不唯一，请改为使用用户名或限制为唯一姓名：" + realName);
            }
            User leader = candidates.get(0);
            Team team = new Team();
            team.setName(row.getName().trim());
            team.setDescription(trimEmptyToNull(row.getDescription()));
            team.setRecruiting(parseYesNo(row.getRecruiting(), true));
            team.setIsPublic(parseYesNo(row.getIsPublic(), true));
            team.setRecruitmentRequirement(trimEmptyToNull(row.getRecruitmentRequirement()));
            if (Boolean.TRUE.equals(team.getRecruiting()) && (team.getRecruitmentRequirement() == null || team.getRecruitmentRequirement().isEmpty())) {
                throw new RuntimeException("第" + (count + 1) + "行：开启招募时，招募要求不能为空");
            }
            team.setHonors(trimEmptyToNull(row.getHonors()));
            team.setTags(trimEmptyToNull(row.getTags()));
            team.setInstructorName(trimEmptyToNull(row.getInstructorName()));
            team.setLeaderStudentId(trimEmptyToNull(row.getLeaderStudentId()));
            team.setCollegeName(trimEmptyToNull(row.getCollegeName()));
            team.setTeamType(trimEmptyToNull(row.getTeamType()));
            createTeam(team, leader.getId());
            count++;
        }
        return count;
    }

    private static String trimEmptyToNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return s.trim();
    }

    private static Boolean parseYesNo(String s, boolean defaultValue) {
        if (s == null || s.trim().isEmpty()) return defaultValue;
        String v = s.trim();
        if ("是".equals(v) || "1".equals(v) || "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v)) return true;
        if ("否".equals(v) || "0".equals(v) || "false".equalsIgnoreCase(v) || "no".equalsIgnoreCase(v)) return false;
        return defaultValue;
    }

    /**
     * 软删除团队（管理员）
     */
    @Transactional
    public void softDeleteTeam(Long teamId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        teamMapper.softDeleteById(teamId);
    }

    /**
     * 批量软删除团队（管理员）
     */
    @Transactional
    public int softDeleteTeams(List<Long> teamIds) {
        if (teamIds == null || teamIds.isEmpty()) {
            throw new RuntimeException("请选择要删除的团队");
        }
        return teamMapper.softDeleteBatch(teamIds);
    }
}

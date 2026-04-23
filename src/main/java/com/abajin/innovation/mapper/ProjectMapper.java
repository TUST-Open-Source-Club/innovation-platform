package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目Mapper接口
 */
@Mapper
public interface ProjectMapper {
    /**
     * 根据ID查询项目
     */
    Project selectById(@Param("id") Long id);

    /**
     * 根据负责人ID查询项目列表
     */
    List<Project> selectByLeaderId(@Param("leaderId") Long leaderId);

    /**
     * 根据团队ID查询项目列表
     */
    List<Project> selectByTeamId(@Param("teamId") Long teamId);

    /**
     * 根据状态查询项目列表
     */
    List<Project> selectByStatus(@Param("status") String status);

    /**
     * 根据状态+审批状态查询项目列表
     */
    List<Project> selectByStatusAndApprovalStatus(@Param("status") String status, @Param("approvalStatus") String approvalStatus);

    /**
     * 根据审批状态查询项目列表
     */
    List<Project> selectByApprovalStatus(@Param("approvalStatus") String approvalStatus);

    /**
     * 插入项目
     */
    int insert(Project project);

    /**
     * 更新项目
     */
    int update(Project project);

    /**
     * 删除项目
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有项目
     */
    List<Project> selectAll();

    /**
     * 查询无人接管项目（负责人虚位以待）
     */
    List<Project> selectUnclaimed();

    /**
     * 清空上一任负责人信息（更换/接管后）
     */
    int clearPreviousLeader(@Param("id") Long projectId);
}

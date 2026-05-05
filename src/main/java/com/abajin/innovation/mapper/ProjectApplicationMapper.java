package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.ProjectApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目申请Mapper接口
 */
@Mapper
public interface ProjectApplicationMapper {
    /**
     * 根据ID查询申请
     */
    ProjectApplication selectById(@Param("id") Long id);

    /**
     * 根据项目ID查询申请列表
     */
    List<ProjectApplication> selectByProjectId(@Param("projectId") Long projectId);

    List<ProjectApplication> selectByProjectIdAndType(@Param("projectId") Long projectId,
                                                      @Param("applicationType") String applicationType);

    List<ProjectApplication> selectByProjectIdAndApplicantIdAndType(@Param("projectId") Long projectId,
                                                                    @Param("applicantId") Long applicantId,
                                                                    @Param("applicationType") String applicationType);
    /**
     * 根据申请人ID查询申请列表
     */
    List<ProjectApplication> selectByApplicantId(@Param("applicantId") Long applicantId);

    /**
     * 查询待审批申请列表（根据审批人角色）
     */
    List<ProjectApplication> selectPendingApprovals(@Param("approverRole") String approverRole);

    /**
     * 插入申请
     */
    int insert(ProjectApplication application);

    /**
     * 更新申请
     */
    int update(ProjectApplication application);

    /**
     * 删除申请
     */
    int deleteById(@Param("id") Long id);
}

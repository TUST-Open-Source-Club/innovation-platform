package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.ProjectRecruitment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectRecruitmentMapper {
    ProjectRecruitment selectById(@Param("id") Long id);

    List<ProjectRecruitment> selectByProjectId(@Param("projectId") Long projectId);

    int insert(ProjectRecruitment recruitment);

    int update(ProjectRecruitment recruitment);

    int deleteById(@Param("id") Long id);
}

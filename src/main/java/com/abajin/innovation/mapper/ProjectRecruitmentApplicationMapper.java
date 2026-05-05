package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.ProjectRecruitmentApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectRecruitmentApplicationMapper {
    ProjectRecruitmentApplication selectById(@Param("id") Long id);

    List<ProjectRecruitmentApplication> selectByProjectId(@Param("projectId") Long projectId);

    List<ProjectRecruitmentApplication> selectByProjectIdAndApplicantId(@Param("projectId") Long projectId,
                                                                        @Param("applicantId") Long applicantId);

    List<ProjectRecruitmentApplication> selectByRecruitmentId(@Param("recruitmentId") Long recruitmentId);

    List<ProjectRecruitmentApplication> selectByRecruitmentIdAndApplicantId(@Param("recruitmentId") Long recruitmentId,
                                                                            @Param("applicantId") Long applicantId);

    int insert(ProjectRecruitmentApplication application);

    int update(ProjectRecruitmentApplication application);
}

package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.TeamRecruitmentApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamRecruitmentApplicationMapper {
    TeamRecruitmentApplication selectById(@Param("id") Long id);

    List<TeamRecruitmentApplication> selectByTeamId(@Param("teamId") Long teamId);

    List<TeamRecruitmentApplication> selectByTeamIdAndApplicantId(@Param("teamId") Long teamId,
                                                                  @Param("applicantId") Long applicantId);

    List<TeamRecruitmentApplication> selectByRecruitmentId(@Param("recruitmentId") Long recruitmentId);

    List<TeamRecruitmentApplication> selectByRecruitmentIdAndApplicantId(@Param("recruitmentId") Long recruitmentId,
                                                                         @Param("applicantId") Long applicantId);

    List<TeamRecruitmentApplication> selectByApplicantId(@Param("applicantId") Long applicantId);

    int insert(TeamRecruitmentApplication application);

    int update(TeamRecruitmentApplication application);
}

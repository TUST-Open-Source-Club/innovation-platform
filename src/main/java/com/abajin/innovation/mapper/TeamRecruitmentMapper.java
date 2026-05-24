package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.TeamRecruitment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamRecruitmentMapper {
    TeamRecruitment selectById(@Param("id") Long id);

    List<TeamRecruitment> selectByTeamId(@Param("teamId") Long teamId);

    List<TeamRecruitment> selectByStatus(@Param("status") String status);

    List<TeamRecruitment> selectAll();

    int insert(TeamRecruitment recruitment);

    int update(TeamRecruitment recruitment);

    int deleteById(@Param("id") Long id);
}

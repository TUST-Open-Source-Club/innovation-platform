package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 团队Mapper接口
 */
@Mapper
public interface TeamMapper {
    /**
     * 根据ID查询团队
     */
    Team selectById(@Param("id") Long id);

    /**
     * 根据队长ID查询团队列表
     */
    List<Team> selectByLeaderId(@Param("leaderId") Long leaderId);

    /**
     * 插入团队
     */
    int insert(Team team);

    /**
     * 更新团队
     */
    int update(Team team);

    /**
     * 删除团队（硬删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有团队
     */
    List<Team> selectAll();

    /**
     * 软删除团队
     */
    int softDeleteById(@Param("id") Long id);

    /**
     * 批量软删除团队
     */
    int softDeleteBatch(@Param("list") List<Long> ids);
}

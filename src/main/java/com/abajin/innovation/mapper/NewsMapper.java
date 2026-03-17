package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻Mapper接口
 */
@Mapper
public interface NewsMapper {
    /**
     * 根据ID查询新闻
     */
    News selectById(@Param("id") Long id);

    /**
     * 分页查询新闻
     */
    List<News> selectPage(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("title") String title,
            @Param("status") String status,
            @Param("approvalStatus") String approvalStatus,
            @Param("categoryId") Long categoryId,
            @Param("authorId") Long authorId
    );

    /**
     * 统计新闻总数
     */
    int count(
            @Param("title") String title,
            @Param("status") String status,
            @Param("approvalStatus") String approvalStatus,
            @Param("categoryId") Long categoryId,
            @Param("authorId") Long authorId
    );

    /**
     * 插入新闻
     */
    int insert(News news);

    /**
     * 更新新闻
     */
    int update(News news);

    /**
     * 删除新闻（硬删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 增加浏览次数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 软删除新闻
     */
    int softDeleteById(@Param("id") Long id);

    /**
     * 批量软删除新闻
     */
    int softDeleteBatch(@Param("list") List<Long> ids);
}

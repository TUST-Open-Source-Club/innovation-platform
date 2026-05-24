package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工单Mapper接口
 */
@Mapper
public interface TicketMapper {

    Ticket selectById(@Param("id") Long id);

    List<Ticket> selectPage(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("typeId") Long typeId,
            @Param("statusId") Long statusId,
            @Param("urgency") String urgency,
            @Param("keyword") String keyword,
            @Param("creatorId") Long creatorId,
            @Param("collegeId") Long collegeId
    );

    int count(
            @Param("typeId") Long typeId,
            @Param("statusId") Long statusId,
            @Param("urgency") String urgency,
            @Param("keyword") String keyword,
            @Param("creatorId") Long creatorId,
            @Param("collegeId") Long collegeId
    );

    int insert(Ticket ticket);

    int update(Ticket ticket);

    int softDeleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("statusId") Long statusId, @Param("updateTime") java.time.LocalDateTime updateTime);

    List<Map<String, Object>> countByType();

    List<Map<String, Object>> countByStatus();

    List<Map<String, Object>> countByUrgency();

    Map<String, Object> countSatisfaction();

    Map<String, Object> countCompletionRate();

    int countTotal();
}

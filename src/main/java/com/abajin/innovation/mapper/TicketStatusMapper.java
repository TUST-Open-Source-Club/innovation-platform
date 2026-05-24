package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.TicketStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工单状态Mapper接口
 */
@Mapper
public interface TicketStatusMapper {

    List<TicketStatus> selectAll();

    TicketStatus selectById(@Param("id") Long id);

    TicketStatus selectByName(@Param("name") String name);

    int insert(TicketStatus ticketStatus);

    int update(TicketStatus ticketStatus);

    int deleteById(@Param("id") Long id);

    int countTicketsByStatusId(@Param("statusId") Long statusId);
}

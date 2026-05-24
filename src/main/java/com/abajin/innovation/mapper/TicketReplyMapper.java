package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.TicketReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工单回复Mapper接口
 */
@Mapper
public interface TicketReplyMapper {

    List<TicketReply> selectByTicketId(@Param("ticketId") Long ticketId);

    TicketReply selectById(@Param("id") Long id);

    int insert(TicketReply ticketReply);

    int softDeleteById(@Param("id") Long id);

    int softDeleteByParentId(@Param("parentId") Long parentId);

    int softDeleteByTicketId(@Param("ticketId") Long ticketId);
}

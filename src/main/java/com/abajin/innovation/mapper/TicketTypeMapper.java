package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.TicketType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工单类型Mapper接口
 */
@Mapper
public interface TicketTypeMapper {

    List<TicketType> selectAll();

    TicketType selectById(@Param("id") Long id);

    TicketType selectByName(@Param("name") String name);

    int insert(TicketType ticketType);

    int update(TicketType ticketType);

    int deleteById(@Param("id") Long id);

    int countTicketsByTypeId(@Param("typeId") Long typeId);
}

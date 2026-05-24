package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.TicketEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工单评价Mapper接口
 */
@Mapper
public interface TicketEvaluationMapper {

    TicketEvaluation selectByTicketId(@Param("ticketId") Long ticketId);

    int insert(TicketEvaluation ticketEvaluation);

    int update(TicketEvaluation ticketEvaluation);
}

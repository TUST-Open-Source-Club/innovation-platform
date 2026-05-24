package com.abajin.innovation.service;

import com.abajin.innovation.dto.TicketEvaluationDTO;
import com.abajin.innovation.entity.Ticket;
import com.abajin.innovation.entity.TicketEvaluation;
import com.abajin.innovation.entity.TicketStatus;
import com.abajin.innovation.mapper.TicketEvaluationMapper;
import com.abajin.innovation.mapper.TicketMapper;
import com.abajin.innovation.mapper.TicketStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 工单评价服务类
 */
@Slf4j
@Service
public class TicketEvaluationService {

    @Autowired
    private TicketEvaluationMapper ticketEvaluationMapper;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketStatusMapper ticketStatusMapper;

    /**
     * 创建评价
     */
    public TicketEvaluation createEvaluation(Long ticketId, TicketEvaluationDTO dto, Long userId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        // 只有工单创建者可以评价
        if (!ticket.getCreatorId().equals(userId)) {
            throw new RuntimeException("只有工单创建者才能评价");
        }
        // 只有已完结的工单可以评价
        TicketStatus status = ticketStatusMapper.selectById(ticket.getStatusId());
        if (status == null || !"已完结".equals(status.getName())) {
            throw new RuntimeException("只有已完结的工单才能评价");
        }
        // 检查是否已评价
        TicketEvaluation existing = ticketEvaluationMapper.selectByTicketId(ticketId);
        if (existing != null) {
            throw new RuntimeException("该工单已评价");
        }
        // 校验满意度值
        if (!"SATISFIED".equals(dto.getSatisfaction()) && !"DISSATISFIED".equals(dto.getSatisfaction())) {
            throw new RuntimeException("满意度值无效，只能是SATISFIED或DISSATISFIED");
        }

        TicketEvaluation evaluation = new TicketEvaluation();
        evaluation.setTicketId(ticketId);
        evaluation.setSatisfaction(dto.getSatisfaction());
        evaluation.setContent(dto.getContent());
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());

        ticketEvaluationMapper.insert(evaluation);
        return ticketEvaluationMapper.selectByTicketId(ticketId);
    }

    /**
     * 查询评价
     */
    public TicketEvaluation getEvaluation(Long ticketId) {
        return ticketEvaluationMapper.selectByTicketId(ticketId);
    }

    /**
     * 更新评价（24小时内可修改）
     */
    public TicketEvaluation updateEvaluation(Long ticketId, TicketEvaluationDTO dto, Long userId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        if (!ticket.getCreatorId().equals(userId)) {
            throw new RuntimeException("只有工单创建者才能修改评价");
        }

        TicketEvaluation existing = ticketEvaluationMapper.selectByTicketId(ticketId);
        if (existing == null) {
            throw new RuntimeException("该工单尚未评价");
        }

        // 检查是否在24小时内
        long hoursSinceCreation = ChronoUnit.HOURS.between(existing.getCreateTime(), LocalDateTime.now());
        if (hoursSinceCreation > 24) {
            throw new RuntimeException("已超过24小时修改时限");
        }

        // 校验满意度值
        if (dto.getSatisfaction() != null &&
            !"SATISFIED".equals(dto.getSatisfaction()) &&
            !"DISSATISFIED".equals(dto.getSatisfaction())) {
            throw new RuntimeException("满意度值无效，只能是SATISFIED或DISSATISFIED");
        }

        if (dto.getSatisfaction() != null) existing.setSatisfaction(dto.getSatisfaction());
        if (dto.getContent() != null) existing.setContent(dto.getContent());
        existing.setUpdateTime(LocalDateTime.now());

        ticketEvaluationMapper.update(existing);
        return ticketEvaluationMapper.selectByTicketId(ticketId);
    }
}

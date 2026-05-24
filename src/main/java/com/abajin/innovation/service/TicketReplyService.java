package com.abajin.innovation.service;

import com.abajin.innovation.dto.CreateTicketReplyDTO;
import com.abajin.innovation.entity.Ticket;
import com.abajin.innovation.entity.TicketReply;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.TicketStatusEnum;
import com.abajin.innovation.mapper.TicketMapper;
import com.abajin.innovation.mapper.TicketReplyMapper;
import com.abajin.innovation.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单回复服务类
 */
@Slf4j
@Service
public class TicketReplyService {

    @Autowired
    private TicketReplyMapper ticketReplyMapper;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建回复
     */
    @Transactional
    public TicketReply createReply(Long ticketId, CreateTicketReplyDTO dto, Long userId) {
        log.info("创建回复开始: ticketId={}, userId={}, content={}", ticketId, userId,
                dto.getContent() != null ? dto.getContent().substring(0, Math.min(50, dto.getContent().length())) + "..." : "null");

        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            log.error("工单不存在: ticketId={}", ticketId);
            throw new RuntimeException("工单不存在");
        }
        log.info("查询到工单: id={}, statusId={}", ticket.getId(), ticket.getStatusId());

        User user = userMapper.selectById(userId);
        if (user == null) {
            log.error("用户不存在: userId={}", userId);
            throw new RuntimeException("用户不存在");
        }

        // 验证父回复是否存在（如果指定了parent_id）
        if (dto.getParentId() != null) {
            TicketReply parentReply = ticketReplyMapper.selectById(dto.getParentId());
            if (parentReply == null) {
                throw new RuntimeException("父回复不存在");
            }
        }

        TicketReply reply = new TicketReply();
        reply.setTicketId(ticketId);
        reply.setParentId(dto.getParentId());
        reply.setContent(dto.getContent());
        reply.setCreatorId(userId);
        reply.setIsDeleted(0);
        reply.setCreateTime(LocalDateTime.now());
        reply.setUpdateTime(LocalDateTime.now());

        int rows = ticketReplyMapper.insert(reply);
        log.info("插入回复结果: rows={}, generatedId={}", rows, reply.getId());
        if (rows == 0) {
            log.error("回复插入失败，影响行数为0");
            throw new RuntimeException("回复保存失败");
        }
        if (reply.getId() == null) {
            log.error("回复插入后未获取到自增ID");
            throw new RuntimeException("回复保存失败：未生成ID");
        }

        // 自动变更工单状态
        autoUpdateTicketStatus(ticket, userId);

        TicketReply result = ticketReplyMapper.selectById(reply.getId());
        log.info("回复创建完成: replyId={}", result != null ? result.getId() : "null");
        return result;
    }

    /**
     * 自动变更工单状态
     * 管理员回复：已受理→待回复
     * 工单创建者回复：待回复→已受理
     */
    private void autoUpdateTicketStatus(Ticket ticket, Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) return;

            boolean isAdmin = "SCHOOL_ADMIN".equals(user.getRole()) || "COLLEGE_ADMIN".equals(user.getRole());
            boolean isCreator = ticket.getCreatorId() != null && ticket.getCreatorId().equals(userId);
            TicketStatusEnum statusId = ticket.getStatusId();

            if (statusId == null) {
                log.warn("工单状态为空，跳过自动状态更新: ticketId={}", ticket.getId());
                return;
            }

            if (isAdmin && statusId == TicketStatusEnum.ACCEPTED) {
                ticketMapper.updateStatus(ticket.getId(), TicketStatusEnum.PENDING_REPLY, LocalDateTime.now());
                log.info("管理员回复，工单状态已更新为待回复: ticketId={}", ticket.getId());
            } else if (isCreator && statusId == TicketStatusEnum.PENDING_REPLY) {
                ticketMapper.updateStatus(ticket.getId(), TicketStatusEnum.ACCEPTED, LocalDateTime.now());
                log.info("创建者回复，工单状态已更新为已受理: ticketId={}", ticket.getId());
            }
        } catch (Exception e) {
            log.error("自动更新工单状态失败: ticketId={}, userId={}, error={}", ticket.getId(), userId, e.getMessage(), e);
            // 状态更新失败不应影响回复创建，不抛出异常
        }
    }

    /**
     * 删除回复（级联删除子回复）
     */
    @Transactional
    public void deleteReply(Long replyId, Long userId, String userRole) {
        TicketReply reply = ticketReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new RuntimeException("回复不存在");
        }
        // 只有回复创建者或管理员可以删除
        if (!reply.getCreatorId().equals(userId) && !"SCHOOL_ADMIN".equals(userRole)) {
            throw new RuntimeException("无权删除此回复");
        }
        // 级联删除子回复
        cascadeDeleteReplies(replyId);
    }

    private void cascadeDeleteReplies(Long replyId) {
        // 先删除子回复
        ticketReplyMapper.softDeleteByParentId(replyId);
        // 再删除自身
        ticketReplyMapper.softDeleteById(replyId);
    }
}

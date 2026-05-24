package com.abajin.innovation.service;

import com.abajin.innovation.dto.CreateTicketReplyDTO;
import com.abajin.innovation.entity.Ticket;
import com.abajin.innovation.entity.TicketReply;
import com.abajin.innovation.entity.TicketStatus;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.TicketMapper;
import com.abajin.innovation.mapper.TicketReplyMapper;
import com.abajin.innovation.mapper.TicketStatusMapper;
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
    private TicketStatusMapper ticketStatusMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建回复
     */
    @Transactional
    public TicketReply createReply(Long ticketId, CreateTicketReplyDTO dto, Long userId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
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

        ticketReplyMapper.insert(reply);

        // 自动变更工单状态
        autoUpdateTicketStatus(ticket, userId);

        return ticketReplyMapper.selectById(reply.getId());
    }

    /**
     * 自动变更工单状态
     * 管理员回复：已受理→待回复
     * 工单创建者回复：待回复→已受理
     */
    private void autoUpdateTicketStatus(Ticket ticket, Long userId) {
        TicketStatus currentStatus = ticketStatusMapper.selectById(ticket.getStatusId());
        if (currentStatus == null) return;

        User user = userMapper.selectById(userId);
        if (user == null) return;

        boolean isAdmin = "SCHOOL_ADMIN".equals(user.getRole()) || "COLLEGE_ADMIN".equals(user.getRole());
        boolean isCreator = ticket.getCreatorId().equals(userId);

        if (isAdmin && "已受理".equals(currentStatus.getName())) {
            TicketStatus pendingStatus = ticketStatusMapper.selectByName("待回复");
            if (pendingStatus != null) {
                ticketMapper.updateStatus(ticket.getId(), pendingStatus.getId(), LocalDateTime.now());
            }
        } else if (isCreator && "待回复".equals(currentStatus.getName())) {
            TicketStatus acceptedStatus = ticketStatusMapper.selectByName("已受理");
            if (acceptedStatus != null) {
                ticketMapper.updateStatus(ticket.getId(), acceptedStatus.getId(), LocalDateTime.now());
            }
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

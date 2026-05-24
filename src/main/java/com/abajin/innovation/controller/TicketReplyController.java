package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.CreateTicketReplyDTO;
import com.abajin.innovation.entity.TicketReply;
import com.abajin.innovation.service.TicketReplyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工单回复控制器
 */
@RestController
@RequestMapping("/tickets/{ticketId}/replies")
public class TicketReplyController {

    @Autowired
    private TicketReplyService ticketReplyService;

    /**
     * 回复工单
     * POST /api/tickets/{ticketId}/replies
     */
    @PostMapping
    public Result<TicketReply> createReply(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateTicketReplyDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TicketReply reply = ticketReplyService.createReply(ticketId, dto, userId);
            return Result.success("回复成功", reply);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除回复
     * DELETE /api/tickets/{ticketId}/replies/{replyId}
     */
    @DeleteMapping("/{replyId}")
    public Result<Void> deleteReply(
            @PathVariable Long ticketId,
            @PathVariable Long replyId,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("role") String userRole) {
        try {
            ticketReplyService.deleteReply(replyId, userId, userRole);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

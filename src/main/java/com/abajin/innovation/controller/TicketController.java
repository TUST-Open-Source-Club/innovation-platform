package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.CreateTicketDTO;
import com.abajin.innovation.dto.TicketQueryDTO;
import com.abajin.innovation.dto.UpdateTicketDTO;
import com.abajin.innovation.entity.Ticket;
import com.abajin.innovation.entity.TicketReply;
import com.abajin.innovation.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工单管理控制器
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * 创建工单
     * POST /api/tickets
     */
    @PostMapping
    public Result<Ticket> createTicket(
            @Valid @RequestBody CreateTicketDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            Ticket ticket = ticketService.createTicket(dto, userId);
            return Result.success("工单创建成功", ticket);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取工单列表（分页）
     * GET /api/tickets
     */
    @GetMapping
    public Result<PageResult<Ticket>> getTicketList(TicketQueryDTO queryDTO) {
        try {
            PageResult<Ticket> result = ticketService.getTicketPage(queryDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的工单
     * GET /api/tickets/my
     */
    @GetMapping("/my")
    public Result<PageResult<Ticket>> getMyTickets(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            PageResult<Ticket> result = ticketService.getMyTickets(userId, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取工单详情
     * GET /api/tickets/{id}
     */
    @GetMapping("/{id}")
    public Result<Ticket> getTicketById(@PathVariable Long id) {
        try {
            Ticket ticket = ticketService.getTicketDetail(id);
            return Result.success(ticket);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取工单回复列表
     * GET /api/tickets/{id}/replies
     */
    @GetMapping("/{id}/replies")
    public Result<List<TicketReply>> getTicketReplies(@PathVariable Long id) {
        try {
            List<TicketReply> replies = ticketService.getTicketReplies(id);
            return Result.success(replies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新工单
     * PUT /api/tickets/{id}
     */
    @PutMapping("/{id}")
    public Result<Ticket> updateTicket(
            @PathVariable Long id,
            @RequestBody UpdateTicketDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            Ticket ticket = ticketService.updateTicket(id, dto, userId);
            return Result.success("工单更新成功", ticket);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除工单
     * DELETE /api/tickets/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTicket(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("role") String userRole) {
        try {
            ticketService.deleteTicket(id, userId, userRole);
            return Result.success("工单删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新工单状态（管理员）
     * PUT /api/tickets/{id}/status
     */
    @PutMapping("/{id}/status")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Ticket> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        try {
            Long statusId = body.get("statusId");
            if (statusId == null) {
                return Result.error("状态ID不能为空");
            }
            Ticket ticket = ticketService.updateTicketStatus(id, statusId);
            return Result.success("状态更新成功", ticket);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

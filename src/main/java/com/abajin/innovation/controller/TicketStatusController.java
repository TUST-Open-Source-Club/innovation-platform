package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.TicketStatus;
import com.abajin.innovation.service.TicketStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工单状态管理控制器
 */
@RestController
@RequestMapping("/ticket-statuses")
public class TicketStatusController {

    @Autowired
    private TicketStatusService ticketStatusService;

    /**
     * 获取所有工单状态
     * GET /api/ticket-statuses
     */
    @GetMapping
    public Result<List<TicketStatus>> getAllStatuses() {
        return Result.success(ticketStatusService.getAllStatuses());
    }

    /**
     * 根据ID获取工单状态
     * GET /api/ticket-statuses/{id}
     */
    @GetMapping("/{id}")
    public Result<TicketStatus> getStatusById(@PathVariable Long id) {
        TicketStatus status = ticketStatusService.getStatusById(id);
        if (status == null) {
            return Result.error("工单状态不存在");
        }
        return Result.success(status);
    }

    /**
     * 创建工单状态
     * POST /api/ticket-statuses
     */
    @PostMapping
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<TicketStatus> createStatus(@RequestBody TicketStatus ticketStatus) {
        try {
            TicketStatus created = ticketStatusService.createStatus(ticketStatus);
            return Result.success("创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新工单状态
     * PUT /api/ticket-statuses/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<TicketStatus> updateStatus(@PathVariable Long id, @RequestBody TicketStatus ticketStatus) {
        try {
            TicketStatus updated = ticketStatusService.updateStatus(id, ticketStatus);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除工单状态
     * DELETE /api/ticket-statuses/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Void> deleteStatus(@PathVariable Long id) {
        try {
            ticketStatusService.deleteStatus(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

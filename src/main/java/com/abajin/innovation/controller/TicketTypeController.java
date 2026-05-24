package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.TicketType;
import com.abajin.innovation.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工单类型管理控制器
 */
@RestController
@RequestMapping("/ticket-types")
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeService;

    /**
     * 获取所有工单类型
     * GET /api/ticket-types
     */
    @GetMapping
    public Result<List<TicketType>> getAllTypes() {
        return Result.success(ticketTypeService.getAllTypes());
    }

    /**
     * 根据ID获取工单类型
     * GET /api/ticket-types/{id}
     */
    @GetMapping("/{id}")
    public Result<TicketType> getTypeById(@PathVariable Long id) {
        TicketType type = ticketTypeService.getTypeById(id);
        if (type == null) {
            return Result.error("工单类型不存在");
        }
        return Result.success(type);
    }

    /**
     * 创建工单类型
     * POST /api/ticket-types
     */
    @PostMapping
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<TicketType> createType(@RequestBody TicketType ticketType) {
        try {
            TicketType created = ticketTypeService.createType(ticketType);
            return Result.success("创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新工单类型
     * PUT /api/ticket-types/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<TicketType> updateType(@PathVariable Long id, @RequestBody TicketType ticketType) {
        try {
            TicketType updated = ticketTypeService.updateType(id, ticketType);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除工单类型
     * DELETE /api/ticket-types/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Void> deleteType(@PathVariable Long id) {
        try {
            ticketTypeService.deleteType(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

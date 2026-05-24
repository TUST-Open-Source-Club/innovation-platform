package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.service.TicketStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 工单状态管理控制器
 * 状态已改为枚举，仅保留查询接口
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
    public Result<List<Map<String, Object>>> getAllStatuses() {
        return Result.success(ticketStatusService.getAllStatuses());
    }
}

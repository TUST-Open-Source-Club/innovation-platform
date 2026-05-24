package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单统计报表控制器
 */
@RestController
@RequestMapping("/tickets/statistics")
public class TicketStatisticsController {

    @Autowired
    private TicketService ticketService;

    /**
     * 工单类型统计
     * GET /api/tickets/statistics/by-type
     */
    @GetMapping("/by-type")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<List<Map<String, Object>>> countByType() {
        try {
            List<Map<String, Object>> stats = ticketService.countByType();
            int total = ticketService.countTotal();
            for (Map<String, Object> stat : stats) {
                long count = ((Number) stat.get("count")).longValue();
                double percentage = total > 0 ? (double) count / total * 100 : 0;
                stat.put("percentage", Math.round(percentage * 100.0) / 100.0);
            }
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 工单状态分布统计
     * GET /api/tickets/statistics/by-status
     */
    @GetMapping("/by-status")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<List<Map<String, Object>>> countByStatus() {
        try {
            List<Map<String, Object>> stats = ticketService.countByStatus();
            int total = ticketService.countTotal();
            for (Map<String, Object> stat : stats) {
                long count = ((Number) stat.get("count")).longValue();
                double percentage = total > 0 ? (double) count / total * 100 : 0;
                stat.put("percentage", Math.round(percentage * 100.0) / 100.0);
            }
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 工单紧急程度统计
     * GET /api/tickets/statistics/by-urgency
     */
    @GetMapping("/by-urgency")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<List<Map<String, Object>>> countByUrgency() {
        try {
            List<Map<String, Object>> stats = ticketService.countByUrgency();
            int total = ticketService.countTotal();
            for (Map<String, Object> stat : stats) {
                long count = ((Number) stat.get("count")).longValue();
                double percentage = total > 0 ? (double) count / total * 100 : 0;
                stat.put("percentage", Math.round(percentage * 100.0) / 100.0);
            }
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 满意度统计
     * GET /api/tickets/statistics/satisfaction
     */
    @GetMapping("/satisfaction")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Map<String, Object>> countSatisfaction() {
        try {
            Map<String, Object> stats = ticketService.countSatisfaction();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 完成率统计
     * GET /api/tickets/statistics/completion-rate
     */
    @GetMapping("/completion-rate")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Map<String, Object>> countCompletionRate() {
        try {
            Map<String, Object> stats = ticketService.countCompletionRate();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 综合统计概览
     * GET /api/tickets/statistics/overview
     */
    @GetMapping("/overview")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Map<String, Object>> getOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            overview.put("total", ticketService.countTotal());
            overview.put("byType", ticketService.countByType());
            overview.put("byStatus", ticketService.countByStatus());
            overview.put("byUrgency", ticketService.countByUrgency());
            overview.put("satisfaction", ticketService.countSatisfaction());
            overview.put("completionRate", ticketService.countCompletionRate());
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

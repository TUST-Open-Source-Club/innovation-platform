package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.TicketEvaluationDTO;
import com.abajin.innovation.entity.TicketEvaluation;
import com.abajin.innovation.service.TicketEvaluationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工单评价控制器
 */
@RestController
@RequestMapping("/tickets/{ticketId}/evaluation")
public class TicketEvaluationController {

    @Autowired
    private TicketEvaluationService ticketEvaluationService;

    /**
     * 评价工单
     * POST /api/tickets/{ticketId}/evaluation
     */
    @PostMapping
    public Result<TicketEvaluation> createEvaluation(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketEvaluationDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TicketEvaluation evaluation = ticketEvaluationService.createEvaluation(ticketId, dto, userId);
            return Result.success("评价成功", evaluation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看工单评价
     * GET /api/tickets/{ticketId}/evaluation
     */
    @GetMapping
    public Result<TicketEvaluation> getEvaluation(@PathVariable Long ticketId) {
        try {
            TicketEvaluation evaluation = ticketEvaluationService.getEvaluation(ticketId);
            if (evaluation == null) {
                return Result.error("该工单尚未评价");
            }
            return Result.success(evaluation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新工单评价（24小时内可修改）
     * PUT /api/tickets/{ticketId}/evaluation
     */
    @PutMapping
    public Result<TicketEvaluation> updateEvaluation(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketEvaluationDTO dto,
            @RequestAttribute("userId") Long userId) {
        try {
            TicketEvaluation evaluation = ticketEvaluationService.updateEvaluation(ticketId, dto, userId);
            return Result.success("评价更新成功", evaluation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

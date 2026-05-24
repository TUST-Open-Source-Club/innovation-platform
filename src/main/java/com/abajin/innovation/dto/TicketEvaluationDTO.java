package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 工单评价DTO
 */
@Data
public class TicketEvaluationDTO {
    @NotBlank(message = "满意度不能为空")
    private String satisfaction; // SATISFIED/DISSATISFIED

    private String content; // 评价内容（可选）
}

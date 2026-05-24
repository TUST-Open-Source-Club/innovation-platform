package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建工单回复DTO
 */
@Data
public class CreateTicketReplyDTO {
    @NotBlank(message = "回复内容不能为空")
    private String content;

    private Long parentId; // 父回复ID（NULL表示顶层回复）
}

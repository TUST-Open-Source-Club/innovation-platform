package com.abajin.innovation.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新工单DTO
 */
@Data
public class UpdateTicketDTO {
    @Size(max = 200, message = "工单标题不能超过200个字符")
    private String title;

    private String content;

    private Long typeId;

    private String urgency; // LOW/MEDIUM/HIGH/URGENT

    private String images; // 图片URL列表（JSON数组格式）
}

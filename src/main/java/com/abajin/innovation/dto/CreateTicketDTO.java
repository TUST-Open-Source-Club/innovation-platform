package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建工单DTO
 */
@Data
public class CreateTicketDTO {
    @NotBlank(message = "工单标题不能为空")
    @Size(max = 200, message = "工单标题不能超过200个字符")
    private String title;

    @NotBlank(message = "工单内容不能为空")
    private String content;

    @NotNull(message = "工单类型不能为空")
    private Long typeId;

    @NotBlank(message = "紧急程度不能为空")
    private String urgency; // LOW/MEDIUM/HIGH/URGENT

    private String images; // 图片URL列表（JSON数组格式）
}

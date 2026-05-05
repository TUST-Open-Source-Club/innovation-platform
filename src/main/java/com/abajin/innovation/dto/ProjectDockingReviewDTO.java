package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目对接审批 DTO
 */
@Data
public class ProjectDockingReviewDTO {
    @NotBlank(message = "审批结果不能为空")
    private String approvalStatus;

    private String approvalComment;
}

package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目招募申请 DTO
 */
@Data
public class ProjectRecruitmentApplicationDTO {
    @NotBlank(message = "申请职位不能为空")
    private String desiredPosition;

    @NotBlank(message = "专业不能为空")
    private String applicantMajor;

    @NotBlank(message = "特长不能为空")
    private String qualifications;

    private String answerContent;
    private String resumeUrl;
    private String remark;
}

package com.abajin.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目招募 DTO
 */
@Data
public class ProjectRecruitmentDTO {
    @NotBlank(message = "招募职位不能为空")
    private String positionName;

    @NotBlank(message = "任务说明不能为空")
    private String taskDescription;

    private String collegePreference;
    private String majorPreference;
    private String questionContent;
}

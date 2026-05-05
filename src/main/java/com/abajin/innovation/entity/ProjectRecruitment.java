package com.abajin.innovation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目招募实体
 */
@Data
public class ProjectRecruitment {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private Long publisherId;
    private String publisherName;
    private String positionName;
    private String taskDescription;
    private String collegePreference;
    private String majorPreference;
    private String questionContent;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.abajin.innovation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队实体类
 */
@Data
public class Team {
    private Long id;
    private String name; // 团队名称
    private String teamType; // 团队类型：INNOVATION=创新团队, STARTUP=创业团队, RESEARCH=科研团队
    private String description; // 团队描述
    private Long leaderId; // 队长ID
    private String leaderName; // 队长姓名
    private String leaderStudentId; // 负责人学号
    private String collegeName; // 学院名
    private Integer memberCount; // 成员数量
    private Boolean recruiting; // 是否招募成员（队长可设置）
    private Boolean isPublic; // 团队是否公开（队长可设置）
    private String recruitmentRequirement; // 招募人员的要求（队长可设置）
    private String honors; // 团队历史荣誉（队长可设置）
    private String tags; // 项目标签，逗号分隔（队长可设置）
    private String instructorName; // 指导老师
    private Boolean isDeleted; // 是否删除：false-未删除，true-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

ALTER TABLE `project_application`
    ADD COLUMN `desired_position` VARCHAR(100) NULL AFTER `application_type`,
    ADD COLUMN `applicant_college_id` BIGINT NULL AFTER `desired_position`,
    ADD COLUMN `applicant_college_name` VARCHAR(100) NULL AFTER `applicant_college_id`,
    ADD COLUMN `applicant_major` VARCHAR(100) NULL AFTER `applicant_college_name`,
    ADD COLUMN `resume_url` VARCHAR(500) NULL AFTER `qualifications`;

CREATE INDEX `idx_project_application_type`
    ON `project_application` (`application_type`);

CREATE INDEX `idx_project_application_applicant_project`
    ON `project_application` (`project_id`, `applicant_id`);

CREATE TABLE IF NOT EXISTS `project_recruitment`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `project_id` BIGINT NOT NULL,
    `project_title` VARCHAR(200) NULL,
    `publisher_id` BIGINT NOT NULL,
    `publisher_name` VARCHAR(50) NULL,
    `position_name` VARCHAR(100) NOT NULL,
    `task_description` TEXT NOT NULL,
    `college_preference` VARCHAR(100) NULL,
    `major_preference` VARCHAR(100) NULL,
    `question_content` TEXT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_recruitment_project`
        FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_recruitment_publisher`
        FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX `idx_project_recruitment_project_id`
    ON `project_recruitment` (`project_id`);

CREATE INDEX `idx_project_recruitment_publisher_id`
    ON `project_recruitment` (`publisher_id`);

CREATE TABLE IF NOT EXISTS `project_recruitment_application`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `recruitment_id` BIGINT NOT NULL,
    `project_id` BIGINT NOT NULL,
    `applicant_id` BIGINT NOT NULL,
    `applicant_name` VARCHAR(50) NULL,
    `applicant_role` VARCHAR(20) NULL,
    `desired_position` VARCHAR(100) NOT NULL,
    `applicant_college_id` BIGINT NULL,
    `applicant_college_name` VARCHAR(100) NULL,
    `applicant_major` VARCHAR(100) NOT NULL,
    `qualifications` TEXT NULL,
    `answer_content` TEXT NULL,
    `resume_url` VARCHAR(500) NULL,
    `remark` TEXT NULL,
    `approval_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `approver_id` BIGINT NULL,
    `approver_name` VARCHAR(50) NULL,
    `approval_comment` TEXT NULL,
    `approval_time` DATETIME NULL,
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_recruitment_application_recruitment`
        FOREIGN KEY (`recruitment_id`) REFERENCES `project_recruitment` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_recruitment_application_project`
        FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_recruitment_application_applicant`
        FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_project_recruitment_application_approver`
        FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX `idx_project_recruitment_application_project_id`
    ON `project_recruitment_application` (`project_id`);

CREATE INDEX `idx_project_recruitment_application_recruitment_id`
    ON `project_recruitment_application` (`recruitment_id`);

CREATE INDEX `idx_project_recruitment_application_applicant_id`
    ON `project_recruitment_application` (`applicant_id`);

CREATE INDEX `idx_project_recruitment_application_status`
    ON `project_recruitment_application` (`approval_status`);

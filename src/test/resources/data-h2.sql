-- H2 Test Database Data

-- 清空已有数据（避免多Spring上下文共享H2内存数据库时主键冲突）
TRUNCATE TABLE college;
TRUNCATE TABLE user;

-- 插入测试学院数据
INSERT INTO college (id, name, code, description) VALUES 
(1, '计算机学院', 'CS', '计算机科学与技术学院'),
(2, '软件学院', 'SE', '软件工程学院'),
(3, '电子信息学院', 'EE', '电子信息工程学院'),
(4, '经济管理学院', 'EM', '经济管理学院');

-- 插入测试用户数据（本地认证用户）
INSERT INTO user (id, username, password, real_name, email, phone, role, college_id, college_name, status, auth_type, is_profile_complete) VALUES 
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'admin@test.com', '13800138000', 'SCHOOL_ADMIN', 1, '计算机学院', 1, 'LOCAL', 1),
(2, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '张老师', 'teacher1@test.com', '13800138001', 'TEACHER', 1, '计算机学院', 1, 'LOCAL', 1),
(3, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '李同学', 'student1@test.com', '13800138002', 'STUDENT', 1, '计算机学院', 1, 'LOCAL', 1);

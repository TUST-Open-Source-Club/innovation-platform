-- MySQL dump 10.13  Distrib 8.0.45, for Linux (aarch64)
--
-- Host: localhost    Database: innovation_platform
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动标题',
                            `activity_type_id` bigint DEFAULT NULL COMMENT '活动类型ID',
                            `activity_series` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动系列：先锋双创榜样/双创技术讲坛/企业家大讲堂',
                            `activity_type_other` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其他活动类型（当活动类型选其他时填写）',
                            `organizer_id` bigint NOT NULL COMMENT '组织者ID（用户ID）',
                            `organizer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织者姓名',
                            `organizer_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织者类型：USER-个人, TEAM-团队, ORGANIZATION-组织',
                            `organizer_entity_id` bigint DEFAULT NULL COMMENT '组织者实体ID（团队ID或组织ID）',
                            `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                            `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                            `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动地点',
                            `space_id` bigint DEFAULT NULL COMMENT '关联预约空间ID',
                            `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动描述',
                            `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动内容',
                            `registration_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名链接',
                            `qr_code_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名二维码图片URL',
                            `host_unit_id` bigint DEFAULT NULL COMMENT '主办单位ID（组织）',
                            `co_organizer_ids` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '承办单位ID列表，逗号分隔',
                            `other_units` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其他单位（文本）',
                            `poster_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '海报图片URL（仅管理员可上传）',
                            `max_participants` int DEFAULT NULL COMMENT '最大参与人数',
                            `registration_deadline` datetime DEFAULT NULL COMMENT '报名截止时间',
                            `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝, PUBLISHED-已发布, ONGOING-进行中, COMPLETED-已完成, CANCELLED-已取消',
                            `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                            `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                            `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                            `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `activity_type_id` (`activity_type_id`) USING BTREE,
                            KEY `organizer_id` (`organizer_id`) USING BTREE,
                            KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                            CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`activity_type_id`) REFERENCES `activity_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                            CONSTRAINT `activity_ibfk_2` FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                            CONSTRAINT `activity_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动申报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (1,'创新创业大赛宣讲会',1,NULL,NULL,5,'陈教授','USER',NULL,'2026-02-10 14:10:16','2026-02-10 16:10:16','学术报告厅',NULL,'介绍大赛规则与流程','详细宣讲内容...',NULL,NULL,NULL,NULL,NULL,NULL,200,'2026-02-09 14:10:16','PUBLISHED','APPROVED',NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,'人工智能技术分享沙龙',4,NULL,NULL,2,'张三','USER',NULL,'2026-02-17 14:10:16','2026-02-17 17:10:16','创客空间',NULL,'AI技术交流与分享','技术分享内容...',NULL,NULL,NULL,NULL,NULL,NULL,50,'2026-02-16 14:10:16','PUBLISHED','APPROVED',NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(3,'项目路演培训',3,NULL,NULL,7,'学院管理员','USER',NULL,'2026-02-13 14:10:16','2026-02-13 18:10:16','路演厅',NULL,'路演技巧与PPT制作','培训内容...',NULL,NULL,NULL,NULL,NULL,NULL,80,'2026-02-12 14:10:16','DRAFT','PENDING',NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(4,'1',3,NULL,NULL,4,'王五','USER',NULL,'2026-02-11 00:00:00','2026-02-19 00:00:00','1',NULL,'1','1',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'REJECTED','REJECTED',1,'1','2026-02-04 09:27:58','2026-02-04 09:27:51','2026-02-04 01:28:03'),(5,'1',2,'',NULL,4,'王五','USER',NULL,'2026-02-04 09:00:00','2026-02-04 10:00:00','1',NULL,'1','1',NULL,'http://192.168.147.110:9000/first/second/activity-qrcode/c8b1d3e99ae14f5db644db2d00de8812.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T110636Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=8c07ba95c3a874946dbfb0f594223606229bc664271cbd4d9215fe9ad6800d37',NULL,NULL,NULL,'http://192.168.147.110:9000/first/second/activity-poster/025cfd3656fa43c0ae7c761ade5d793b.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T110612Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=84a87bca85a4b180a9c32675f0153af12084bbb1647d3276a21f70d5fcb6903b',4,NULL,'APPROVED','APPROVED',1,'1','2026-02-04 09:28:20','2026-02-04 09:28:14','2026-03-10 11:06:38'),(6,'1',1,'先锋双创榜样',NULL,2,'张三','USER',NULL,'2026-03-10 09:00:00','2026-03-10 10:00:00','四楼A区会议室',6,'1','1',NULL,'http://192.168.147.110:9000/first/second/activity-qrcode/b0d561efac904f598ec212f770d316fc.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T140045Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=fc4758d21964b7327d73c00f7757f238a70f004d07789a6c5a0f6a3068994b5b',5,'4,5',NULL,'http://192.168.147.110:9000/first/second/activity-poster/3d343ffe8bc04728babc47276273e694.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T140042Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e9a3f7f146901729f1da21ee55bc89c96b0da2712a707dbfc9060ef0e05d7ffb',NULL,NULL,'APPROVED','APPROVED',7,'','2026-03-09 10:44:41','2026-03-09 10:43:25','2026-03-10 14:00:48'),(7,'1',2,'先锋双创榜样',NULL,2,'张三','USER',NULL,'2026-03-10 09:00:00','2026-03-10 10:00:00','共创空间静音仓1',7,'1','1',NULL,NULL,4,'9,15',NULL,'http://192.168.147.110:9000/first/second/activity-poster/3799a20f1f9e41669cddfcf7780bdd7e.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T140106Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e8bfce522c2c50d17b71b6f87c9d3e8c89dd5606e06343bd3b68bc3a48c61015',NULL,NULL,'APPROVED','APPROVED',7,'','2026-03-09 14:01:03','2026-03-09 11:07:43','2026-03-10 14:01:08'),(8,'1',1,'先锋双创榜样',NULL,2,'张三','USER',NULL,'2026-03-10 19:00:00','2026-03-10 20:00:00','共创空间静音仓2',8,'1','1',NULL,NULL,5,'14,3',NULL,'http://192.168.147.110:9000/first/second/activity-poster/fdfd958de4934fb5b2f3e9889f42d7fc.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T140123Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=01acb08b98835a307adde4d5c054d898147c035511c8834fe704521ba215d940',NULL,NULL,'APPROVED','APPROVED',1,'','2026-03-09 14:19:22','2026-03-09 13:57:00','2026-03-10 14:01:25'),(9,'1',1,'先锋双创榜样',NULL,2,'张三','USER',NULL,'2026-03-12 19:00:00','2026-03-12 20:00:00','共创空间静音仓1',7,'1','1',NULL,NULL,6,'4,5',NULL,'http://192.168.147.110:9000/first/second/activity-poster/d3d84c7e959743fbb7eb49aea5662b57.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T124207Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=a268d1f537d87a05cd794448723f3679bb615c07fb8e0fc0b02cdd0fdc0ed123',NULL,NULL,'PUBLISHED','APPROVED',1,'','2026-03-09 14:52:36','2026-03-09 14:45:44','2026-03-10 12:42:07'),(10,'1',1,'双创技术讲坛',NULL,2,'张三','USER',NULL,'2026-03-11 17:00:00','2026-03-11 18:00:00','共创空间静音仓2',8,'1','1',NULL,NULL,5,'5,15',NULL,'http://192.168.147.110:9000/first/second/activity-poster/05dd94b01a154b949bcfe6a6a9146419.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260310%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260310T140139Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=0f035bee34ac8cee2452b1d5ea48d6f12cf7d85dbfb824c0a2bd7f96b08824a0',NULL,NULL,'PUBLISHED','APPROVED',1,'','2026-03-09 15:11:55','2026-03-09 15:02:35','2026-03-10 14:01:40'),(11,'`1',1,'先锋双创榜样',NULL,4,'王五','USER',NULL,'2026-03-26 18:00:00','2026-03-26 19:00:00','共创空间静音仓2',8,'1','1',NULL,NULL,5,'10,16',NULL,NULL,NULL,NULL,'REJECTED','REJECTED',1,'1','2026-03-09 15:13:06','2026-03-09 15:12:40','2026-03-09 07:13:07'),(12,'1',1,'先锋双创榜样',NULL,2,'张三','USER',NULL,'2026-03-19 09:00:00','2026-03-19 10:00:00','共创空间静音仓2',3,'1','1',NULL,NULL,5,'5,9',NULL,NULL,NULL,NULL,'REJECTED','REJECTED',7,'1','2026-03-12 19:39:13','2026-03-12 19:38:45','2026-03-12 11:39:16'),(13,'5',1,'先锋双创榜样',NULL,2,'张三','USER',NULL,'2026-03-13 12:00:00','2026-03-13 13:00:00','共创空间静音仓1',2,'5','5',NULL,'http://192.168.147.110:9000/first/second/activity-qrcode/323f98f8cc43425b93abf78ca9d784e1.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260312%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260312T124143Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f75ac59497ad98066f7965525b8bf9c0dc64ce7e97d0f8dc387797dd39a1ad2d',4,'5,9',NULL,'http://192.168.147.110:9000/first/second/activity-poster/5244da717b414f81b9eb0263d65b1cce.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260312%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260312T124242Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f2f5c1af5284785f794da861b5e3b8bd8aa35834091b17743291bd0d256ac723',NULL,NULL,'PUBLISHED','APPROVED',1,'','2026-03-12 20:42:20','2026-03-12 20:41:48','2026-03-12 12:42:52'),(14,'67',1,'双创技术讲坛',NULL,2,'张三','USER',NULL,'2026-03-13 10:00:00','2026-03-13 11:00:00','全景智慧多功能厅',4,'6','6',NULL,'http://192.168.147.110:9000/first/second/activity-qrcode/9722433c1add429a8b10d6d4f5468855.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260312%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260312T131208Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=1665aec025a18f008b361c1c439476e714771cc9a84ed172f5dae665eefa1473',4,'5,9',NULL,'http://192.168.147.110:9000/first/second/activity-poster/efcf2dca4cdc4c43bdb3edc6d71fa380.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260312%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260312T131314Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f9869eadeed7a9bc5fe03d3405829983717eb58d945f34aa6dadd5a2ab2b3d73',NULL,NULL,'PUBLISHED','APPROVED',1,'','2026-03-12 21:12:36','2026-03-12 21:12:12','2026-03-12 13:13:20');
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_registration`
--

DROP TABLE IF EXISTS `activity_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_registration` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `activity_id` bigint NOT NULL COMMENT '活动ID',
                                         `user_id` bigint NOT NULL COMMENT '报名用户ID',
                                         `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名用户姓名',
                                         `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                         `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                                         `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注',
                                         `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消',
                                         `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                         `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                         `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE KEY `uk_activity_user` (`activity_id`,`user_id`) USING BTREE,
                                         KEY `user_id` (`user_id`) USING BTREE,
                                         KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                         CONSTRAINT `activity_registration_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                         CONSTRAINT `activity_registration_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                         CONSTRAINT `activity_registration_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_registration`
--

LOCK TABLES `activity_registration` WRITE;
/*!40000 ALTER TABLE `activity_registration` DISABLE KEYS */;
INSERT INTO `activity_registration` VALUES (1,1,2,'张三','13800001111','zhangsan@stu.edu.cn','期待参加','APPROVED','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,1,3,'李四','13800002222','lisi@stu.edu.cn',NULL,'APPROVED','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(3,1,5,'王五','13800003333','wangwu@stu.edu.cn',NULL,'PENDING','PENDING',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(4,2,2,'张三','13800001111','zhangsan@stu.edu.cn',NULL,'APPROVED','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16');
/*!40000 ALTER TABLE `activity_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_summary`
--

DROP TABLE IF EXISTS `activity_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_summary` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `activity_id` bigint NOT NULL COMMENT '活动ID',
                                    `actual_participants` int DEFAULT NULL COMMENT '实际参与人数',
                                    `summary_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动总结内容',
                                    `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动成果',
                                    `photos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动照片（JSON数组，存储文件路径）',
                                    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
                                    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过',
                                    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                                    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE KEY `uk_activity_summary` (`activity_id`) USING BTREE,
                                    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                    CONSTRAINT `activity_summary_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                    CONSTRAINT `activity_summary_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动总结表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_summary`
--

LOCK TABLES `activity_summary` WRITE;
/*!40000 ALTER TABLE `activity_summary` DISABLE KEYS */;
INSERT INTO `activity_summary` VALUES (1,10,1,'1','1','[\"http://192.168.147.110:9000/first/second/activity-summary-photo/9d578997c8634e95bb35b7aab1f00bfc.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260311T050737Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=5c2f60921fca89c7f95c0d52d997667f835a22f139b5d76768fd72fe86703112\"]','[{\"name\":\"2024-2025学年第二学期选课通知.doc\",\"url\":\"http://192.168.147.110:9000/first/second/activity-summary-attachment/30153bae0a4f47919733390bf463ddc5.doc?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260311T050737Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=09ef58c413462a0f5442b8634da26e4a381e66e416f4fb6fc9ed89b151c2bc04\"}]','SUBMITTED','PENDING',NULL,NULL,NULL,'2026-03-11 12:22:30','2026-03-11 05:07:38'),(2,9,1,'1','1','[\"http://192.168.147.110:9000/first/second/activity-summary-photo/6bd10cf5cb2d4b31b4ad4b051d5053d6.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260311T050825Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=58754e151d870f78d5053ea480ed0f51228ba97d110ef7a407ed9aa091f7913d\"]','[{\"name\":\"工作通知【2024】54号：关于开展我校2024年“新时代·实践行”系列实践活动评审考核的通知.doc\",\"url\":\"http://192.168.147.110:9000/first/second/activity-summary-attachment/cda155d80a824493810cd8601a103895.doc?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260311T050825Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=7d0bf4dd1ed01467f565c08dba94b7da91c384fd7579769d8b964669f357eb44\"}]','SUBMITTED','PENDING',NULL,NULL,NULL,'2026-03-11 12:58:43','2026-03-11 05:08:26');
/*!40000 ALTER TABLE `activity_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_type`
--

DROP TABLE IF EXISTS `activity_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_type` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动类型名称',
                                 `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动类型代码',
                                 `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动类型描述',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_type`
--

LOCK TABLES `activity_type` WRITE;
/*!40000 ALTER TABLE `activity_type` DISABLE KEYS */;
INSERT INTO `activity_type` VALUES (1,'创新创业大赛','INNOVATION_CONTEST','各类创新创业竞赛','2026-02-03 14:10:02','2026-02-03 14:10:02'),(2,'讲座培训','LECTURE_TRAINING','讲座、培训活动','2026-02-03 14:10:02','2026-02-03 14:10:02'),(3,'项目路演','ROADSHOW','项目展示路演','2026-02-03 14:10:02','2026-02-03 14:10:02'),(4,'交流沙龙','SALON','交流讨论活动','2026-02-03 14:10:02','2026-02-03 14:10:02'),(5,'成果展示','EXHIBITION','成果展示活动','2026-02-03 14:10:02','2026-02-03 14:10:02');
/*!40000 ALTER TABLE `activity_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `college`
--

DROP TABLE IF EXISTS `college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `college` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学院名称',
                           `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院代码',
                           `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '学院描述',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `college`
--

LOCK TABLES `college` WRITE;
/*!40000 ALTER TABLE `college` DISABLE KEYS */;
INSERT INTO `college` VALUES (1,'计算机学院','CS','计算机科学与技术学院','2026-02-03 14:09:50','2026-02-03 14:09:50'),(2,'机械工程学院','ME','机械工程学院','2026-02-03 14:09:50','2026-03-09 02:20:05'),(3,'管理学院','MG','管理学院','2026-02-03 14:09:50','2026-02-03 14:09:50'),(4,'电子信息与自动化学院','EEA','电子信息与自动化学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(5,'食品科学与工程学院','FSE','食品科学与工程学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(6,'化工与材料学院','CME','化工与材料学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(7,'生物工程学院','BIO','生物工程学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(8,'海洋与环境学院','OCE','海洋与环境学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(9,'轻工科学与工程学院','LSE','轻工科学与工程学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(10,'艺术设计学院','ART','艺术设计学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(11,'经济与管理学院','MGT','经济与管理学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(12,'马克思主义学院','MARX','马克思主义学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(13,'人工智能学院','AI','人工智能学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(14,'文法学院','LAW','文法学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(15,'理学院','SCI','理学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(16,'外国语学院','FL','外国语学院','2026-03-09 02:20:05','2026-03-09 02:20:05'),(17,'终身教育学院','LIFELONG','终身教育学院','2026-03-09 02:20:05','2026-03-09 02:20:05');
/*!40000 ALTER TABLE `college` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entry_application`
--

DROP TABLE IF EXISTS `entry_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entry_application` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `applicant_id` bigint NOT NULL COMMENT '申请人ID（用户ID）',
                                     `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                     `applicant_student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人学号',
                                     `applicant_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人联系方式',
                                     `team_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创新团队名称',
                                     `team_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队类型：INNOVATION-创新团队, STARTUP-创业团队, RESEARCH-科研团队',
                                     `team_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队简介',
                                     `innovation_direction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创新方向',
                                     `team_positioning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '创新团队定位与建设思路（详细描述）',
                                     `team_size` int DEFAULT NULL COMMENT '团队规模（总人数）',
                                     `recruitment_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '招募人员的要求（必填）',
                                     `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师姓名',
                                     `instructor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师联系方式',
                                     `campus_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师姓名',
                                     `campus_mentor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师联系方式',
                                     `enterprise_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师姓名',
                                     `enterprise_mentor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师联系方式',
                                     `partner_company` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合作企业',
                                     `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目名称',
                                     `project_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目简介',
                                     `project_achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目成绩（AB类赛事参赛所获最高荣誉奖项等）',
                                     `expected_outcomes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '预期成果',
                                     `is_competition_registered` tinyint DEFAULT '0' COMMENT '是否已报名参加竞赛：0-否，1-是',
                                     `competition_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '竞赛名称（拟报或已报竞赛）',
                                     `team_members` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目组成员（JSON数组，包含学号、姓名、专业、主要工作）',
                                     `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                     `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系邮箱',
                                     `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
                                     `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PENDING-待审核, APPROVED-已通过, REJECTED-已驳回, ENTERED-已入驻, EXITED-已退出',
                                     `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                     `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                     `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                                     `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                     `entry_time` datetime DEFAULT NULL COMMENT '入驻时间',
                                     `exit_time` datetime DEFAULT NULL COMMENT '退出时间',
                                     `exit_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '退出原因',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `applicant_id` (`applicant_id`) USING BTREE,
                                     KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                     CONSTRAINT `entry_application_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                     CONSTRAINT `entry_application_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='入驻申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entry_application`
--

LOCK TABLES `entry_application` WRITE;
/*!40000 ALTER TABLE `entry_application` DISABLE KEYS */;
INSERT INTO `entry_application` VALUES (1,2,'张三','2021001001','13800001111','创新先锋队','INNOVATION','AI与大数据创新团队','人工智能应用',NULL,3,NULL,'陈教授','13800004444',NULL,NULL,NULL,NULL,NULL,'智能校园管理系统','校园综合管理平台',NULL,NULL,0,NULL,NULL,'13800001111','zhangsan@stu.edu.cn',NULL,'PENDING','PENDING',NULL,NULL,NULL,NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,3,'李四','2021001002','13800002222','创业孵化小队','STARTUP','互联网创业团队','校园服务',NULL,2,NULL,'刘教授','13800005555',NULL,NULL,NULL,NULL,NULL,'校园二手交易平台','二手物品交易',NULL,NULL,0,NULL,NULL,'13800002222','lisi@stu.edu.cn',NULL,'ENTERED','APPROVED',NULL,NULL,NULL,'2026-01-24 14:10:16',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(3,4,'王五','12345644','13800003333','1','STARTUP','1','1','1',4,NULL,'1','13211111333','2','12322234556','3','13455662666','','1','1','1','1',0,'','[]','13800003333','asd151325@qq.com','[]','APPROVED','APPROVED',1,'','2026-03-09 16:40:44',NULL,NULL,NULL,'2026-03-09 16:21:00','2026-03-09 16:40:44'),(4,4,'王五','1','13800003333','1','INNOVATION','1','1','1',5,NULL,'1','1','11','1','1','1','','1','1','1','1',0,'','[]','13800003333','1','[]','ENTERED','PENDING',7,'','2026-03-09 16:49:05','2026-03-09 16:49:11',NULL,NULL,'2026-03-09 16:48:48','2026-03-09 16:49:11'),(5,4,'王五','1','13800003333','1','INNOVATION','1','1','1',4,NULL,'1','1','1','1','1','1','','1','1','1','1',0,'','[]','13800003333','1','[]','ENTERED','APPROVED',1,'','2026-03-09 18:28:09','2026-03-09 18:28:09',NULL,NULL,'2026-03-09 18:27:44','2026-03-09 18:28:09'),(6,4,'王五','1','13800003333','1','INNOVATION','1','1','1',5,NULL,'1','1','1','1','1','','1','1','1','1','1',0,'','[]','13800003333','1','[]','ENTERED','APPROVED',1,'','2026-03-09 18:34:04','2026-03-09 18:34:04',NULL,NULL,'2026-03-09 18:33:43','2026-03-09 18:34:04'),(7,4,'王五','1','13800003333','1','INNOVATION','1','1','1',1,'123456','1','1','1','1','1','','1','1','1','1','1',0,'','[{\"studentId\":\"1\",\"name\":\"1\",\"major\":\"1\",\"mainWork\":\"1\"}]','13800003333','1','[]','ENTERED','APPROVED',1,'','2026-03-11 12:40:09','2026-03-11 12:40:09',NULL,NULL,'2026-03-11 12:39:50','2026-03-11 12:40:09'),(8,2,'张三','5','13800001111','5','INNOVATION','5','5','5',3,'5555','5','5','5','5','1','','','5','5','5','5',0,'','[{\"studentId\":\"5\",\"name\":\"5\",\"major\":\"5\",\"mainWork\":\"5\"}]','13800003333','1','[]','ENTERED','APPROVED',1,'','2026-03-12 20:38:53','2026-03-12 20:38:53',NULL,NULL,'2026-03-12 20:38:20','2026-03-12 20:38:53'),(9,2,'张三','56','13800001111','56','INNOVATION','56','65','1',5,'9000','66','66','66','66','1','','','66','6','6','6',0,'','[{\"studentId\":\"1\",\"name\":\"1\",\"major\":\"1\",\"mainWork\":\"1\"}]','13800003333','1','[]','ENTERED','APPROVED',1,'','2026-03-12 21:08:53','2026-03-12 21:08:53',NULL,NULL,'2026-03-12 21:08:29','2026-03-12 21:08:53');
/*!40000 ALTER TABLE `entry_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entry_space_allocation`
--

DROP TABLE IF EXISTS `entry_space_allocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entry_space_allocation` (
                                          `id` bigint NOT NULL AUTO_INCREMENT,
                                          `entry_application_id` bigint NOT NULL COMMENT '入驻申请ID',
                                          `space_id` bigint NOT NULL COMMENT '分配的空间ID',
                                          `start_date` date DEFAULT NULL COMMENT '开始日期',
                                          `end_date` date DEFAULT NULL COMMENT '结束日期',
                                          `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-使用中, EXPIRED-已过期, RELEASED-已释放',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `entry_application_id` (`entry_application_id`) USING BTREE,
                                          KEY `space_id` (`space_id`) USING BTREE,
                                          CONSTRAINT `entry_space_allocation_ibfk_1` FOREIGN KEY (`entry_application_id`) REFERENCES `entry_application` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                          CONSTRAINT `entry_space_allocation_ibfk_2` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='入驻空间分配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entry_space_allocation`
--

LOCK TABLES `entry_space_allocation` WRITE;
/*!40000 ALTER TABLE `entry_space_allocation` DISABLE KEYS */;
/*!40000 ALTER TABLE `entry_space_allocation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fund_application`
--

DROP TABLE IF EXISTS `fund_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fund_application` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请标题',
                                    `fund_type_id` bigint DEFAULT NULL COMMENT '基金类型ID',
                                    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
                                    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                    `applicant_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人类型：USER-个人, TEAM-团队, PROJECT-项目',
                                    `applicant_entity_id` bigint DEFAULT NULL COMMENT '申请人实体ID',
                                    `project_id` bigint DEFAULT NULL COMMENT '关联项目ID',
                                    `team_id` bigint DEFAULT NULL COMMENT '关联团队ID',
                                    `application_amount` decimal(15,2) DEFAULT NULL COMMENT '申请金额',
                                    `application_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '申请理由',
                                    `application_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '申请内容',
                                    `expected_outcomes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '预期成果',
                                    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
                                    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝, FUNDED-已资助',
                                    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                                    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                    `approved_amount` decimal(15,2) DEFAULT NULL COMMENT '批准金额',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `fund_type_id` (`fund_type_id`) USING BTREE,
                                    KEY `applicant_id` (`applicant_id`) USING BTREE,
                                    KEY `project_id` (`project_id`) USING BTREE,
                                    KEY `team_id` (`team_id`) USING BTREE,
                                    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                    CONSTRAINT `fund_application_ibfk_1` FOREIGN KEY (`fund_type_id`) REFERENCES `fund_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `fund_application_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `fund_application_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `fund_application_ibfk_4` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `fund_application_ibfk_5` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='基金申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fund_application`
--

LOCK TABLES `fund_application` WRITE;
/*!40000 ALTER TABLE `fund_application` DISABLE KEYS */;
INSERT INTO `fund_application` VALUES (1,'智能校园系统研发基金',1,2,'张三','PROJECT',1,1,NULL,50000.00,'项目开发需要服务器及开发资源','申请用于云服务器、API服务等','完成系统上线并推广',NULL,'SUBMITTED','PENDING',NULL,NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,'工业机器人项目资助',2,5,'陈教授','PROJECT',2,2,NULL,30000.00,'实验设备采购','申请用于视觉识别设备','完成原型验证',NULL,'APPROVED','APPROVED',NULL,NULL,NULL,28000.00,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(3,'好东西',2,2,'张三','PROJECT',1,1,NULL,20000.00,'开发','开发','卡覅',NULL,'REJECTED','REJECTED',1,'不想给','2026-02-03 22:50:00',NULL,'2026-02-03 22:49:44','2026-02-03 14:50:05'),(4,'1',2,2,'张三','PROJECT',1,1,NULL,1.00,'1','1','1',NULL,'REJECTED','REJECTED',1,'1','2026-02-04 09:29:32',NULL,'2026-02-04 09:29:23','2026-02-04 01:29:37'),(5,'1',1,2,'张三','PROJECT',1,1,NULL,20000.00,'1','1','1',NULL,'FUNDED','APPROVED',7,'','2026-03-09 14:48:06',20000.00,'2026-03-09 14:47:48','2026-03-09 06:48:07'),(6,'2',3,2,'张三','PROJECT',1,1,NULL,30000.00,'1','1','1',NULL,'FUNDED','APPROVED',1,'','2026-03-09 16:01:20',30000.00,'2026-03-09 16:00:32','2026-03-09 08:01:21'),(7,'67',1,2,'张三','PROJECT',13,13,NULL,3000.00,'1','1','1',NULL,'FUNDED','APPROVED',1,'','2026-03-12 21:14:34',3000.00,'2026-03-12 21:14:17','2026-03-12 13:14:38');
/*!40000 ALTER TABLE `fund_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fund_type`
--

DROP TABLE IF EXISTS `fund_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fund_type` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '基金类型名称',
                             `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '基金类型代码',
                             `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '基金类型描述',
                             `max_amount` decimal(15,2) DEFAULT NULL COMMENT '最大申请金额',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='基金类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fund_type`
--

LOCK TABLES `fund_type` WRITE;
/*!40000 ALTER TABLE `fund_type` DISABLE KEYS */;
INSERT INTO `fund_type` VALUES (1,'创新创业基金','INNOVATION_FUND','支持创新创业项目',100000.00,'2026-02-03 14:10:02','2026-02-03 14:10:02'),(2,'科研基金','RESEARCH_FUND','支持科研项目',50000.00,'2026-02-03 14:10:02','2026-02-03 14:10:02'),(3,'创业启动基金','STARTUP_FUND','支持创业项目启动',200000.00,'2026-02-03 14:10:02','2026-02-03 14:10:02');
/*!40000 ALTER TABLE `fund_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `information_link`
--

DROP TABLE IF EXISTS `information_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `information_link` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源类型：PROJECT-项目, TEAM-团队, FUND-基金申请',
                                    `source_id` bigint NOT NULL COMMENT '源ID',
                                    `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型：PROJECT-项目, TEAM-团队, FUND-基金申请',
                                    `target_id` bigint NOT NULL COMMENT '目标ID',
                                    `link_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联类型（如：合作、引用、关联等）',
                                    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '关联说明',
                                    `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `creator_id` (`creator_id`) USING BTREE,
                                    KEY `idx_source` (`source_type`,`source_id`) USING BTREE,
                                    KEY `idx_target` (`target_type`,`target_id`) USING BTREE,
                                    CONSTRAINT `information_link_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='信息对接记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `information_link`
--

LOCK TABLES `information_link` WRITE;
/*!40000 ALTER TABLE `information_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `information_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `innovation_team_application`
--

DROP TABLE IF EXISTS `innovation_team_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `innovation_team_application` (
                                               `id` bigint NOT NULL AUTO_INCREMENT,
                                               `team_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创新团队名称',
                                               `cooperative_enterprise` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合作企业',
                                               `applicant_student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人学号',
                                               `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发起人姓名',
                                               `applicant_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人联系方式',
                                               `on_campus_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师姓名',
                                               `on_campus_mentor_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师联系方式',
                                               `enterprise_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师姓名',
                                               `enterprise_mentor_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师联系方式',
                                               `innovation_direction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创新方向',
                                               `positioning_and_ideas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '创新团队定位与建设思路',
                                               `applicant_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人签字',
                                               `applicant_sign_date` date DEFAULT NULL COMMENT '发起人签字日期',
                                               `on_campus_mentor_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师签字（学院盖章）',
                                               `on_campus_mentor_sign_date` date DEFAULT NULL COMMENT '校内导师签字日期',
                                               `enterprise_mentor_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师签字（企业盖章）',
                                               `enterprise_mentor_sign_date` date DEFAULT NULL COMMENT '企业导师签字日期',
                                               `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝',
                                               `applicant_id` bigint DEFAULT NULL COMMENT '申请人ID',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `applicant_id` (`applicant_id`) USING BTREE,
                                               CONSTRAINT `innovation_team_application_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='校企联合创新团队申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `innovation_team_application`
--

LOCK TABLES `innovation_team_application` WRITE;
/*!40000 ALTER TABLE `innovation_team_application` DISABLE KEYS */;
INSERT INTO `innovation_team_application` VALUES (1,'AI应用创新团队','某科技公司','2021001001','张三','13800001111','陈教授',NULL,NULL,NULL,'人工智能产业化应用',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'SUBMITTED',2,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,'智能制造联合团队','某制造企业','2021002001','王五','13800003333','刘教授',NULL,NULL,NULL,'工业互联网与智能制造',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'DRAFT',5,'2026-02-03 14:10:16','2026-02-03 14:10:16');
/*!40000 ALTER TABLE `innovation_team_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '新闻标题',
                        `category_id` bigint DEFAULT NULL COMMENT '分类ID',
                        `author_id` bigint DEFAULT NULL COMMENT '作者ID',
                        `author_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '作者姓名',
                        `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图片URL',
                        `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '摘要',
                        `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '新闻内容',
                        `source` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源',
                        `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
                        `related_activity_id` bigint DEFAULT NULL COMMENT '关联活动ID（可选）',
                        `view_count` int DEFAULT '0' COMMENT '浏览次数',
                        `like_count` int DEFAULT '0' COMMENT '点赞数',
                        `is_top` tinyint DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
                        `is_published` tinyint DEFAULT '0' COMMENT '是否发布：0-否，1-是',
                        `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
                        `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PENDING-待审核, PUBLISHED-已发布, REJECTED-已驳回',
                        `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                        `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                        `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                        `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `is_deleted` tinyint(1) DEFAULT '0',
                        PRIMARY KEY (`id`) USING BTREE,
                        KEY `category_id` (`category_id`) USING BTREE,
                        KEY `author_id` (`author_id`) USING BTREE,
                        KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                        KEY `related_activity_id` (`related_activity_id`) USING BTREE,
                        CONSTRAINT `news_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `news_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                        CONSTRAINT `news_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                        CONSTRAINT `news_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                        CONSTRAINT `news_ibfk_4` FOREIGN KEY (`related_activity_id`) REFERENCES `activity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='新闻表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_category`
--

DROP TABLE IF EXISTS `news_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_category` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
                                 `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类代码',
                                 `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '分类描述',
                                 `sort_order` int DEFAULT '0' COMMENT '排序顺序',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='新闻分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_category`
--

LOCK TABLES `news_category` WRITE;
/*!40000 ALTER TABLE `news_category` DISABLE KEYS */;
INSERT INTO `news_category` VALUES (1,'平台公告','ANNOUNCEMENT','平台公告通知',1,'2026-02-03 14:10:02','2026-02-03 14:10:02'),(2,'活动动态','ACTIVITY','活动相关动态',2,'2026-02-03 14:10:02','2026-02-03 14:10:02'),(3,'项目展示','PROJECT','项目展示',3,'2026-02-03 14:10:02','2026-02-03 14:10:02'),(4,'政策法规','POLICY','相关政策法规',4,'2026-02-03 14:10:02','2026-02-03 14:10:02'),(5,'行业资讯','INDUSTRY','行业相关资讯',5,'2026-02-03 14:10:02','2026-02-03 14:10:02');
/*!40000 ALTER TABLE `news_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织名称',
                                `organization_type_id` bigint DEFAULT NULL COMMENT '组织类型ID',
                                `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织代码',
                                `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '组织描述',
                                `leader_id` bigint DEFAULT NULL COMMENT '负责人ID',
                                `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人姓名',
                                `member_count` int DEFAULT '0' COMMENT '成员数量',
                                `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
                                `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院名称',
                                `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-非活跃, DISBANDED-已解散',
                                `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `code` (`code`) USING BTREE,
                                KEY `organization_type_id` (`organization_type_id`) USING BTREE,
                                KEY `leader_id` (`leader_id`) USING BTREE,
                                KEY `college_id` (`college_id`) USING BTREE,
                                KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                CONSTRAINT `organization_ibfk_1` FOREIGN KEY (`organization_type_id`) REFERENCES `organization_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `organization_ibfk_2` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `organization_ibfk_3` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `organization_ibfk_4` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='组织表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization`
--

LOCK TABLES `organization` WRITE;
/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
INSERT INTO `organization` VALUES (1,'科技创新协会',1,'TIA','学生科技创新社团',2,'张三',25,1,'计算机学院','ACTIVE','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,'智能硬件工作室',2,'IHW','专注智能硬件开发',5,'陈教授',12,1,'计算机学院','ACTIVE','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16');
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_member`
--

DROP TABLE IF EXISTS `organization_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization_member` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `organization_id` bigint NOT NULL COMMENT '组织ID',
                                       `user_id` bigint NOT NULL COMMENT '用户ID',
                                       `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户姓名',
                                       `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：LEADER-负责人, MEMBER-成员, ADVISOR-顾问',
                                       `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
                                       `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-非活跃',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `uk_org_user` (`organization_id`,`user_id`) USING BTREE,
                                       KEY `user_id` (`user_id`) USING BTREE,
                                       CONSTRAINT `organization_member_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                       CONSTRAINT `organization_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='组织成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_member`
--

LOCK TABLES `organization_member` WRITE;
/*!40000 ALTER TABLE `organization_member` DISABLE KEYS */;
INSERT INTO `organization_member` VALUES (1,1,2,'张三','LEADER','2026-02-03 14:10:16','ACTIVE'),(2,1,3,'李四','MEMBER','2026-02-03 14:10:16','ACTIVE'),(3,1,5,'王五','MEMBER','2026-02-03 14:10:16','ACTIVE'),(4,2,5,'陈教授','LEADER','2026-02-03 14:10:16','ACTIVE'),(5,2,2,'张三','MEMBER','2026-02-03 14:10:16','ACTIVE');
/*!40000 ALTER TABLE `organization_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_type`
--

DROP TABLE IF EXISTS `organization_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization_type` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织类型名称（如：社团、工作室、实验室等）',
                                     `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织类型代码',
                                     `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '组织类型描述',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='组织类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_type`
--

LOCK TABLES `organization_type` WRITE;
/*!40000 ALTER TABLE `organization_type` DISABLE KEYS */;
INSERT INTO `organization_type` VALUES (1,'学生社团','STUDENT_CLUB','学生自发组织的社团','2026-02-03 14:10:02','2026-02-03 14:10:02'),(2,'创新工作室','INNOVATION_STUDIO','创新工作室','2026-02-03 14:10:02','2026-02-03 14:10:02'),(3,'实验室','LABORATORY','实验室','2026-02-03 14:10:02','2026-02-03 14:10:02'),(4,'创业团队','STARTUP_TEAM','创业团队','2026-02-03 14:10:02','2026-02-03 14:10:02');
/*!40000 ALTER TABLE `organization_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_library`
--

DROP TABLE IF EXISTS `person_library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_library` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `person_type_id` bigint NOT NULL COMMENT '人员类型ID',
                                  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID（如果是校内人员）',
                                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
                                  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性别：MALE-男, FEMALE-女',
                                  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                                  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像URL',
                                  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职称/头衔',
                                  `organization` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属单位/企业',
                                  `position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职位',
                                  `research_direction` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '研究方向/专业领域',
                                  `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '主要成就/荣誉',
                                  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '个人简介',
                                  `expertise_areas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '专业领域（JSON数组）',
                                  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-非活跃',
                                  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'APPROVED' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                  `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                  `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `person_type_id` (`person_type_id`) USING BTREE,
                                  KEY `user_id` (`user_id`) USING BTREE,
                                  KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                  CONSTRAINT `person_library_ibfk_1` FOREIGN KEY (`person_type_id`) REFERENCES `person_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                  CONSTRAINT `person_library_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                  CONSTRAINT `person_library_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='人员库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_library`
--

LOCK TABLES `person_library` WRITE;
/*!40000 ALTER TABLE `person_library` DISABLE KEYS */;
INSERT INTO `person_library` VALUES (1,1,5,'陈教授','MALE','13800004444','chen@edu.cn',NULL,'副教授','计算机学院',NULL,'人工智能、机器学习','省科技进步奖','多年从事AI研究，指导多项国家级项目',NULL,'ACTIVE','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,1,5,'刘教授','MALE','13800005555','liu@edu.cn',NULL,'教授','机械学院',NULL,'智能制造、机器人','国家自然科学基金','机械领域专家',NULL,'ACTIVE','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(3,3,NULL,'李明','MALE','13900001111','liming@company.com',NULL,'技术总监','某科技公司',NULL,'云计算、大数据','行业领军人物','企业技术专家，多次担任大赛评委',NULL,'ACTIVE','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(4,4,2,'张三','MALE','13800001111','zhangsan@stu.edu.cn',NULL,'优秀学生','计算机学院',NULL,'软件开发','省赛一等奖','成绩优异，项目经验丰富',NULL,'ACTIVE','APPROVED',NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(5,1,NULL,'23','MALE','123456789','asd151325@qq.com',NULL,'11','11','11','11','11','11','[\"11\"]','ACTIVE','APPROVED',NULL,NULL,'2026-03-12 13:16:40','2026-03-12 13:16:40');
/*!40000 ALTER TABLE `person_library` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_type`
--

DROP TABLE IF EXISTS `person_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_type` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '人员类型名称',
                               `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '人员类型代码',
                               `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '人员类型描述',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='人员类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_type`
--

LOCK TABLES `person_type` WRITE;
/*!40000 ALTER TABLE `person_type` DISABLE KEYS */;
INSERT INTO `person_type` VALUES (1,'校内导师','ON_CAMPUS_MENTOR','校内指导教师','2026-02-03 14:10:02','2026-02-03 14:10:02'),(2,'校外导师','OFF_CAMPUS_MENTOR','校外指导教师','2026-02-03 14:10:02','2026-02-03 14:10:02'),(3,'行业专家','INDUSTRY_EXPERT','行业领域专家','2026-02-03 14:10:02','2026-02-03 14:10:02'),(4,'优秀学生','OUTSTANDING_STUDENT','优秀学生代表','2026-02-03 14:10:02','2026-02-03 14:10:02');
/*!40000 ALTER TABLE `person_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目标题',
                           `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目描述',
                           `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目类别',
                           `leader_id` bigint DEFAULT NULL COMMENT '项目负责人ID，NULL表示虚位以待',
                           `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人姓名',
                           `previous_leader_id` bigint DEFAULT NULL COMMENT '上一任负责人ID',
                           `previous_leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上一任负责人姓名',
                           `previous_leader_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上一任负责人联系方式',
                           `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导老师姓名',
                           `team_id` bigint DEFAULT NULL COMMENT '团队ID',
                           `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, IN_PROGRESS-进行中, COMPLETED-已完成',
                           `approval_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                           `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审核意见',
                           `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
                           `review_time` datetime DEFAULT NULL COMMENT '审核时间',
                           `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                           `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `leader_id` (`leader_id`) USING BTREE,
                           KEY `team_id` (`team_id`) USING BTREE,
                           CONSTRAINT `project_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                           CONSTRAINT `project_ibfk_2` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,'智能校园管理系统','基于AI的校园综合管理平台开发','软件开发',2,'张三',NULL,NULL,NULL,'1',1,'APPROVED','APPROVED',NULL,NULL,NULL,NULL,NULL,'2026-02-03 14:10:16','2026-03-10 05:55:14'),(2,'工业机器人视觉识别','机器视觉在工业检测中的应用研究','智能制造',5,'陈教授',NULL,NULL,NULL,NULL,2,'IN_PROGRESS','APPROVED',NULL,NULL,NULL,NULL,NULL,'2026-02-03 14:10:16','2026-03-10 05:56:02'),(3,'校园二手交易平台','大学生二手物品交易小程序','互联网+',3,'李四',NULL,NULL,NULL,NULL,3,'DRAFT','PENDING',NULL,NULL,NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(4,'创新创业大赛筹备','2024年创新创业大赛项目准备','竞赛',2,'张三',NULL,NULL,NULL,NULL,1,'PENDING','PENDING',NULL,NULL,NULL,NULL,NULL,'2026-02-03 14:10:16','2026-02-03 14:10:16'),(5,'1','1','1',5,'陈教授',4,'王五','13800003333','1',NULL,'APPROVED','APPROVED','',1,'2026-02-20 23:30:13','2026-02-20 23:30:13',NULL,'2026-02-20 23:28:21','2026-03-10 05:55:33'),(6,'1','1','1',4,'王五',NULL,NULL,NULL,'1',4,'APPROVED','APPROVED','',7,'2026-03-09 20:19:24','2026-03-12 00:00:00','2026-03-20 00:00:00','2026-03-09 18:45:16','2026-03-10 05:55:35'),(7,'2','2','2',4,'王五',NULL,NULL,NULL,'2',NULL,'APPROVED','APPROVED','',7,'2026-03-09 20:21:21','2026-03-11 00:00:00','2026-03-12 00:00:00','2026-03-09 20:20:56','2026-03-10 05:55:38'),(8,'1','1','1',3,'李四',NULL,NULL,NULL,'1',NULL,'REJECTED','REJECTED','1',7,'2026-03-09 20:23:18',NULL,NULL,'2026-03-09 20:22:54','2026-03-10 05:56:05'),(9,'3','3','3',3,'李四',NULL,NULL,NULL,'3',NULL,'APPROVED','APPROVED','',7,'2026-03-09 20:32:24','2026-03-05 00:00:00','2026-03-27 00:00:00','2026-03-09 20:32:06','2026-03-10 05:55:41'),(10,'3','3','3',5,'陈教授',3,'李四','13800002222','3',3,'APPROVED','APPROVED','',1,'2026-03-12 21:05:33','2026-03-12 21:05:33','2026-03-20 00:00:00','2026-03-09 20:58:59','2026-03-12 13:05:37'),(11,'.4','4','4',4,'王五',NULL,NULL,NULL,'4',NULL,'APPROVED','APPROVED','',1,'2026-03-10 14:20:25','2026-03-10 14:20:25','2026-03-21 00:00:00','2026-03-10 14:19:45','2026-03-10 06:20:27'),(12,'5','5','5',2,'张三',NULL,NULL,NULL,'陈教授',NULL,'APPROVED','APPROVED','',1,'2026-03-12 20:37:23','2026-03-12 20:37:23','2026-04-16 00:00:00','2026-03-12 20:36:47','2026-03-12 12:37:26'),(13,'5','5','5',2,'张三',NULL,NULL,NULL,'6',8,'APPROVED','APPROVED','',1,'2026-03-12 21:03:41','2026-03-12 21:03:41','2026-03-21 00:00:00','2026-03-12 21:03:16','2026-03-12 13:04:03');
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_application`
--

DROP TABLE IF EXISTS `project_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_application` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `application_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请编号',
                                       `project_id` bigint NOT NULL COMMENT '项目ID',
                                       `project_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目标题（冗余字段，便于查询）',
                                       `applicant_id` bigint NOT NULL COMMENT '申请人ID',
                                       `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                       `applicant_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人角色：STUDENT-学生, TEACHER-教师',
                                       `application_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请类型：JOIN_TEAM-加入团队, TAKE_OVER-接管项目',
                                       `application_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '申请内容',
                                       `qualifications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '资质说明',
                                       `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                       `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系邮箱',
                                       `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                       `approver_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批人角色：PROJECT_LEADER-项目负责人, COLLEGE_ADMIN-学院管理员',
                                       `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                       `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批人姓名',
                                       `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
                                       `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                                       `status` int DEFAULT '1' COMMENT '状态：1-有效, 0-无效',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `application_no` (`application_no`) USING BTREE,
                                       KEY `approver_id` (`approver_id`) USING BTREE,
                                       KEY `idx_project_id` (`project_id`) USING BTREE,
                                       KEY `idx_applicant_id` (`applicant_id`) USING BTREE,
                                       KEY `idx_approval_status` (`approval_status`) USING BTREE,
                                       KEY `idx_approver_role` (`approver_role`) USING BTREE,
                                       CONSTRAINT `project_application_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `project_application_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `project_application_ibfk_3` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_application`
--

LOCK TABLES `project_application` WRITE;
/*!40000 ALTER TABLE `project_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_application_form`
--

DROP TABLE IF EXISTS `project_application_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_application_form` (
                                            `id` bigint NOT NULL AUTO_INCREMENT,
                                            `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师姓名',
                                            `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '团队负责人姓名',
                                            `leader_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队负责人联系电话',
                                            `competition_registered` tinyint DEFAULT '0' COMMENT '是否已报名参加竞赛：0-否，1-是',
                                            `competition_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '竞赛名称（拟报或已报竞赛）',
                                            `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
                                            `project_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目简介',
                                            `project_achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目成绩（AB类赛事参赛所获最高荣誉奖项等）',
                                            `expected_outcomes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '预期成果',
                                            `leader_declaration` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队负责人声明内容',
                                            `leader_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队负责人签字',
                                            `leader_sign_date` date DEFAULT NULL COMMENT '团队负责人签字日期',
                                            `member_declaration` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队成员声明内容',
                                            `member_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队成员签字',
                                            `member_sign_date` date DEFAULT NULL COMMENT '团队成员签字日期',
                                            `instructor_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '指导教师意见',
                                            `instructor_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师签字（学院盖章）',
                                            `instructor_sign_date` date DEFAULT NULL COMMENT '指导教师签字日期',
                                            `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝',
                                            `applicant_id` bigint DEFAULT NULL COMMENT '申请人ID',
                                            `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `applicant_id` (`applicant_id`) USING BTREE,
                                            CONSTRAINT `project_application_form_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目申请表单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_application_form`
--

LOCK TABLES `project_application_form` WRITE;
/*!40000 ALTER TABLE `project_application_form` DISABLE KEYS */;
INSERT INTO `project_application_form` VALUES (1,'陈教授','张三','13800001111',0,NULL,'智能校园管理系统','基于AI的校园管理平台，提升管理效率',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'SUBMITTED',2,'张三','2026-02-03 14:10:16','2026-02-03 14:10:16'),(2,'刘教授','李四','13800002222',0,NULL,'校园二手交易平台','方便学生二手物品交易的小程序',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'DRAFT',3,'李四','2026-02-03 14:10:16','2026-02-03 14:10:16');
/*!40000 ALTER TABLE `project_application_form` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_application_member`
--

DROP TABLE IF EXISTS `project_application_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_application_member` (
                                              `id` bigint NOT NULL AUTO_INCREMENT,
                                              `application_form_id` bigint NOT NULL COMMENT '申请表单ID',
                                              `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学号',
                                              `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '姓名',
                                              `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业',
                                              `main_work` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '主要工作',
                                              `signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '签名',
                                              `sort_order` int DEFAULT '0' COMMENT '排序顺序',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `application_form_id` (`application_form_id`) USING BTREE,
                                              CONSTRAINT `project_application_member_ibfk_1` FOREIGN KEY (`application_form_id`) REFERENCES `project_application_form` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目申请表单成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_application_member`
--

LOCK TABLES `project_application_member` WRITE;
/*!40000 ALTER TABLE `project_application_member` DISABLE KEYS */;
INSERT INTO `project_application_member` VALUES (1,1,'2021001001','张三','计算机科学与技术','项目负责人、后端开发',NULL,1,'2026-02-03 14:10:16'),(2,1,'2021001002','李四','软件工程','前端开发',NULL,2,'2026-02-03 14:10:16'),(3,1,'2021001003','王五','数据科学','数据分析',NULL,3,'2026-02-03 14:10:16'),(4,2,'2021001002','李四','软件工程','项目负责人',NULL,1,'2026-02-03 14:10:16'),(5,2,'2021001001','张三','计算机科学与技术','技术支持',NULL,2,'2026-02-03 14:10:16');
/*!40000 ALTER TABLE `project_application_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_file`
--

DROP TABLE IF EXISTS `project_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_file` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `project_id` bigint NOT NULL COMMENT '项目ID',
                                `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
                                `original_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原始文件名',
                                `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件路径',
                                `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件类型',
                                `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
                                `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
                                `upload_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上传用户姓名',
                                `upload_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `project_id` (`project_id`) USING BTREE,
                                KEY `upload_user_id` (`upload_user_id`) USING BTREE,
                                CONSTRAINT `project_file_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                CONSTRAINT `project_file_ibfk_2` FOREIGN KEY (`upload_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_file`
--

LOCK TABLES `project_file` WRITE;
/*!40000 ALTER TABLE `project_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `space`
--

DROP TABLE IF EXISTS `space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `space` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '空间名称',
                         `space_type_id` bigint NOT NULL COMMENT '空间类型ID',
                         `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置',
                         `capacity` int DEFAULT NULL COMMENT '容量（人数）',
                         `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '设施设备（JSON格式）',
                         `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '空间描述',
                         `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用, MAINTENANCE-维护中, DISABLED-已禁用',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`) USING BTREE,
                         KEY `space_type_id` (`space_type_id`) USING BTREE,
                         CONSTRAINT `space_ibfk_1` FOREIGN KEY (`space_type_id`) REFERENCES `space_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='空间信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `space`
--

LOCK TABLES `space` WRITE;
/*!40000 ALTER TABLE `space` DISABLE KEYS */;
INSERT INTO `space` VALUES (1,'四楼A区会议室',1,'创新创业学院人工智能实践基地A区4楼',NULL,NULL,NULL,'AVAILABLE','2026-03-09 02:20:05','2026-03-12 11:09:54'),(2,'共创空间静音仓1',4,'共创空间',NULL,NULL,NULL,'AVAILABLE','2026-03-09 02:20:05','2026-03-12 11:11:06'),(3,'共创空间静音仓2',4,'共创空间',NULL,NULL,NULL,'AVAILABLE','2026-03-09 02:20:05','2026-03-10 13:59:37'),(4,'全景智慧多功能厅',3,'创新创业学院人工智能实践基地A区4楼A405',NULL,NULL,NULL,'AVAILABLE','2026-03-09 02:20:05','2026-03-10 13:59:40');
/*!40000 ALTER TABLE `space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `space_reservation`
--

DROP TABLE IF EXISTS `space_reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `space_reservation` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `space_id` bigint DEFAULT NULL COMMENT '空间ID，选其他时为NULL',
                                     `custom_space_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其他空间名称',
                                     `applicant_id` bigint NOT NULL COMMENT '申请人ID',
                                     `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                     `reservation_date` date NOT NULL COMMENT '预定日期',
                                     `start_time` time NOT NULL COMMENT '开始时间',
                                     `end_time` time NOT NULL COMMENT '结束时间',
                                     `purpose` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '使用目的',
                                     `attendee_count` int DEFAULT NULL COMMENT '预计参与人数',
                                     `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                     `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消, COMPLETED-已完成',
                                     `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                                     `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
                                     `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
                                     `review_time` datetime DEFAULT NULL COMMENT '审批时间',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `space_id` (`space_id`) USING BTREE,
                                     KEY `applicant_id` (`applicant_id`) USING BTREE,
                                     KEY `reviewer_id` (`reviewer_id`) USING BTREE,
                                     CONSTRAINT `space_reservation_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                     CONSTRAINT `space_reservation_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='空间预定表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `space_reservation`
--

LOCK TABLES `space_reservation` WRITE;
/*!40000 ALTER TABLE `space_reservation` DISABLE KEYS */;
INSERT INTO `space_reservation` VALUES (5,NULL,'你好',4,'王五','2026-02-20','10:00:00','11:00:00','fsdf',1,'18649061562','CANCELLED','APPROVED',1,'1','2026-02-19 22:13:04','2026-02-19 22:12:02','2026-02-19 14:13:44'),(9,7,NULL,2,'张三','2026-03-11','11:00:00','12:00:00','1',1,'13812345678','CANCELLED','APPROVED',7,'','2026-03-09 14:44:20','2026-03-09 14:44:13','2026-03-09 06:44:57'),(10,8,NULL,2,'张三','2026-03-26','10:00:00','11:00:00','1',1,'13812345677','CANCELLED','APPROVED',1,'','2026-03-09 15:33:05','2026-03-09 15:32:43','2026-03-09 07:33:11'),(11,7,NULL,2,'张三','2026-03-11','11:00:00','12:00:00','1',1,'13811112355','APPROVED','APPROVED',1,'','2026-03-10 16:02:37','2026-03-10 16:02:23','2026-03-10 08:02:39'),(12,7,NULL,5,'陈教授','2026-03-11','09:00:00','10:00:00','1',1,'18266632221','APPROVED','APPROVED',1,'','2026-03-10 16:02:59','2026-03-10 16:02:50','2026-03-10 08:03:01'),(13,1,NULL,2,'张三','2026-03-13','11:00:00','12:00:00','1',1,'13822213663','APPROVED','APPROVED',1,'','2026-03-12 20:40:33','2026-03-12 20:40:13','2026-03-12 12:40:36'),(14,2,NULL,2,'张三','2026-03-13','11:00:00','12:00:00','1',1,'13822255663','APPROVED','APPROVED',1,'','2026-03-12 21:10:53','2026-03-12 21:10:38','2026-03-12 13:10:56');
/*!40000 ALTER TABLE `space_reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `space_type`
--

DROP TABLE IF EXISTS `space_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `space_type` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '空间类型名称（如：会议室、实验室、路演厅等）',
                              `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '空间类型代码',
                              `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '空间类型描述',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='空间类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `space_type`
--

LOCK TABLES `space_type` WRITE;
/*!40000 ALTER TABLE `space_type` DISABLE KEYS */;
INSERT INTO `space_type` VALUES (1,'会议室','MEETING_ROOM','用于会议、讨论等','2026-02-03 14:10:02','2026-02-03 14:10:02'),(2,'实验室','LAB','用于实验、研发等','2026-02-03 14:10:02','2026-02-03 14:10:02'),(3,'路演厅','ROADSHOW','用于项目路演、展示等','2026-02-03 14:10:02','2026-02-03 14:10:02'),(4,'创客空间','MAKERSPACE','用于创客活动、制作等','2026-02-03 14:10:02','2026-02-03 14:10:02'),(5,'培训室','TRAINING_ROOM','用于培训、讲座等','2026-02-03 14:10:02','2026-02-03 14:10:02');
/*!40000 ALTER TABLE `space_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '团队名称',
                        `team_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队类型',
                        `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队描述',
                        `leader_id` bigint NOT NULL COMMENT '队长ID',
                        `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '队长姓名',
                        `leader_student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人学号',
                        `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院名',
                        `member_count` int DEFAULT '0' COMMENT '成员数量',
                        `recruiting` tinyint(1) DEFAULT '1' COMMENT '是否招募成员：1-是，0-否',
                        `is_public` tinyint(1) DEFAULT '1' COMMENT '团队是否公开：1-是，0-否',
                        `recruitment_requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '招募人员的要求',
                        `honors` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队历史荣誉',
                        `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目标签，逗号分隔',
                        `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导老师',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `is_deleted` tinyint(1) DEFAULT '0',
                        PRIMARY KEY (`id`) USING BTREE,
                        KEY `leader_id` (`leader_id`) USING BTREE,
                        CONSTRAINT `team_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='团队表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_member`
--

DROP TABLE IF EXISTS `team_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_member` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `team_id` bigint NOT NULL COMMENT '团队ID',
                               `user_id` bigint NOT NULL COMMENT '用户ID',
                               `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户姓名',
                               `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：LEADER-队长, MEMBER-成员',
                               `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常, INACTIVE-已退出',
                               `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'APPROVED' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
                               `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学号',
                               `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '年级',
                               `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业',
                               `competition_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '比赛经历',
                               `awards` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '获奖情况',
                               `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                               `resume_attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简历附件URL或路径',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `uk_team_user` (`team_id`,`user_id`) USING BTREE,
                               KEY `user_id` (`user_id`) USING BTREE,
                               CONSTRAINT `team_member_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                               CONSTRAINT `team_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='团队成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_member`
--

LOCK TABLES `team_member` WRITE;
/*!40000 ALTER TABLE `team_member` DISABLE KEYS */;
INSERT INTO `team_member` VALUES (1,1,2,'张三','LEADER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,1,3,'李四','MEMBER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,1,5,'陈教授','MEMBER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,2,5,'陈教授','LEADER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,2,6,'刘教授','MEMBER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,3,3,'李四','LEADER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,3,2,'张三','MEMBER','ACTIVE','APPROVED','2026-02-03 14:10:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,2,4,'王五','MEMBER','ACTIVE','REJECTED','2026-02-03 22:30:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,1,4,'1','MEMBER','ACTIVE','REJECTED','2026-02-20 22:31:14','','','','','','1381111111111',NULL),(13,4,4,'王五','LEADER','ACTIVE','APPROVED','2026-03-09 18:34:04',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,5,4,'王五','LEADER','ACTIVE','APPROVED','2026-03-11 12:40:09',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,6,3,'李四','LEADER','ACTIVE','APPROVED','2026-03-12 20:06:37',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,7,3,'李四','LEADER','ACTIVE','APPROVED','2026-03-12 20:09:09',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,8,2,'张三','LEADER','ACTIVE','APPROVED','2026-03-12 20:38:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,9,4,'王五','LEADER','ACTIVE','APPROVED','2026-03-12 20:39:40',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(19,7,2,'张三','MEMBER','ACTIVE','APPROVED','2026-03-12 20:44:49','111','2022','计算机科学与技术','','','13822213665',NULL),(20,8,3,'李四','MEMBER','ACTIVE','APPROVED','2026-03-12 21:07:35','222','2022','计算机科学与技术','','','13822255663',NULL),(21,10,2,'张三','LEADER','ACTIVE','APPROVED','2026-03-12 21:08:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,11,4,'王五','LEADER','ACTIVE','APPROVED','2026-03-12 21:09:48',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `team_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
                        `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                        `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '真实姓名',
                        `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                        `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                        `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：STUDENT, TEACHER, COLLEGE_ADMIN, SCHOOL_ADMIN',
                        `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
                        `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院名称',
                        `status` int DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `username` (`username`) USING BTREE,
                        KEY `college_id` (`college_id`) USING BTREE,
                        CONSTRAINT `user_ibfk_1` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$pJd9FzuPMUtLQzhJdOv2ZOosJiRyXHS/cilRvUqoDVFvyzZKSGRjO','系统管理员','admin@example.com',NULL,'SCHOOL_ADMIN',NULL,NULL,1,'2026-02-03 14:09:50','2026-02-03 14:41:26'),(2,'student1','$2a$10$EfWlYL7xZrQdCKzSM.4TLeYag9MZR99xXXTOSU4hCcpzS3aLSmIDm','张三','zhangsan@stu.edu.cn','13800001111','STUDENT',1,'计算机学院',1,'2026-02-03 14:10:16','2026-03-12 13:02:18'),(3,'student2','$2a$10$pJd9FzuPMUtLQzhJdOv2ZOosJiRyXHS/cilRvUqoDVFvyzZKSGRjO','李四','lisi@stu.edu.cn','13800002222','STUDENT',1,'计算机学院',1,'2026-02-03 14:10:16','2026-02-03 14:10:40'),(4,'student3','$2a$10$pJd9FzuPMUtLQzhJdOv2ZOosJiRyXHS/cilRvUqoDVFvyzZKSGRjO','王五','wangwu@stu.edu.cn','13800003333','STUDENT',2,'机械学院',1,'2026-02-03 14:10:16','2026-02-03 14:10:42'),(5,'teacher1','$2a$10$pJd9FzuPMUtLQzhJdOv2ZOosJiRyXHS/cilRvUqoDVFvyzZKSGRjO','陈教授','chen@edu.cn','13800004444','TEACHER',1,'计算机学院',1,'2026-02-03 14:10:16','2026-02-03 14:10:44'),(6,'teacher2','$2a$10$pJd9FzuPMUtLQzhJdOv2ZOosJiRyXHS/cilRvUqoDVFvyzZKSGRjO','刘教授','liu@edu.cn','13800005555','TEACHER',2,'机械学院',1,'2026-02-03 14:10:16','2026-02-03 14:10:47'),(7,'cadmin','$2a$10$pJd9FzuPMUtLQzhJdOv2ZOosJiRyXHS/cilRvUqoDVFvyzZKSGRjO','学院管理员','college_admin@edu.cn','13800006666','COLLEGE_ADMIN',1,'计算机学院',1,'2026-02-03 14:10:16','2026-03-09 07:32:17');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-23  9:14:25

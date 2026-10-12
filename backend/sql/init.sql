-- ============================================================
-- ZHSX 智能学习计划助手 - 数据库初始化脚本
-- 与后端实体（com.zhsx.entity.*）严格对应
-- 演示账号密码均为 123456（BCrypt 加密）
--
-- ⚠️ 脚本会先删除已有的 zhsx 库再重建，请确认无需要保留的数据
-- 使用方式（任选其一）：
--   1. mysql -uroot -p < backend/sql/init.sql
--   2. IDEA Database 面板 / Navicat / DataGrip 中直接运行本文件
-- ============================================================

DROP DATABASE IF EXISTS zhsx;
CREATE DATABASE zhsx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE zhsx;

-- ------------------------------------------------------------
-- 用户表
-- ------------------------------------------------------------
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL COMMENT '主键（应用内为雪花ID）',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE = InnoDB COMMENT = '用户表';

-- ------------------------------------------------------------
-- 学习目标表
-- ------------------------------------------------------------
CREATE TABLE `study_goal` (
    `id`          BIGINT        NOT NULL COMMENT '主键',
    `user_id`     BIGINT        NOT NULL COMMENT '所属用户',
    `title`       VARCHAR(100)  NOT NULL COMMENT '目标名称',
    `category`    VARCHAR(50)   NOT NULL COMMENT '类别',
    `description` VARCHAR(2000) DEFAULT NULL COMMENT '描述（用于自动拆分计划）',
    `start_date`  DATE          NOT NULL COMMENT '开始日期',
    `end_date`    DATE          NOT NULL COMMENT '结束日期',
    `daily_hours` DECIMAL(4, 1) NOT NULL COMMENT '每日学习时长（小时）',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1-进行中',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB COMMENT = '学习目标表';

-- ------------------------------------------------------------
-- 学习计划表
-- ------------------------------------------------------------
CREATE TABLE `study_plan` (
    `id`          BIGINT       NOT NULL COMMENT '主键',
    `goal_id`     BIGINT       NOT NULL COMMENT '所属目标',
    `title`       VARCHAR(200) NOT NULL COMMENT '计划标题',
    `content`     TEXT         COMMENT '当日学习内容',
    `plan_date`   DATE         NOT NULL COMMENT '计划日期',
    `duration`    INT          DEFAULT NULL COMMENT '计划时长（分钟）',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-未开始 1-进行中 2-已完成',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_goal_id` (`goal_id`)
) ENGINE = InnoDB COMMENT = '学习计划表';

-- ------------------------------------------------------------
-- 学习进度表
-- ------------------------------------------------------------
CREATE TABLE `study_progress` (
    `id`              BIGINT        NOT NULL COMMENT '主键',
    `plan_id`         BIGINT        NOT NULL COMMENT '计划ID',
    `user_id`         BIGINT        NOT NULL COMMENT '用户ID',
    `completion_rate` DECIMAL(5, 2) NOT NULL DEFAULT 0 COMMENT '完成度百分比',
    `actual_duration` INT           DEFAULT NULL COMMENT '实际时长（分钟）',
    `note`            VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB COMMENT = '学习进度表';

-- ------------------------------------------------------------
-- 学习提醒表
-- ------------------------------------------------------------
CREATE TABLE `study_reminder` (
    `id`          BIGINT       NOT NULL COMMENT '主键',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `plan_id`     BIGINT       NOT NULL COMMENT '关联计划ID',
    `remind_type` TINYINT      DEFAULT NULL COMMENT '提醒类型：1-邮件',
    `remind_time` DATETIME     NOT NULL COMMENT '提醒时间',
    `content`     VARCHAR(500) DEFAULT NULL COMMENT '提醒内容',
    `is_sent`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已发送：0-否 1-是',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_plan_id` (`plan_id`)
) ENGINE = InnoDB COMMENT = '学习提醒表';

-- ------------------------------------------------------------
-- 学习资源表
-- ------------------------------------------------------------
CREATE TABLE `study_resource` (
    `id`          BIGINT        NOT NULL COMMENT '主键',
    `category`    VARCHAR(50)   NOT NULL COMMENT '类别（与目标类别匹配用于推荐）',
    `title`       VARCHAR(200)  NOT NULL COMMENT '资源标题',
    `type`        TINYINT       DEFAULT NULL COMMENT '类型：1-电子书 2-视频 3-文章 4-其他',
    `url`         VARCHAR(500)  DEFAULT NULL COMMENT '资源链接',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '资源简介',
    `cover`       VARCHAR(500)  DEFAULT NULL COMMENT '封面图地址',
    `rating`      DECIMAL(2, 1) DEFAULT NULL COMMENT '评分（0-9.9）',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`)
) ENGINE = InnoDB COMMENT = '学习资源表';

-- ============================================================
-- 种子数据
-- 演示账号：demo / 123456    测试账号：test / 123456
-- ============================================================

-- 用户
INSERT INTO `user` (`id`, `username`, `email`, `phone`, `password`, `avatar`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'demo', 'demo@qq.com', '13800000001', '$2a$10$TXlPFsYzJNfJvDRrN2LMHODdPMN0vDVryCz6x6ht5r1sijadrgVXe', NULL, 1, '2026-10-01 09:00:00', '2026-10-01 09:00:00', 0),
(2, 'test', 'test@qq.com', '13800000002', '$2a$10$TXlPFsYzJNfJvDRrN2LMHODdPMN0vDVryCz6x6ht5r1sijadrgVXe', NULL, 1, '2026-10-01 09:05:00', '2026-10-01 09:05:00', 0);

-- 学习目标
INSERT INTO `study_goal` (`id`, `user_id`, `title`, `category`, `description`, `start_date`, `end_date`, `daily_hours`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, 'Java 后端学习计划', '编程开发', 'Java 基础语法。集合框架。Spring Boot 入门。MySQL 数据库。Redis 缓存实战', '2026-10-01', '2026-10-07', 2.0, 1, '2026-10-01 10:00:00', '2026-10-01 10:00:00', 0),
(2, 1, '英语四级备考', '语言学习', '高频词汇积累。听力精听训练。阅读理解技巧。写作模板练习', '2026-10-01', '2026-10-14', 1.5, 1, '2026-10-01 10:10:00', '2026-10-01 10:10:00', 0),
(3, 2, '数据结构复习', '计算机基础', '链表与数组。二叉树遍历。排序算法对比', '2026-10-05', '2026-10-06', 1.0, 1, '2026-10-05 08:00:00', '2026-10-05 08:00:00', 0);

-- 学习计划（目标1：7天）
INSERT INTO `study_plan` (`id`, `goal_id`, `title`, `content`, `plan_date`, `duration`, `status`, `sort_order`, `create_time`, `deleted`) VALUES
(101, 1, '第1天：Java 后端学习计划', 'Java 基础语法', '2026-10-01', 120, 2, 0, '2026-10-01 10:01:00', 0),
(102, 1, '第2天：Java 后端学习计划', '集合框架', '2026-10-02', 120, 2, 1, '2026-10-01 10:01:00', 0),
(103, 1, '第3天：Java 后端学习计划', 'Spring Boot 入门', '2026-10-03', 120, 1, 2, '2026-10-01 10:01:00', 0),
(104, 1, '第4天：Java 后端学习计划', 'MySQL 数据库', '2026-10-04', 120, 0, 3, '2026-10-01 10:01:00', 0),
(105, 1, '第5天：Java 后端学习计划', 'Redis 缓存实战', '2026-10-05', 120, 0, 4, '2026-10-01 10:01:00', 0),
(106, 1, '第6天：Java 后端学习计划', '综合练习', '2026-10-06', 120, 0, 5, '2026-10-01 10:01:00', 0),
(107, 1, '第7天：Java 后端学习计划', '阶段复习', '2026-10-07', 120, 0, 6, '2026-10-01 10:01:00', 0);

-- 学习计划（目标2：4天）
INSERT INTO `study_plan` (`id`, `goal_id`, `title`, `content`, `plan_date`, `duration`, `status`, `sort_order`, `create_time`, `deleted`) VALUES
(201, 2, '第1天：英语四级备考', '高频词汇积累', '2026-10-01', 90, 1, 0, '2026-10-01 10:11:00', 0),
(202, 2, '第2天：英语四级备考', '听力精听训练', '2026-10-02', 90, 0, 1, '2026-10-01 10:11:00', 0),
(203, 2, '第3天：英语四级备考', '阅读理解技巧', '2026-10-03', 90, 0, 2, '2026-10-01 10:11:00', 0),
(204, 2, '第4天：英语四级备考', '写作模板练习', '2026-10-04', 90, 0, 3, '2026-10-01 10:11:00', 0);

-- 学习计划（目标3：用户 test）
INSERT INTO `study_plan` (`id`, `goal_id`, `title`, `content`, `plan_date`, `duration`, `status`, `sort_order`, `create_time`, `deleted`) VALUES
(301, 3, '第1天：数据结构复习', '链表与数组', '2026-10-05', 60, 0, 0, '2026-10-05 08:01:00', 0),
(302, 3, '第2天：数据结构复习', '二叉树遍历。排序算法对比', '2026-10-06', 60, 0, 1, '2026-10-05 08:01:00', 0);

-- 学习进度（对应已完成/进行中的计划）
INSERT INTO `study_progress` (`id`, `plan_id`, `user_id`, `completion_rate`, `actual_duration`, `note`, `update_time`) VALUES
(1001, 101, 1, 100.00, 130, '基础语法掌握良好', '2026-10-01 21:30:00'),
(1002, 102, 1, 100.00, 115, '集合框架完成', '2026-10-02 21:10:00'),
(1003, 103, 1, 50.00, 60, 'Spring Boot 进行中', '2026-10-03 20:00:00'),
(1004, 201, 1, 50.00, 45, '背了 200 个高频词', '2026-10-01 22:00:00');

-- 学习提醒（一已发送 + 一待发送）
INSERT INTO `study_reminder` (`id`, `user_id`, `plan_id`, `remind_type`, `remind_time`, `content`, `is_sent`, `create_time`, `deleted`) VALUES
(4001, 1, 103, 1, '2026-10-11 20:00:00', '记得完成今天的 Spring Boot 学习任务', 1, '2026-10-10 19:00:00', 0),
(4002, 1, 104, 1, '2026-10-13 09:00:00', '今天开始 MySQL 数据库部分的学习', 0, '2026-10-10 19:05:00', 0);

-- 学习资源（类别与目标类别一致，便于推荐接口命中）
INSERT INTO `study_resource` (`id`, `category`, `title`, `type`, `url`, `description`, `cover`, `rating`, `create_time`) VALUES
(5001, '编程开发', 'Java 核心技术（卷 I）', 1, 'https://book.douban.com/subject/27077737/', 'Java 入门到进阶经典教材，覆盖语法、面向对象、集合与并发基础', NULL, 4.8, '2026-09-20 10:00:00'),
(5002, '编程开发', 'Spring Boot 官方文档', 3, 'https://spring.io/projects/spring-boot', '官方权威指南，适合查阅 Spring Boot 各项特性与配置', NULL, 4.7, '2026-09-20 10:05:00'),
(5003, '编程开发', 'MySQL 必知必会', 1, 'https://book.douban.com/subject/3356980/', 'SQL 基础查询与数据库设计入门首选', NULL, 4.5, '2026-09-20 10:10:00'),
(5004, '编程开发', 'Redis 设计与实现', 1, 'https://book.douban.com/subject/25900156/', '深入理解 Redis 数据结构与持久化机制', NULL, 4.6, '2026-09-20 10:15:00'),
(5005, '语言学习', '英语四级高频词汇带背', 2, 'https://www.bilibili.com', '四级高频词汇视频带背课程', NULL, 4.4, '2026-09-20 10:20:00'),
(5006, '语言学习', '四级写作万能模板', 3, 'https://zhuanlan.zhihu.com', '历年真题写作模板与范文解析', NULL, 4.3, '2026-09-20 10:25:00'),
(5007, '计算机基础', '大话数据结构', 1, 'https://book.douban.com/subject/33189862/', '通俗易懂的数据结构入门读物', NULL, 4.5, '2026-09-20 10:30:00');

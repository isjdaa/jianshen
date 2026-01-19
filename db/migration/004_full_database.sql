-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS stu_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE stu_manage;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS tb_customer;
DROP TABLE IF EXISTS tb_coach;
DROP TABLE IF EXISTS tb_admin;

-- 创建管理员表
CREATE TABLE tb_admin (
    username VARCHAR(64) PRIMARY KEY,
    password VARCHAR(128) NOT NULL
);

-- 创建教练表
CREATE TABLE tb_coach (
    id VARCHAR(64) PRIMARY KEY,
    password VARCHAR(128) NOT NULL,
    name VARCHAR(64),
    tele VARCHAR(32),
    specialization VARCHAR(255),
    gender VARCHAR(8),
    age INT,
    address VARCHAR(255)
);

-- 创建客户表
CREATE TABLE tb_customer (
    id VARCHAR(64) PRIMARY KEY,
    password VARCHAR(128) NOT NULL,
    name VARCHAR(64),
    tele VARCHAR(32),
    joindate DATETIME,
    age INT,
    gender VARCHAR(8),
    address VARCHAR(255),
    membership_type VARCHAR(32),
    expiry_date DATETIME,
    balance DECIMAL(10,2),
    coach_id VARCHAR(64),
    FOREIGN KEY (coach_id) REFERENCES tb_coach(id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_customer_coach ON tb_customer(coach_id);
CREATE INDEX idx_coach_name ON tb_coach(name);
CREATE INDEX idx_coach_specialization ON tb_coach(specialization);

-- 插入初始数据

-- 插入管理员数据（密码：admin123，未加密）
INSERT INTO tb_admin (username, password) VALUES ('admin', 'admin123');

-- 插入教练数据
INSERT INTO tb_coach (id, password, name, tele, specialization, gender, age, address) VALUES 
('COACH001', '123456', '张三', '13800138001', '健身教练', '男', 28, '北京市朝阳区'),
('COACH002', '123456', '李四', '13800138002', '瑜伽教练', '女', 26, '北京市海淀区'),
('COACH003', '123456', '王五', '13800138003', '游泳教练', '男', 30, '北京市西城区');

-- 插入客户数据
INSERT INTO tb_customer (id, password, name, tele, joindate, age, gender, address, membership_type, expiry_date, balance, coach_id) VALUES 
('CUST001', '123456', '赵六', '13800138004', NOW(), 25, '男', '北京市丰台区', '普通会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 0.00, 'COACH001'),
('CUST002', '123456', '孙七', '13800138005', NOW(), 23, '女', '北京市石景山区', '银卡会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 500.00, 'COACH002'),
('CUST003', '123456', '周八', '13800138006', NOW(), 27, '男', '北京市通州区', '金卡会员', DATE_ADD(NOW(), INTERVAL 2 YEAR), 1000.00, 'COACH003');
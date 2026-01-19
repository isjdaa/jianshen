-- 迁移脚本：创建客户表和教练表

-- 删除旧的学生表（如果存在）
DROP TABLE IF EXISTS tb_student;

-- 删除旧的班级表（如果存在）
DROP TABLE IF EXISTS tb_clazz;

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
    coach_id VARCHAR(64)
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

-- 创建索引
CREATE INDEX idx_customer_coach ON tb_customer(coach_id);
CREATE INDEX idx_coach_name ON tb_coach(name);
CREATE INDEX idx_coach_specialization ON tb_coach(specialization);
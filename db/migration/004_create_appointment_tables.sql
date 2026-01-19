-- 迁移脚本：创建预约表

-- 创建预约表
CREATE TABLE tb_appointment (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    coach_id VARCHAR(64) NOT NULL,
    appointment_date DATETIME NOT NULL,
    appointment_time VARCHAR(32) NOT NULL,
    status VARCHAR(16) DEFAULT '待确认', -- 待确认、已确认、已完成、已取消
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    remarks VARCHAR(255)
);

-- 创建课程表
CREATE TABLE tb_course (
    id VARCHAR(64) PRIMARY KEY,
    course_name VARCHAR(64) NOT NULL,
    coach_id VARCHAR(64) NOT NULL,
    course_time DATETIME NOT NULL,
    duration INT NOT NULL, -- 课程时长（分钟）
    max_students INT NOT NULL, -- 最大人数
    current_students INT DEFAULT 0, -- 当前报名人数
    status VARCHAR(16) DEFAULT '未开始', -- 未开始、进行中、已结束
    description VARCHAR(255)
);

-- 创建课程预约表
CREATE TABLE tb_course_appointment (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    course_id VARCHAR(64) NOT NULL,
    appointment_time DATETIME NOT NULL,
    status VARCHAR(16) DEFAULT '已预约', -- 已预约、已参加、已取消
    create_time DATETIME NOT NULL
);

-- 创建索引
CREATE INDEX idx_appointment_customer ON tb_appointment(customer_id);
CREATE INDEX idx_appointment_coach ON tb_appointment(coach_id);
CREATE INDEX idx_course_coach ON tb_course(coach_id);
CREATE INDEX idx_course_appointment_customer ON tb_course_appointment(customer_id);
CREATE INDEX idx_course_appointment_course ON tb_course_appointment(course_id);

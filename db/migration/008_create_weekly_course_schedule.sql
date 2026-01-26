-- 迁移脚本：创建周循环课程表
-- 优化排课系统，支持教练每周循环排课

-- 创建周循环课程模板表
CREATE TABLE tb_weekly_course_schedule (
    id VARCHAR(64) PRIMARY KEY,
    coach_id VARCHAR(64) NOT NULL,
    course_name VARCHAR(128) NOT NULL,
    day_of_week TINYINT NOT NULL, -- 1=周一, 2=周二, ..., 7=周日
    start_time TIME NOT NULL, -- 课程开始时间 (HH:mm:ss)
    duration INT NOT NULL, -- 课程时长（分钟）
    max_students INT NOT NULL, -- 最大人数
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE, -- 是否启用
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_coach_weekday (coach_id, day_of_week),
    INDEX idx_active_schedule (is_active, day_of_week)
);

-- 创建课程生成记录表（记录哪些日期已生成课程）
CREATE TABLE tb_course_generation_log (
    id VARCHAR(64) PRIMARY KEY,
    schedule_id VARCHAR(64) NOT NULL, -- 对应的周循环模板ID
    generated_date DATE NOT NULL, -- 生成的日期
    course_id VARCHAR(64), -- 生成的课程ID
    generation_time DATETIME NOT NULL,
    UNIQUE KEY uk_schedule_date (schedule_id, generated_date),
    INDEX idx_generation_date (generated_date)
);

-- 修改原有课程表，添加来源标识
ALTER TABLE tb_course ADD COLUMN schedule_id VARCHAR(64);
ALTER TABLE tb_course ADD COLUMN is_generated BOOLEAN DEFAULT FALSE;
ALTER TABLE tb_course ADD INDEX idx_schedule_source (schedule_id, is_generated);

-- 插入测试数据：创建一些周循环课程模板
INSERT INTO tb_weekly_course_schedule (id, coach_id, course_name, day_of_week, start_time, duration, max_students, description, create_time, update_time) VALUES
('SCH001', 'COACH001', '晨间瑜伽', 1, '08:00:00', 60, 15, '周一晨间瑜伽课程，帮助开启美好的一天', NOW(), NOW()),
('SCH002', 'COACH001', '晚间普拉提', 3, '19:00:00', 45, 12, '周三晚间普拉提课程，缓解工作压力', NOW(), NOW()),
('SCH003', 'COACH001', '周末HIIT', 6, '10:00:00', 50, 20, '周六HIIT高强度训练，燃脂塑形', NOW(), NOW()),
('SCH004', 'COACH002', '太极拳入门', 2, '14:00:00', 90, 10, '周二下午太极拳课程，适合初学者', NOW(), NOW()),
('SCH005', 'COACH002', '健身指导', 4, '16:00:00', 60, 8, '周四下午私人健身指导课程', NOW(), NOW()),
('SCH006', 'COACH003', '搏击训练', 1, '20:00:00', 75, 6, '周一晚间搏击训练课程', NOW(), NOW()),
('SCH007', 'COACH003', '体能提升', 5, '18:30:00', 60, 10, '周五晚间体能提升训练', NOW(), NOW());

-- 更新注释
ALTER TABLE tb_course COMMENT '课程表 - 支持手动创建和自动生成';
ALTER TABLE tb_weekly_course_schedule COMMENT '周循环课程模板表';
ALTER TABLE tb_course_generation_log COMMENT '课程生成日志表';
-- 使用数据库
USE stu_manage;

-- 1. 插入管理员数据（如果不存在）
INSERT IGNORE INTO tb_admin (username, password) VALUES ('admin', 'admin123');

-- 2. 插入教练数据（如果不存在）
INSERT IGNORE INTO tb_coach (id, password, name, tele, specialization, gender, age, address) VALUES 
('COACH001', '123456', '张三', '13800138001', '健身教练', '男', 28, '北京市朝阳区'),
('COACH002', '123456', '李四', '13800138002', '瑜伽教练', '女', 26, '北京市海淀区'),
('COACH003', '123456', '王五', '13800138003', '游泳教练', '男', 30, '北京市西城区'),
('COACH004', '123456', '赵教练', '13800138004', '普拉提教练', '女', 27, '北京市东城区'),
('COACH005', '123456', '钱教练', '13800138005', '拳击教练', '男', 32, '北京市丰台区');

-- 3. 插入客户数据（如果不存在）
INSERT IGNORE INTO tb_customer (id, password, name, tele, joindate, age, gender, address, membership_type, expiry_date, balance, coach_id) VALUES 
('CUST001', '123456', '赵六', '13800138006', NOW(), 25, '男', '北京市丰台区', '普通会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 0.00, 'COACH001'),
('CUST002', '123456', '孙七', '13800138007', NOW(), 23, '女', '北京市石景山区', '银卡会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 500.00, 'COACH002'),
('CUST003', '123456', '周八', '13800138008', NOW(), 27, '男', '北京市通州区', '金卡会员', DATE_ADD(NOW(), INTERVAL 2 YEAR), 1000.00, 'COACH003'),
('CUST004', '123456', '吴九', '13800138009', NOW(), 24, '女', '北京市大兴区', '普通会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 200.00, 'COACH004'),
('CUST005', '123456', '郑十', '13800138010', NOW(), 29, '男', '北京市顺义区', '银卡会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 600.00, 'COACH005'),
('CUST006', '123456', '王十一', '13800138011', NOW(), 22, '女', '北京市昌平区', '普通会员', DATE_ADD(NOW(), INTERVAL 1 YEAR), 0.00, 'COACH001'),
('CUST007', '123456', '李十二', '13800138012', NOW(), 31, '男', '北京市房山区', '金卡会员', DATE_ADD(NOW(), INTERVAL 2 YEAR), 1500.00, 'COACH002');

-- 4. 插入课程数据（如果不存在）
INSERT IGNORE INTO tb_course (id, course_name, coach_id, course_time, duration, max_students, current_students, status, description) VALUES
('COURSE001', '基础健身课程', 'COACH001', DATE_ADD(NOW(), INTERVAL 1 DAY), 60, 10, 3, '未开始', '适合初学者的基础健身课程'),
('COURSE002', '瑜伽入门', 'COACH002', DATE_ADD(NOW(), INTERVAL 2 DAY), 90, 15, 5, '未开始', '放松身心的瑜伽入门课程'),
('COURSE003', '游泳提高班', 'COACH003', DATE_ADD(NOW(), INTERVAL 3 DAY), 120, 8, 2, '未开始', '提高游泳技巧的进阶课程'),
('COURSE004', '普拉提核心训练', 'COACH004', DATE_ADD(NOW(), INTERVAL 4 DAY), 75, 12, 4, '未开始', '专注核心力量的普拉提课程'),
('COURSE005', '拳击基础', 'COACH005', DATE_ADD(NOW(), INTERVAL 5 DAY), 90, 10, 3, '未开始', '学习拳击技巧和防身术'),
('COURSE006', '高级健身课程', 'COACH001', DATE_ADD(NOW(), INTERVAL 6 DAY), 60, 8, 2, '未开始', '适合有经验健身者的高级课程'),
('COURSE007', '高温瑜伽', 'COACH002', DATE_ADD(NOW(), INTERVAL 7 DAY), 90, 12, 4, '未开始', '在高温环境下的高强度瑜伽课程'),
-- 添加当前时间段内的测试课程（用于测试签到功能）
('COURSE_TEST_001', '测试签到课程-张三', 'COACH001', DATE_ADD(NOW(), INTERVAL -30 MINUTE), 60, 10, 1, '进行中', '测试签到功能专用课程'),
('COURSE_TEST_002', '测试签到课程-李四', 'COACH002', DATE_ADD(NOW(), INTERVAL -45 MINUTE), 90, 15, 1, '进行中', '测试签到功能专用课程'),
('COURSE_TEST_003', '测试签到课程-王五', 'COACH003', NOW(), 120, 8, 1, '进行中', '测试签到功能专用课程');

-- 5. 插入预约数据（如果不存在）
INSERT IGNORE INTO tb_appointment (id, customer_id, coach_id, appointment_date, appointment_time, status, create_time, update_time, remarks) VALUES 
('APT001', 'CUST001', 'COACH001', DATE_ADD(NOW(), INTERVAL 1 DAY), '14:00-15:00', '已确认', NOW(), NOW(), '第一次预约，希望学习基础健身'),
('APT002', 'CUST002', 'COACH002', DATE_ADD(NOW(), INTERVAL 2 DAY), '16:00-17:00', '待确认', NOW(), NOW(), '想学习瑜伽放松'),
('APT003', 'CUST003', 'COACH003', DATE_ADD(NOW(), INTERVAL 3 DAY), '10:00-11:00', '已确认', NOW(), NOW(), '想提高游泳技术'),
('APT004', 'CUST004', 'COACH004', DATE_ADD(NOW(), INTERVAL 4 DAY), '19:00-20:00', '待确认', NOW(), NOW(), '想尝试普拉提'),
('APT005', 'CUST005', 'COACH005', DATE_ADD(NOW(), INTERVAL 5 DAY), '15:00-16:00', '已确认', NOW(), NOW(), '想学习拳击防身');

-- 6. 插入课程预约数据（如果不存在）
INSERT IGNORE INTO tb_course_appointment (id, customer_id, course_id, appointment_time, status, create_time) VALUES 
('COURSEAPT001', 'CUST001', 'COURSE001', NOW(), '已预约', NOW()),
('COURSEAPT002', 'CUST002', 'COURSE002', NOW(), '已预约', NOW()),
('COURSEAPT003', 'CUST003', 'COURSE003', NOW(), '已预约', NOW()),
('COURSEAPT004', 'CUST004', 'COURSE004', NOW(), '已预约', NOW()),
('COURSEAPT005', 'CUST005', 'COURSE005', NOW(), '已预约', NOW()),
('COURSEAPT006', 'CUST006', 'COURSE001', NOW(), '已预约', NOW()),
('COURSEAPT007', 'CUST007', 'COURSE002', NOW(), '已预约', NOW()),
('COURSEAPT008', 'CUST001', 'COURSE006', NOW(), '已预约', NOW()),
('COURSEAPT009', 'CUST002', 'COURSE007', NOW(), '已预约', NOW()),
('COURSEAPT010', 'CUST003', 'COURSE001', NOW(), '已预约', NOW());

-- 7. 更新课程当前人数（确保与课程预约数量一致）
UPDATE tb_course c
SET current_students = (
    SELECT COUNT(*)
    FROM tb_course_appointment ca
    WHERE ca.course_id = c.id AND ca.status = '已预约'
);

-- 显示插入结果
SELECT '数据插入完成！' AS result;
SELECT '管理员数量：', COUNT(*) FROM tb_admin;
SELECT '教练数量：', COUNT(*) FROM tb_coach;
SELECT '客户数量：', COUNT(*) FROM tb_customer;
SELECT '课程数量：', COUNT(*) FROM tb_course;
SELECT '预约数量：', COUNT(*) FROM tb_appointment;
SELECT '课程预约数量：', COUNT(*) FROM tb_course_appointment;
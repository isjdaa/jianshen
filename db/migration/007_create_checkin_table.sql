-- 创建教练签到记录表
CREATE TABLE IF NOT EXISTS check_in_record (
    id VARCHAR(64) PRIMARY KEY,
    coach_id VARCHAR(64) NOT NULL,
    coach_name VARCHAR(64) NOT NULL,
    check_type VARCHAR(16) NOT NULL, -- 上班签到、下班签退
    check_time DATETIME NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_checkin_coach ON check_in_record(coach_id);
CREATE INDEX idx_checkin_time ON check_in_record(check_time);
CREATE INDEX idx_checkin_type ON check_in_record(check_type);

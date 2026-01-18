-- 会员表示例（可根据现有数据库调整字段类型/长度）
CREATE TABLE member (
  id VARCHAR(64) PRIMARY KEY,
  password VARCHAR(128) NOT NULL,
  name VARCHAR(64),
  tele VARCHAR(32),
  joindate DATETIME,
  age INT,
  gender CHAR(1),
  address VARCHAR(255),
  membership_type VARCHAR(32),
  expiry_date DATETIME,
  balance DECIMAL(10,2),
  trainer_no VARCHAR(64)
);
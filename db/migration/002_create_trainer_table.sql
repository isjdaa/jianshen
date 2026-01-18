-- Migration script to create trainer table
CREATE TABLE tb_trainer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    specialization VARCHAR(255)
);

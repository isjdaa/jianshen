-- Migration script to migrate classes to trainers
INSERT INTO tb_trainer (id, specialization)
SELECT id, specialization FROM tb_clazz;
DELETE FROM tb_clazz;

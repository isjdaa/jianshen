-- Migration script to migrate students to members
INSERT INTO tb_member (id, name)
SELECT id, name FROM tb_student;
DELETE FROM tb_student;

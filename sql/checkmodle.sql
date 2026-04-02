-- checkModule相关的ddl

-- check_module_id外键
-- ALTER TABLE <子表名>
--     ADD CONSTRAINT fk_<子表短名>_cm
--     FOREIGN KEY (cm_id) REFERENCES check_module (id)
--     ON DELETE RESTRICT
--   ON UPDATE RESTRICT;

-- check_item添加外键
ALTER TABLE check_item
    ADD CONSTRAINT fk_check_item_cm
        FOREIGN KEY (cm_id) REFERENCES check_module (id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;


-- check_module_instru 添加check_module外键
ALTER TABLE check_module_instru
    ADD CONSTRAINT fk_check_module_instru_cm
        FOREIGN KEY (cm_id) REFERENCES check_module (id)
            ON DELETE  NO ACTION
            ON UPDATE  NO ACTION;


-- Adiciona role na tb_cliente apenas se não existir
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM all_tab_columns
    WHERE table_name = 'TB_CLIENTE'
    AND column_name = 'ROLE'
    AND owner = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA');

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE tb_cliente ADD role VARCHAR2(255)';
    END IF;
END;
/

-- Renomeia cargo para role na tb_funcionario apenas se cargo existir
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM all_tab_columns
    WHERE table_name = 'TB_FUNCIONARIO'
    AND column_name = 'CARGO'
    AND owner = SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA');

    IF v_count > 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE tb_funcionario RENAME COLUMN cargo TO role';
    END IF;
END;
/
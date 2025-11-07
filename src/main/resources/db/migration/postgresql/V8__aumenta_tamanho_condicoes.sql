DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_name='locacao' AND column_name='condicao_devolucao') THEN
    EXECUTE 'ALTER TABLE locacao ALTER COLUMN condicao_devolucao TYPE VARCHAR(255)';
END IF;

  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_name='locacao' AND column_name='condicao_entrega') THEN
    EXECUTE 'ALTER TABLE locacao ALTER COLUMN condicao_entrega TYPE VARCHAR(255)';
END IF;
END $$;

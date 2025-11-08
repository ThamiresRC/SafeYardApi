DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename='locacao') THEN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ix_locacao_moto') THEN
      EXECUTE 'CREATE INDEX ix_locacao_moto ON locacao(moto_id)';
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ix_locacao_cliente') THEN
      EXECUTE 'CREATE INDEX ix_locacao_cliente ON locacao(cliente_id)';
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ix_locacao_saida') THEN
      EXECUTE 'CREATE INDEX ix_locacao_saida ON locacao(data_saida)';
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ix_locacao_devolucao') THEN
      EXECUTE 'CREATE INDEX ix_locacao_devolucao ON locacao(data_devolucao)';
END IF;
END IF;
END $$;

DO $$
BEGIN
  -- cliente(cpf)
  IF EXISTS (SELECT 1 FROM pg_tables WHERE schemaname='public' AND tablename='cliente') THEN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_cliente_cpf') THEN
      EXECUTE 'CREATE INDEX idx_cliente_cpf ON cliente(cpf)';
END IF;
END IF;

  -- moto(placa)
  IF EXISTS (SELECT 1 FROM pg_tables WHERE schemaname='public' AND tablename='moto') THEN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_moto_placa') THEN
      EXECUTE 'CREATE INDEX idx_moto_placa ON moto(placa)';
END IF;
END IF;

  -- locacao(cliente_id, moto_id, data_saida)
  IF EXISTS (SELECT 1 FROM pg_tables WHERE schemaname='public' AND tablename='locacao') THEN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_locacao_cliente') THEN
      EXECUTE 'CREATE INDEX idx_locacao_cliente ON locacao(cliente_id)';
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_locacao_moto') THEN
      EXECUTE 'CREATE INDEX idx_locacao_moto ON locacao(moto_id)';
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_locacao_data_saida') THEN
      EXECUTE 'CREATE INDEX idx_locacao_data_saida ON locacao(data_saida)';
END IF;
END IF;

  -- registro_moto_patio(moto_id)
  IF EXISTS (SELECT 1 FROM pg_tables WHERE schemaname='public' AND tablename='registro_moto_patio') THEN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_registro_moto') THEN
      EXECUTE 'CREATE INDEX idx_registro_moto ON registro_moto_patio(moto_id)';
END IF;
END IF;
END $$;

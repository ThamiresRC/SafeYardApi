CREATE INDEX IF NOT EXISTS ix_locacao_moto    ON locacao(moto_id);
CREATE INDEX IF NOT EXISTS ix_locacao_cliente ON locacao(cliente_id);
CREATE INDEX IF NOT EXISTS ix_locacao_saida   ON locacao(data_saida);
CREATE INDEX IF NOT EXISTS ix_locacao_devol   ON locacao(data_devolucao);

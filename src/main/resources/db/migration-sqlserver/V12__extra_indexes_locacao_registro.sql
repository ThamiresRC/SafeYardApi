IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_cliente' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_cliente ON dbo.LOCACAO(cliente_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_moto' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_moto ON dbo.LOCACAO(moto_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_dataSaida' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_dataSaida ON dbo.LOCACAO(data_saida);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_dataDevolucao' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_dataDevolucao ON dbo.LOCACAO(data_devolucao);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_REGISTRO_moto' AND object_id=OBJECT_ID('dbo.REGISTRO_MOTO_PATIO'))
CREATE INDEX IX_REGISTRO_moto ON dbo.REGISTRO_MOTO_PATIO(moto_id);

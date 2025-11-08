IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_moto' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_moto ON dbo.LOCACAO(moto_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_cliente' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_cliente ON dbo.LOCACAO(cliente_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_saida' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_saida ON dbo.LOCACAO(data_saida);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_LOCACAO_devolucao' AND object_id=OBJECT_ID('dbo.LOCACAO'))
CREATE INDEX IX_LOCACAO_devolucao ON dbo.LOCACAO(data_devolucao);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.CLIENTE WHERE EMAIL = 'cliente@safeyard.com')
INSERT INTO dbo.CLIENTE (CPF, EMAIL, NOME)
VALUES ('33333333333', 'cliente@safeyard.com', 'Cliente Demo');
GO

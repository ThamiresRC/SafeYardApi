-- senha bcrypt: 123456
IF NOT EXISTS (SELECT 1 FROM dbo.USERS WHERE EMAIL = 'admin@safeyard.com')
INSERT INTO dbo.USERS (ATIVO, EMAIL, NOME, ROLE, SENHA)
VALUES (1, 'admin@safeyard.com', 'Administrador SafeYard', 'ADMIN',
        '$2a$10$uQkXULE42q9BKBFszPN5veYZ2CtXp/eSqZPhJpGJE5CBHAJeX8aUi');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.USERS WHERE EMAIL = 'func@safeyard.com')
INSERT INTO dbo.USERS (ATIVO, EMAIL, NOME, ROLE, SENHA)
VALUES (1, 'func@safeyard.com', 'Funcionário SafeYard', 'FUNCIONARIO',
        '$2a$10$uQkXULE42q9BKBFszPN5veYZ2CtXp/eSqZPhJpGJE5CBHAJeX8aUi');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.USERS WHERE EMAIL = 'cliente@safeyard.com')
INSERT INTO dbo.USERS (ATIVO, EMAIL, NOME, ROLE, SENHA)
VALUES (1, 'cliente@safeyard.com', 'Cliente SafeYard', 'CLIENTE',
        '$2a$10$uQkXULE42q9BKBFszPN5veYZ2CtXp/eSqZPhJpGJE5CBHAJeX8aUi');
GO

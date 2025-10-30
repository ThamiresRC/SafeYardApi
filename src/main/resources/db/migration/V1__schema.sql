-- CLIENTE
IF OBJECT_ID('dbo.CLIENTE', 'U') IS NULL
BEGIN
CREATE TABLE dbo.CLIENTE (
                             ID        BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                             CPF       VARCHAR(11)  NOT NULL,
                             EMAIL     VARCHAR(100) NOT NULL,
                             NOME      VARCHAR(100) NOT NULL
);

ALTER TABLE dbo.CLIENTE
    ADD CONSTRAINT UK_CLIENTE_CPF UNIQUE (CPF);

CREATE UNIQUE INDEX UX_CLIENTE_EMAIL ON dbo.CLIENTE(EMAIL);
END
GO

-- MOTO
IF OBJECT_ID('dbo.MOTO', 'U') IS NULL
BEGIN
CREATE TABLE dbo.MOTO (
                          ID          BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                          CHASSI      VARCHAR(17)  NOT NULL,
                          IMAGEM_PATH VARCHAR(255) NULL,
                          MODELO      VARCHAR(30)  NOT NULL,
                          PLACA       VARCHAR(7)   NOT NULL,
                          STATUS      VARCHAR(30)  NOT NULL
);

ALTER TABLE dbo.MOTO
    ADD CONSTRAINT UK_MOTO_PLACA UNIQUE (PLACA);
END
GO

-- REGISTRO_MOTO_PATIO
IF OBJECT_ID('dbo.REGISTRO_MOTO_PATIO', 'U') IS NULL
BEGIN
CREATE TABLE dbo.REGISTRO_MOTO_PATIO (
                                         ID                 BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                                         DATA_HORA_REGISTRO DATETIME2(3) NOT NULL,
                                         SETOR              VARCHAR(30)  NOT NULL,
                                         TIPO               VARCHAR(10)  NOT NULL,
                                         VAGA               VARCHAR(3)   NOT NULL,
                                         MOTO_ID            BIGINT NULL
);
END
GO

-- LOCACAO
IF OBJECT_ID('dbo.LOCACAO', 'U') IS NULL
BEGIN
CREATE TABLE dbo.LOCACAO (
                             ID                 BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                             CONDICAO_DEVOLUCAO VARCHAR(255) NULL,
                             CONDICAO_ENTREGA   VARCHAR(255) NULL,
                             DATA_DEVOLUCAO     DATETIME2(3) NULL,
                             DATA_SAIDA         DATETIME2(3) NOT NULL,
                             QR_CODE            VARCHAR(200) NULL,
                             CLIENTE_ID         BIGINT NULL,
                             MOTO_ID            BIGINT NULL
);
END
GO

-- USERS
IF OBJECT_ID('dbo.USERS', 'U') IS NULL
BEGIN
CREATE TABLE dbo.USERS (
                           ID    BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                           ATIVO BIT         NOT NULL,
                           EMAIL VARCHAR(180) NOT NULL,
                           NOME  VARCHAR(120) NOT NULL,
                           ROLE  VARCHAR(30)  NOT NULL,
                           SENHA VARCHAR(255) NOT NULL
);

ALTER TABLE dbo.USERS
    ADD CONSTRAINT UK_USERS_EMAIL UNIQUE (EMAIL);
END
GO

-- TOKEN
IF OBJECT_ID('dbo.TOKEN', 'U') IS NULL
BEGIN
CREATE TABLE dbo.TOKEN (
                           ID       BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                           ATIVO    BIT         NULL,
                           EXPIRADO BIT         NULL,
                           TOKEN    VARCHAR(255) NULL,
                           USER_ID  BIGINT      NULL
);
END
GO

-- FKs
ALTER TABLE dbo.REGISTRO_MOTO_PATIO
    WITH CHECK ADD CONSTRAINT FK_REGISTRO_MOTO
    FOREIGN KEY (MOTO_ID) REFERENCES dbo.MOTO(ID);
GO

ALTER TABLE dbo.LOCACAO
    WITH CHECK ADD CONSTRAINT FK_LOCACAO_MOTO
    FOREIGN KEY (MOTO_ID) REFERENCES dbo.MOTO(ID);
GO

ALTER TABLE dbo.LOCACAO
    WITH CHECK ADD CONSTRAINT FK_LOCACAO_CLIENTE
    FOREIGN KEY (CLIENTE_ID) REFERENCES dbo.CLIENTE(ID);
GO

ALTER TABLE dbo.TOKEN
    WITH CHECK ADD CONSTRAINT FK_TOKEN_USER
    FOREIGN KEY (USER_ID) REFERENCES dbo.USERS(ID);
GO

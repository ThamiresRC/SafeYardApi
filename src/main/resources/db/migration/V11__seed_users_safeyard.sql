------------------------------------------------------------
-- V11 - Seed de usuários padrão para o modelo ATUAL
-- Tabela alvo: USERS (a mesma da entidade User)
-- Senha: 123456 (BCrypt)
-- Roles válidas: ADMIN, FUNCIONARIO, CLIENTE
------------------------------------------------------------

-- 1) Garante que a tabela USERS exista
-- (se o V1 já criou, o H2 ignora)
CREATE TABLE IF NOT EXISTS USERS (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     nome   VARCHAR(120) NOT NULL,
    email  VARCHAR(180) NOT NULL UNIQUE,
    senha  VARCHAR(120) NOT NULL,
    role   VARCHAR(20)  NOT NULL,
    ativo  BOOLEAN      NOT NULL DEFAULT TRUE
    );

------------------------------------------------------------
-- 2) Insere usuários padrão (idempotente)
-- hash de 123456 gerado com BCrypt (strength 10)
-- $2a$10$TgR.fj6EqC13pXm7c1OHduAk.PGwdEro4uC6OsPiD8tKiILBoTI6.
------------------------------------------------------------

-- ADMIN
MERGE INTO USERS (nome, email, senha, role, ativo)
    KEY (email)
    VALUES (
    'Admin SafeYard',
    'admin@safeyard.com',
    '$2a$10$TgR.fj6EqC13pXm7c1OHduAk.PGwdEro4uC6OsPiD8tKiILBoTI6.', -- 123456
    'ADMIN',
    TRUE
    );

-- FUNCIONARIO
MERGE INTO USERS (nome, email, senha, role, ativo)
    KEY (email)
    VALUES (
    'Funcionário SafeYard',
    'func@safeyard.com',
    '$2a$10$TgR.fj6EqC13pXm7c1OHduAk.PGwdEro4uC6OsPiD8tKiILBoTI6.', -- 123456
    'FUNCIONARIO',
    TRUE
    );

-- CLIENTE
MERGE INTO USERS (nome, email, senha, role, ativo)
    KEY (email)
    VALUES (
    'Cliente SafeYard',
    'cliente@safeyard.com',
    '$2a$10$TgR.fj6EqC13pXm7c1OHduAk.PGwdEro4uC6OsPiD8tKiILBoTI6.', -- 123456
    'CLIENTE',
    TRUE
    );

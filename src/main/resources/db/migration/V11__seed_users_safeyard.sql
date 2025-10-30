MERGE INTO USERS (NOME, EMAIL, SENHA, ROLE, ATIVO)
    KEY (EMAIL)
    VALUES (
    'Administrador SafeYard',
    'admin@safeyard.com',
    '$2a$10$uQkXULE42q9BKBFszPN5veYZ2CtXp/eSqZPhJpGJE5CBHAJeX8aUi',
    'ADMIN',
    TRUE
    );

MERGE INTO USERS (NOME, EMAIL, SENHA, ROLE, ATIVO)
    KEY (EMAIL)
    VALUES (
    'Funcionário SafeYard',
    'func@safeyard.com',
    '$2a$10$uQkXULE42q9BKBFszPN5veYZ2CtXp/eSqZPhJpGJE5CBHAJeX8aUi',
    'FUNCIONARIO',
    TRUE
    );

MERGE INTO USERS (NOME, EMAIL, SENHA, ROLE, ATIVO)
    KEY (EMAIL)
    VALUES (
    'Cliente SafeYard',
    'cliente@safeyard.com',
    '$2a$10$uQkXULE42q9BKBFszPN5veYZ2CtXp/eSqZPhJpGJE5CBHAJeX8aUi',
    'CLIENTE',
    TRUE
    );

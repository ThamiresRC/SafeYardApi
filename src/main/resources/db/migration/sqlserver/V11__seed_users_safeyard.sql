INSERT INTO users (ativo, email, nome, role, senha)
SELECT TRUE, 'admin@safeyard.com', 'Administrador SafeYard', 'ADMIN',
       '$2a$10$uQkXULE4zq9BKBFSzPN5veY2ZCtkXp/eSqZPhJpGjE5CBHAJeX8aUi'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='admin@safeyard.com');

INSERT INTO users (ativo, email, nome, role, senha)
SELECT TRUE, 'func@safeyard.com', 'Funcionário SafeYard', 'FUNCIONARIO',
       '$2a$10$uQkXULE4zq9BKBFSzPN5veY2ZCtkXp/eSqZPhJpGjE5CBHAJeX8aUi'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='func@safeyard.com');

INSERT INTO users (ativo, email, nome, role, senha)
SELECT TRUE, 'cliente@safeyard.com', 'Cliente SafeYard', 'CLIENTE',
       '$2a$10$uQkXULE4zq9BKBFSzPN5veY2ZCtkXp/eSqZPhJpGjE5CBHAJeX8aUi'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='cliente@safeyard.com');

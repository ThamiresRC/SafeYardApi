MERGE INTO cliente (id, cpf, email, nome)
    KEY(email)
    VALUES (DEFAULT, '33333333333', 'cliente@safeyard.com', 'Cliente Demo');
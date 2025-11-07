INSERT INTO cliente (cpf, email, nome)
SELECT '33333333333', 'cliente@safeyard.com', 'Cliente Demo'
    WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE email='cliente@safeyard.com');

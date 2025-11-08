-- CLIENTES
INSERT INTO cliente (cpf, email, nome)
SELECT '11122233344','ana@safeyard.com','Ana Paula'
    WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf='11122233344' OR email='ana@safeyard.com');

INSERT INTO cliente (cpf, email, nome)
SELECT '22233344455','bruno@safeyard.com','Bruno Lima'
    WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf='22233344455' OR email='bruno@safeyard.com');

-- MOTOS (usar foto_url)
INSERT INTO moto (chassi, foto_url, modelo, placa, status)
SELECT '9BWZZZ377VT004251', NULL, 'CG 160', 'ABC1D23', 'DISPONIVEL'
    WHERE NOT EXISTS (SELECT 1 FROM moto WHERE placa='ABC1D23');

INSERT INTO moto (chassi, foto_url, modelo, placa, status)
SELECT '9BG116GW04C400001', NULL, 'Fazer 250', 'EFG4H56', 'DISPONIVEL'
    WHERE NOT EXISTS (SELECT 1 FROM moto WHERE placa='EFG4H56');

INSERT INTO moto (chassi, foto_url, modelo, placa, status)
SELECT '93HSDM4003P002345', NULL, 'Biz 125', 'IJK7L89', 'MANUTENCAO'
    WHERE NOT EXISTS (SELECT 1 FROM moto WHERE placa='IJK7L89');

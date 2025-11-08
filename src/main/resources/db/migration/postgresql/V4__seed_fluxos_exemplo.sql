INSERT INTO locacao (condicao_entrega, data_saida, qr_code, cliente_id, moto_id)
SELECT 'OK', NOW(), 'QR-LOC-0001', 1, 1
    WHERE NOT EXISTS (SELECT 1 FROM locacao WHERE qr_code='QR-LOC-0001');

INSERT INTO registro_moto_patio (data_hora_registro, setor, tipo, vaga, moto_id)
VALUES (NOW(), 'A', 'ENTRADA', 'A1', 1);

INSERT INTO registro_moto_patio (data_hora_registro, setor, tipo, vaga, moto_id)
VALUES (NOW(), 'A', 'SAIDA', 'A1', 1);

INSERT INTO registro_moto_patio (data_hora_registro, setor, tipo, vaga, moto_id)
VALUES (NOW(), 'B', 'ENTRADA', 'B2', 2);

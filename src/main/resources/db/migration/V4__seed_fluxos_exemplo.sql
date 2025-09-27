INSERT INTO LOCACAO (CONDICAO_ENTREGA, DATA_SAIDA, QR_CODE, CLIENTE_ID, MOTO_ID)
VALUES ('OK', CURRENT_TIMESTAMP, 'QR-LOC-0001', 1, 1);

INSERT INTO REGISTRO_MOTO_PATIO (DATA_HORA_REGISTRO, SETOR, TIPO, VAGA, MOTO_ID) VALUES
                                                                                     (CURRENT_TIMESTAMP, 'A', 'ENTRADA', 'A1', 1),
                                                                                     (CURRENT_TIMESTAMP, 'A', 'SAIDA',   'A1', 1),
                                                                                     (CURRENT_TIMESTAMP, 'B', 'ENTRADA', 'B2', 2);

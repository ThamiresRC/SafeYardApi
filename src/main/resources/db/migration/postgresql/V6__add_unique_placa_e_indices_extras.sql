-- remover duplicados por placa (mantém o menor id)
WITH d AS (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY placa ORDER BY id) rn
    FROM moto
)
DELETE FROM moto USING d WHERE moto.id = d.id AND d.rn > 1;

-- remover duplicados por chassi
WITH d AS (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY chassi ORDER BY id) rn
    FROM moto
)
DELETE FROM moto USING d WHERE moto.id = d.id AND d.rn > 1;

-- índices / uniques
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ux_moto_placa') THEN
    EXECUTE 'CREATE UNIQUE INDEX ux_moto_placa ON moto(placa)';
END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ux_moto_chassi') THEN
    EXECUTE 'CREATE UNIQUE INDEX ux_moto_chassi ON moto(chassi)';
END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_moto_status') THEN
    EXECUTE 'CREATE INDEX idx_moto_status ON moto(status)';
END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='idx_moto_modelo') THEN
    EXECUTE 'CREATE INDEX idx_moto_modelo ON moto(modelo)';
END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name='moto' AND column_name='foto_url'
  ) THEN
    EXECUTE 'ALTER TABLE moto ADD COLUMN foto_url VARCHAR(255)';
END IF;
END $$;

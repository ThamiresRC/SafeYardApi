-- V12__create_integration_events.sql
DO $$
BEGIN
    -- se a tabela ainda não existe (porque o V10 era de SQL Server e falhou)
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'integration_events'
    ) THEN
CREATE TABLE integration_events (
                                    id          BIGSERIAL PRIMARY KEY,
                                    source      varchar(50)   NOT NULL,
                                    type        varchar(80)   NOT NULL,
                                    event_ts    timestamp(3),
                                    created_at  timestamp(3)  DEFAULT (now()),
                                    data        text
);

-- índices parecidos com os do SQL Server
CREATE INDEX ix_integration_events_created_at ON integration_events (created_at);
CREATE INDEX ix_integration_events_type       ON integration_events (type);
ELSE
        -- se já existir mas com tipo errado, arruma
ALTER TABLE integration_events
ALTER COLUMN data TYPE text;
END IF;
END $$;

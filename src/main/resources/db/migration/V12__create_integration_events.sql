DO $$
BEGIN
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

CREATE INDEX ix_integration_events_created_at ON integration_events (created_at);
CREATE INDEX ix_integration_events_type       ON integration_events (type);
ELSE

ALTER TABLE integration_events
ALTER COLUMN data TYPE text;
END IF;
END $$;

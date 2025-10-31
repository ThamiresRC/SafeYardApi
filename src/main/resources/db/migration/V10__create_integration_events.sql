DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables
                   WHERE table_name = 'integration_events'
                     AND table_schema = 'public') THEN
CREATE TABLE public.integration_events (
                                           id         BIGSERIAL PRIMARY KEY,
                                           source     VARCHAR(50)   NOT NULL,
                                           type       VARCHAR(80)   NOT NULL,
                                           event_ts   TIMESTAMP(3),
                                           created_at TIMESTAMP(3)  NOT NULL DEFAULT (NOW()),
                                           data       TEXT
);

CREATE INDEX ix_integration_events_created_at ON public.integration_events (created_at);
CREATE INDEX ix_integration_events_type       ON public.integration_events (type);
END IF;
END $$;

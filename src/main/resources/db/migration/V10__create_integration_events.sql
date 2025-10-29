CREATE TABLE integration_events (
                                    id BIGINT IDENTITY PRIMARY KEY,     -- em Azure SQL vira IDENTITY; no H2 (modo MSSQL) também funciona
                                    source     VARCHAR(50)   NOT NULL,
                                    type       VARCHAR(80)   NOT NULL,
                                    event_ts   DATETIME2     NULL,      -- timestamp do evento (UTC)
                                    created_at DATETIME2     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    data       VARCHAR(4000) NULL
);

CREATE INDEX ix_integration_events_created_at ON integration_events(created_at);
CREATE INDEX ix_integration_events_type       ON integration_events(type);

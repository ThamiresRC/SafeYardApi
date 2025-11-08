IF NOT EXISTS (
    SELECT 1
    FROM sys.tables
    WHERE name = 'integration_events'
)
BEGIN
CREATE TABLE integration_events (
                                    id          BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                                    source      NVARCHAR(50)   NOT NULL,
                                    type        NVARCHAR(80)   NOT NULL,
                                    event_ts    DATETIME2(3)   NULL,
                                    created_at  DATETIME2(3)   NOT NULL CONSTRAINT DF_integration_events_created_at DEFAULT (SYSUTCDATETIME()),
                                    data        NVARCHAR(MAX)  NULL
);

CREATE INDEX ix_integration_events_created_at
    ON integration_events (created_at);

CREATE INDEX ix_integration_events_type
    ON integration_events (type);
END
ELSE
BEGIN
ALTER TABLE integration_events
ALTER COLUMN data NVARCHAR(MAX) NULL;
END

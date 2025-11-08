IF OBJECT_ID('dbo.integration_events', 'U') IS NULL
BEGIN
CREATE TABLE dbo.integration_events (
                                        id         BIGINT IDENTITY(1,1) PRIMARY KEY,
                                        source     VARCHAR(50)  NOT NULL,
                                        type       VARCHAR(80)  NOT NULL,
                                        event_ts   DATETIME2(3) NULL,
                                        created_at DATETIME2(3) NOT NULL CONSTRAINT DF_integration_events_created_at DEFAULT (SYSUTCDATETIME()),
                                        data       NVARCHAR(MAX) NULL
);
CREATE INDEX ix_integration_events_created_at ON dbo.integration_events (created_at);
CREATE INDEX ix_integration_events_type       ON dbo.integration_events (type);
END
GO

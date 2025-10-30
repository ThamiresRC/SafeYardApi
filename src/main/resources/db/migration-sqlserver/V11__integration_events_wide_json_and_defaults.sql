IF NOT EXISTS (
  SELECT 1 FROM sys.objects
  WHERE object_id = OBJECT_ID(N'[dbo].[integration_events]') AND type = 'U'
)
BEGIN
CREATE TABLE [dbo].[integration_events] (
    [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [source]     VARCHAR(50)   NOT NULL,
    [type]       VARCHAR(80)   NOT NULL,
    [event_ts]   DATETIME2(3)  NULL,
    [created_at] DATETIME2(3)  NOT NULL CONSTRAINT DF_integration_events_created_at DEFAULT (SYSUTCDATETIME()),
    [data]       NVARCHAR(MAX) NULL
    );
END
ELSE
BEGIN

    IF EXISTS (SELECT 1 FROM sys.columns
               WHERE Name = N'data'
                 AND Object_ID = Object_ID(N'[dbo].[integration_events]')
                 AND (system_type_id <> 231 OR max_length <> -1)) -- 231=NVARCHAR, -1=MAX
BEGIN
ALTER TABLE [dbo].[integration_events] ALTER COLUMN [data] NVARCHAR(MAX) NULL;
END


    IF EXISTS (SELECT 1 FROM sys.columns
               WHERE Name = N'event_ts'
                 AND Object_ID = Object_ID(N'[dbo].[integration_events]'))
BEGIN
ALTER TABLE [dbo].[integration_events] ALTER COLUMN [event_ts] DATETIME2(3) NULL;
END


    IF NOT EXISTS (
        SELECT 1
        FROM sys.default_constraints dc
        JOIN sys.columns c ON c.default_object_id = dc.object_id
        WHERE dc.parent_object_id = OBJECT_ID(N'[dbo].[integration_events]')
          AND c.name = 'created_at'
    )
BEGIN
ALTER TABLE [dbo].[integration_events]
    ADD CONSTRAINT DF_integration_events_created_at
    DEFAULT (SYSUTCDATETIME()) FOR [created_at];
END
END


IF NOT EXISTS (
  SELECT 1 FROM sys.indexes
  WHERE name = 'IX_integration_events_type_created_at'
    AND object_id = OBJECT_ID(N'[dbo].[integration_events]')
)
BEGIN
CREATE INDEX IX_integration_events_type_created_at
    ON [dbo].[integration_events] ([type], [created_at]);
END

IF NOT EXISTS (
  SELECT 1 FROM sys.indexes
  WHERE name = 'IX_integration_events_created_at'
    AND object_id = OBJECT_ID(N'[dbo].[integration_events]')
)
BEGIN
CREATE INDEX IX_integration_events_created_at
    ON [dbo].[integration_events] ([created_at]);
END


PRINT '✅ integration_events atualizada com NVARCHAR(MAX) e índices em type, created_at';

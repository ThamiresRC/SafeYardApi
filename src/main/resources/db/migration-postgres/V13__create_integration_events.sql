CREATE TABLE IF NOT EXISTS integration_events (
                                                  id          BIGSERIAL PRIMARY KEY,
                                                  created_at  timestamp,
                                                  event_ts    timestamp,
                                                  source      varchar(50) NOT NULL,
    type        varchar(80) NOT NULL,
    data        text
    );

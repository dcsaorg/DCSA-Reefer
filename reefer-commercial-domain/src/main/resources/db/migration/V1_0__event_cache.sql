CREATE TABLE event_cache_queue (
    event_id uuid NOT NULL PRIMARY KEY,
    event_type varchar(32) NOT NULL
);

CREATE TABLE event_cache_queue_dead (
    event_id uuid NOT NULL PRIMARY KEY,
    event_type varchar(32) NOT NULL,
    failure_reason_type varchar(200),
    failure_reason_message text
);

CREATE TABLE event_cache (
    event_id uuid NOT NULL PRIMARY KEY,
    event_type varchar(32) NOT NULL,
    content jsonb NOT NULL,
    event_created_date_time timestamp with time zone NOT NULL,
    event_date_time timestamp with time zone NOT NULL
);
CREATE INDEX ON event_cache (event_created_date_time);
CREATE INDEX ON event_cache (event_date_time);

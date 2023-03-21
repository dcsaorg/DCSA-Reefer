
CREATE TABLE unmatched_reefer_commercial_event (
    event_id varchar(100) NOT NULL PRIMARY KEY REFERENCES reefer_commercial_event (event_id),
    created_date_time timestamp with time zone NOT NULL default now()
);
CREATE INDEX ON unmatched_reefer_commercial_event (created_date_time);

CREATE TABLE outgoing_reefer_commercial_event (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    subscription_id uuid NOT NULL, -- REFERENCES reefer_commercial_event_subscription (id),
    next_delivery_attempt timestamp with time zone NOT NULL default now(),
    delivery_attempts int NOT NULL default 0
);
CREATE INDEX ON outgoing_reefer_commercial_event (next_delivery_attempt);
CREATE UNIQUE INDEX ON outgoing_reefer_commercial_event (event_id, subscription_id);

CREATE TABLE delivered_reefer_commercial_event (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    subscription_id uuid NOT NULL, -- REFERENCES reefer_commercial_event_subscription (id),
    callback_url text NOT NULL,
    last_delivery_attempt timestamp with time zone NOT NULL,
    delivery_attempts int NOT NULL
);

CREATE TABLE undeliverable_reefer_commercial_event (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    subscription_id uuid NOT NULL, -- REFERENCES reefer_commercial_event_subscription (id),
    callback_url text NOT NULL,
    last_delivery_attempt timestamp with time zone NOT NULL,
    delivery_attempts int NOT NULL,
    error_details text NOT NULL
);

CREATE FUNCTION queue_unmatched_reefer_commercial_event() RETURNS TRIGGER AS $$
    BEGIN
      INSERT INTO unmatched_reefer_commercial_event (event_id) VALUES (NEW.event_id);
      RETURN NULL;
    END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER queue_match_reefer_commercial_event AFTER INSERT ON reefer_commercial_event
       FOR EACH ROW EXECUTE PROCEDURE queue_unmatched_reefer_commercial_event();

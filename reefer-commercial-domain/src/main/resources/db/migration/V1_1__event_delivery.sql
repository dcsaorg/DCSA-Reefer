
CREATE TABLE outgoing_event_message (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    subscription_id uuid NOT NULL,
    next_delivery_attempt timestamp with time zone NOT NULL default now(),
    delivery_attempts int NOT NULL default 0
);
CREATE INDEX ON outgoing_event_message (next_delivery_attempt);
CREATE UNIQUE INDEX ON outgoing_event_message (event_id, subscription_id);

CREATE TABLE delivered_event_message (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    subscription_id uuid NOT NULL,
    callback_url text NOT NULL,
    delivery_time timestamp with time zone NOT NULL,
    delivery_attempts int NOT NULL
);

CREATE TABLE undeliverable_event_message (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    subscription_id uuid NOT NULL,
    callback_url text NULL,
    last_delivery_attempt timestamp with time zone NOT NULL,
    delivery_attempts int NOT NULL,
    error_details text NOT NULL
);

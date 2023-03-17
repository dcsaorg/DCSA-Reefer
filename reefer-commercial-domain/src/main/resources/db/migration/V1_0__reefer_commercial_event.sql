
CREATE TABLE reefer_commercial_event (
    event_id varchar(100) NOT NULL PRIMARY KEY,
    content jsonb NOT NULL,
    event_created_date_time timestamp with time zone NOT NULL,
    event_date_time timestamp with time zone NULL
);
CREATE INDEX ON reefer_commercial_event (event_created_date_time);
CREATE INDEX ON reefer_commercial_event (event_date_time);
CREATE INDEX reefer_event_type_code_idx ON reefer_commercial_event USING btree ((content->>'reeferEventTypeCode'));
CREATE INDEX equipment_reference_idx ON reefer_commercial_event USING btree ((content->>'equipmentReference'));

CREATE TABLE reefer_commercial_event_document_reference (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL REFERENCES reefer_commercial_event (event_id),
    type varchar(3) NOT NULL,
    reference varchar(100) NOT NULL
);
CREATE UNIQUE INDEX ON reefer_commercial_event_document_reference (event_id, type, reference);

CREATE TABLE reefer_commercial_event_subscription (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    callback_url text NOT NULL,
    equipment_reference varchar(11) NULL,
    carrier_booking_reference varchar(35) NULL,
    secret bytea NOT NULL,
    created_date_time timestamp with time zone NOT NULL default now(),
    updated_date_time timestamp with time zone NOT NULL default now()
);
CREATE INDEX ON reefer_commercial_event_subscription (created_date_time);
CREATE INDEX ON reefer_commercial_event_subscription (updated_date_time);

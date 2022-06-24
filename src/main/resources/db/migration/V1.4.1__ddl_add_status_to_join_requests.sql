ALTER TABLE join_request_notifications ADD status varchar NOT NULL DEFAULT 'PENDING';
COMMENT ON COLUMN join_request_notifications.status IS 'Status of the join request';

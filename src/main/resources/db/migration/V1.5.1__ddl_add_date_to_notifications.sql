ALTER TABLE notifications ADD created_date timestamp NOT NULL DEFAULT now();
COMMENT ON COLUMN notifications.created_date IS 'Timestamp of when the notification was created';

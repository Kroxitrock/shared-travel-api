ALTER TABLE travels ADD COLUMN status varchar(15) NOT NULL DEFAULT 'PENDING';

-- Column comments

COMMENT ON COLUMN travels.status IS 'Status of the travel';

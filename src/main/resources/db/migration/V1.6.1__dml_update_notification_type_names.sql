UPDATE notifications
	SET "type"='REQUEST_REJECTED'
	WHERE "type"='DECLINE';

UPDATE notifications
	SET "type"='REQUEST_APPROVED'
	WHERE "type"='ACCEPT';
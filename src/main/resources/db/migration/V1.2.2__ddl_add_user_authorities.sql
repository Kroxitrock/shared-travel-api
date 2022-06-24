CREATE TABLE user_authorities (
	user_id bigint NOT NULL,
	authority varchar(32) NOT NULL,
	CONSTRAINT user_authorities_pk PRIMARY KEY (user_id,authority),
	CONSTRAINT user_authorities_fk FOREIGN KEY (user_id) REFERENCES public.users(id)
);

COMMENT ON COLUMN user_authorities.user_id IS 'ID of the security user';
COMMENT ON COLUMN user_authorities.authority IS 'Name of the authority given to the user';

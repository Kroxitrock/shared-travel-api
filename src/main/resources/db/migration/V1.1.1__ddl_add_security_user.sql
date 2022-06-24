CREATE TABLE "users" (
	id bigserial NOT NULL,
	email varchar(255) NOT NULL,
	"password" varchar(128) NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_un UNIQUE (email)
);

-- Column comments

COMMENT ON COLUMN "users".email IS 'Email address of the user used as username as well';
COMMENT ON COLUMN "users"."password" IS 'Bcrypt hashed password of the user';


CREATE TABLE public.users (
	id bigserial NOT NULL,
	first_name varchar(25) NOT NULL,
	last_name varchar(25) NOT NULL,
	email varchar(40) NOT NULL,
	"password" varchar(50) NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (id)
);

-- Column comments

COMMENT ON COLUMN public.users.id IS 'id of the user';
COMMENT ON COLUMN public.users.first_name IS 'first name of the user';
COMMENT ON COLUMN public.users.last_name IS 'last name of the user';
COMMENT ON COLUMN public.users.email IS 'email of the user';
COMMENT ON COLUMN public.users."password" IS 'password of the user';

-- Many to many table users-travels

CREATE TABLE public.users_travels (
	user_id bigint NOT NULL,
	travel_id bigint NOT NULL,
	CONSTRAINT users_travels_pk PRIMARY KEY (travel_id,user_id),
	CONSTRAINT "_travel" FOREIGN KEY (travel_id) REFERENCES public.travels(id),
	CONSTRAINT "_user" FOREIGN KEY (user_id) REFERENCES public.users(id)
);

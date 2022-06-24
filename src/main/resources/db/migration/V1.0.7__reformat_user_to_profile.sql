ALTER TABLE users RENAME CONSTRAINT users_pk TO profiles_pk;
ALTER TABLE users RENAME TO profiles;

ALTER TABLE users_travels RENAME CONSTRAINT users_travels_pk TO profiles_travels_pk;
ALTER TABLE users_travels RENAME TO profiles_travels;
ALTER TABLE profiles_travels RENAME COLUMN user_id TO profile_id;

ALTER SEQUENCE users_id_seq RENAME TO profiles_id_seq;

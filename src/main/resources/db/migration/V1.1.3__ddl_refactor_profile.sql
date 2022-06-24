ALTER TABLE profiles DROP COLUMN email;
ALTER TABLE profiles DROP COLUMN "password";
ALTER TABLE profiles ADD user_id bigint;

UPDATE profiles
SET user_id = id;

ALTER TABLE profiles ALTER COLUMN user_id SET NOT NULL;
COMMENT ON COLUMN profiles.user_id IS 'Foreign key to the security user behind the profile';
ALTER TABLE profiles ADD CONSTRAINT profiles_fk FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE profiles ADD CONSTRAINT profiles_un UNIQUE (user_id);
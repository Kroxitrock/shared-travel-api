CREATE TABLE public.profile_settings
(
    id            bigserial NOT NULL,
    email_visible boolean   NOT NULL DEFAULT TRUE,
    CONSTRAINT profile_settings_pk PRIMARY KEY (id)
);

-- Column comments

COMMENT
ON COLUMN public.profile_settings.email_visible IS 'Should the profile email be publicly visible';

-- Add FK to profiles

ALTER TABLE public.profiles
    ADD profile_settings_id bigint;
ALTER TABLE public.profiles
    ADD CONSTRAINT profiles_fk_1 FOREIGN KEY (profile_settings_id) REFERENCES public.profile_settings (id);

-- Column comments

COMMENT
ON COLUMN public.profiles.profile_settings_id IS 'Foreign key to the setting of the profile';

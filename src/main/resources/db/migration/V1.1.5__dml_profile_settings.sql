INSERT INTO public.profile_settings DEFAULT
VALUES;
INSERT INTO public.profile_settings DEFAULT
VALUES;
INSERT INTO public.profile_settings DEFAULT
VALUES;

UPDATE public.profiles
SET profile_settings_id=1
WHERE id = 1;

UPDATE public.profiles
SET profile_settings_id=2
WHERE id = 2;

UPDATE public.profiles
SET profile_settings_id=3
WHERE id = 3;

ALTER TABLE public.notifications ADD "read" boolean NOT NULL DEFAULT false;
COMMENT ON COLUMN public.notifications."read" IS 'If the notification has been read';

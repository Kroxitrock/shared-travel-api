ALTER TABLE public.notifications ADD "processed" boolean NOT NULL DEFAULT false;
COMMENT ON COLUMN public.notifications."processed" IS 'If the notification has been processed';

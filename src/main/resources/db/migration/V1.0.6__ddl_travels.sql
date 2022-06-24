-- Add driver_id foreign key many to one connection to the driver user

ALTER TABLE public.travels ADD driver_id bigint;
ALTER TABLE public.travels ADD CONSTRAINT travels_fk_2 FOREIGN KEY (driver_id) REFERENCES public.users(id);

-- Column comments

COMMENT ON COLUMN travels.driver_id IS 'Driver id of the user assigned as driver to this travel';

-- Update existing travels to include driver id

UPDATE public.travels SET driver_id = 1;

-- Set driver_id column to NOT NULL

ALTER TABLE public.travels ALTER COLUMN driver_id SET NOT NULL;
CREATE TABLE public.notifications (
      id bigserial NOT NULL,
      notified_person_id bigint NOT NULL,
      message_data varchar NOT NULL,
      "type" varchar NOT NULL,
      CONSTRAINT notifications_pk PRIMARY KEY (id),
      CONSTRAINT notifications_fk FOREIGN KEY (notified_person_id) REFERENCES public.profiles(id)
);

-- Column comments

COMMENT ON COLUMN public.notifications.id IS 'id of the notification';
COMMENT ON COLUMN public.notifications.notified_person_id IS 'id of the person to notify';
COMMENT ON COLUMN public.notifications.message_data IS 'the notification''s message data';
COMMENT ON COLUMN public.notifications."type" IS 'the notification type';

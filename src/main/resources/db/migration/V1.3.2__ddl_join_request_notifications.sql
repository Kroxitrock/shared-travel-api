CREATE TABLE public.join_request_notifications (
    notification_id bigint NOT NULL,
    passenger_id bigint NOT NULL,
    travel_id bigint NOT NULL,
    CONSTRAINT join_request_notifications_fk FOREIGN KEY (notification_id) REFERENCES public.notifications(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT join_request_notifications_fk_1 FOREIGN KEY (passenger_id) REFERENCES public.profiles(id),
    CONSTRAINT join_request_notifications_fk_2 FOREIGN KEY (travel_id) REFERENCES public.travels(id)
);

-- Column comments

COMMENT ON COLUMN public.join_request_notifications.notification_id IS 'Id relating to the notification';
COMMENT ON COLUMN public.join_request_notifications.passenger_id IS 'Id relating to the passenger';
COMMENT ON COLUMN public.join_request_notifications.travel_id IS 'Id relating to the travel';

-- Notification Table Changes

ALTER TABLE public.notifications ALTER COLUMN notified_person_id TYPE bigint USING notified_person_id::bigint;

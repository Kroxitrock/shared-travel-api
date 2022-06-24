package eu.sharedtravel.app.components.notification.service;

import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.TravelService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCronService {

    private final TravelService travelService;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 1 0 * * *") // Every day at 00:01:00
    public void notifyTravelToday() {
        List<Travel> travels = travelService.getTravelsForToday();

        List<Notification> notifications = notificationService.createTravelTodayNotifications(travels);

        log.info("Successfully notified {} people for their travel being today!", notifications.size());
    }

}

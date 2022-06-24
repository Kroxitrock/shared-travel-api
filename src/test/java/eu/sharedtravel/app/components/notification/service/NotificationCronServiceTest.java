package eu.sharedtravel.app.components.notification.service;

import eu.sharedtravel.app.components.travel.TravelTestMocks;
import eu.sharedtravel.app.components.travel.service.TravelService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {NotificationCronService.class})
class NotificationCronServiceTest {

    @Autowired
    private NotificationCronService notificationCronService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private TravelService travelService;

    @Test
    void givenTravelForTodayShouldNotify() {
        var travels = Collections.singletonList(TravelTestMocks.mockTravel());

        Mockito.when(travelService.getTravelsForToday()).thenReturn(travels);

        notificationCronService.notifyTravelToday();

        Mockito.verify(notificationService).createTravelTodayNotifications(travels);
    }

}

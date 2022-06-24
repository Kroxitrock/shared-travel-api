package eu.sharedtravel.app.components.notification;

import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestStatus;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.travel.TravelTestMocks;
import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class NotificationTestMocks {

    public static Page<Notification> mockNotificationPage() {
        return new PageImpl<>(Collections.singletonList(mockNotification()));
    }

    public static JoinRequestNotification mockJoinNotification() {
        return JoinRequestNotification.builder()
            .id(NotificationTestConstants.ID)
            .notifiedPerson(ProfileTestMocks.mockDriverProfile())
            .passenger(ProfileTestMocks.mockUserProfile())
            .travel(TravelTestMocks.mockTravel())
            .type(NotificationTestConstants.TYPE)
            .status(JoinRequestStatus.PENDING)
            .build();
    }

    public static Notification mockNotification() {
        return Notification.builder()
            .id(NotificationTestConstants.ID)
            .notifiedPerson(ProfileTestMocks.mockDriverProfile())
            .type(NotificationTestConstants.TYPE)
            .messageData(NotificationTestConstants.MESSAGE_DATA)
            .build();
    }
}

package eu.sharedtravel.app.components.notification.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import eu.sharedtravel.app.components.notification.NotificationTestMocks;
import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.notification.model.NotificationType;
import eu.sharedtravel.app.components.notification.repository.NotificationRepository;
import eu.sharedtravel.app.components.notification.repository.predicate.NotificationPredicates;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.TravelTestMocks;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {NotificationService.class})
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationPredicates notificationPredicates;

    private Profile driver;

    private BooleanExpression mockExpression;

    private JoinRequestNotification joinRequest;

    @BeforeEach
    public void setUp() {
        mockExpression = Expressions.asBoolean(true);
        driver = ProfileTestMocks.mockDriverProfile();
        joinRequest = NotificationTestMocks.mockJoinNotification();
    }

    @Test
    void givenProfileShouldGetNotificationPageForThatProfile() {
        var mockNotificationPage = NotificationTestMocks.mockNotificationPage();
        var pageable = PageRequest.of(1, 20);

        Mockito.when(notificationPredicates.defaultSort()).thenReturn(Sort.by("id"));
        Mockito.when(notificationPredicates.forProfile(ProfileTestMocks.mockDriverProfile()))
            .thenReturn(mockExpression);
        Mockito.when(notificationRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
            .thenReturn(mockNotificationPage);

        var fetchedNotificationPage = notificationService.findNotifications(driver, pageable);

        Assertions.assertNotNull(fetchedNotificationPage);
        Assertions.assertFalse(fetchedNotificationPage.isEmpty());
        Assertions.assertFalse(fetchedNotificationPage.getContent().isEmpty());
    }

    @Test
    void givenProfileShouldGetNotificationCountForThatProfile() {
        Mockito.when(notificationPredicates.forProfileAndNotRead(ProfileTestMocks.mockDriverProfile()))
            .thenReturn(mockExpression);
        Mockito.when(notificationRepository.count(Mockito.any(BooleanExpression.class)))
            .thenReturn(1L);

        var fetchedNotificationCount = notificationService.notificationCount(driver);

        Assertions.assertNotNull(fetchedNotificationCount);
        Assertions.assertEquals(1L, fetchedNotificationCount);
    }

    @Test
    void givenJoinRequestShouldCreateJoinRequestStatusChangeNotification() {

        notificationService.createJoinRequestStatusChangeNotification(joinRequest, NotificationType.REQUEST_APPROVED);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        Mockito.verify(notificationRepository).save(notificationCaptor.capture());

        Notification notification = notificationCaptor.getValue();
        Assertions.assertEquals(notification.getNotifiedPerson(), joinRequest.getPassenger());
        Assertions.assertEquals(NotificationType.REQUEST_APPROVED, notification.getType());
    }

    @Test
    void givenTravelShouldCreateTravelCanceledNotifications() {
        var travel = TravelTestMocks.mockTravel();
        var notifiedPerson = travel.getPassengers().stream().findFirst().orElse(null);

        notificationService.createTravelCanceledNotifications(travel);

        @SuppressWarnings("unchecked") // There is no other type of list to captor
        ArgumentCaptor<List<Notification>> notificationListCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(notificationRepository).saveAll(notificationListCaptor.capture());

        var notifications = notificationListCaptor.getValue();
        Assertions.assertEquals(1, notifications.size());

        var notification = notifications.get(0);
        Assertions.assertEquals(NotificationType.TRAVEL_CANCELED, notification.getType());
        Assertions.assertEquals(notifiedPerson, notification.getNotifiedPerson());
    }

    @Test
    void givenTravelShouldCreateAppliedTravelCanceledNotifications() {
        var travel = TravelTestMocks.mockTravel();
        var applicant = ProfileTestMocks.mockUserProfile();

        notificationService.createAppliedTravelCanceledNotifications(travel, Collections.singletonList(applicant));

        @SuppressWarnings("unchecked") // There is no other type of list to captor
        ArgumentCaptor<List<Notification>> notificationListCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(notificationRepository).saveAll(notificationListCaptor.capture());

        var notifications = notificationListCaptor.getValue();
        Assertions.assertEquals(1, notifications.size());

        var notification = notifications.get(0);
        Assertions.assertEquals(NotificationType.APPLIED_TRAVEL_CANCELED, notification.getType());
        Assertions.assertEquals(applicant, notification.getNotifiedPerson());
    }

    @Test
    void givenTravelAndPassengerShouldCreatePassengerLeftNotification() {
        var travel = TravelTestMocks.mockTravel();
        var passenger = ProfileTestMocks.mockUserProfile();

        notificationService.createPassengerLeftNotification(travel, passenger);

        ArgumentCaptor<Notification> notificationListCaptor = ArgumentCaptor.forClass(Notification.class);

        Mockito.verify(notificationRepository).save(notificationListCaptor.capture());

        var notification = notificationListCaptor.getValue();

        Assertions.assertEquals(NotificationType.PASSENGER_LEFT, notification.getType());
        Assertions.assertEquals(travel.getDriver(), notification.getNotifiedPerson());
    }

    @Test
    void givenTravelAndPassengerShouldCreatePassengerKickedNotification() {
        var travel = TravelTestMocks.mockTravel();
        var passenger = ProfileTestMocks.mockUserProfile();

        notificationService.createPassengerKickedNotification(travel, passenger);

        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);

        Mockito.verify(notificationRepository).save(notificationArgumentCaptor.capture());

        var notification = notificationArgumentCaptor.getValue();

        Assertions.assertEquals(NotificationType.PASSENGER_KICKED, notification.getType());
        Assertions.assertEquals(passenger, notification.getNotifiedPerson());
    }

    @Test
    void givenListOfTravelsShouldCreateTravelTodayNotifications() {
        var travel = TravelTestMocks.mockTravel();
        var travels = Collections.singletonList(travel);

        notificationService.createTravelTodayNotifications(travels);

        @SuppressWarnings("unchecked") // We know the type of the list
        ArgumentCaptor<List<Notification>> notificationsArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(notificationRepository).saveAll(notificationsArgumentCaptor.capture());

        var response = notificationsArgumentCaptor.getValue();

        Assertions.assertTrue(response.stream()
            .anyMatch(notification -> notification.getType() == NotificationType.PASSENGER_TRAVEL_TODAY));
        Assertions.assertTrue(
            response.stream().anyMatch(notification -> notification.getType() == NotificationType.DRIVER_TRAVEL_TODAY));
    }
}

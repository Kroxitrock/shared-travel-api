package eu.sharedtravel.app.components.notification.service;

import eu.sharedtravel.app.common.page.PageableUtil;
import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.notification.model.NotificationType;
import eu.sharedtravel.app.components.notification.repository.NotificationRepository;
import eu.sharedtravel.app.components.notification.repository.predicate.NotificationPredicates;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.model.Travel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPredicates notificationPredicates;

    public Page<Notification> findNotifications(Profile profile, Pageable pageable) {
        pageable = PageableUtil.pageableWithDefaultSort(pageable, notificationPredicates.defaultSort());

        Page<Notification> notificationPage = notificationRepository
            .findAll(notificationPredicates.forProfile(profile), pageable);
        notificationPage.getContent().forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notificationPage);

        return notificationPage;
    }

    public Long notificationCount(Profile profile) {
        return notificationRepository.count(notificationPredicates.forProfileAndNotRead(profile));
    }

    @Transactional
    public void createJoinRequestStatusChangeNotification(JoinRequestNotification joinRequest,
        NotificationType notificationType) {
        Notification notification = buildSimpleTravelNotification(notificationType,
            joinRequest.getTravel(), joinRequest.getPassenger());

        notificationRepository.save(notification);
    }

    @Transactional
    public void createTravelCanceledNotifications(Travel travel) {
        createSimpleTravelNotificationsForStatus(NotificationType.TRAVEL_CANCELED, travel, travel.getPassengers());
    }

    @Transactional
    public void createAppliedTravelCanceledNotifications(Travel travel, List<Profile> applicants) {
        createSimpleTravelNotificationsForStatus(NotificationType.APPLIED_TRAVEL_CANCELED, travel, applicants);
    }

    @Transactional
    public void createPassengerLeftNotification(Travel travel, Profile passenger) {
        Notification notification = buildTwoPersonNotification(NotificationType.PASSENGER_LEFT, travel,
            travel.getDriver(), passenger);

        notificationRepository.save(notification);
    }

    @Transactional
    public void createPassengerKickedNotification(Travel travel, Profile passenger) {
        Notification notification = buildSimpleTravelNotification(NotificationType.PASSENGER_KICKED, travel, passenger);

        notificationRepository.save(notification);
    }

    @Transactional
    public List<Notification> createTravelTodayNotifications(List<Travel> travels) {
        List<Notification> notifications = new ArrayList<>();

        travels.forEach(travel -> {
            notifications.addAll(buildSimpleTravelNotificationList(NotificationType.PASSENGER_TRAVEL_TODAY, travel,
                travel.getPassengers()));

            notifications.add(buildSimpleTravelNotification(NotificationType.DRIVER_TRAVEL_TODAY, travel,
                travel.getDriver()));
        });

        return notificationRepository.saveAll(notifications);
    }

    private void createSimpleTravelNotificationsForStatus(NotificationType notificationType, Travel travel,
        Collection<Profile> notifiedPeople) {
        List<Notification> notifications = buildSimpleTravelNotificationList(notificationType,
            travel, notifiedPeople);

        notificationRepository.saveAll(notifications);
    }

    private List<Notification> buildSimpleTravelNotificationList(NotificationType notificationType, Travel travel,
        Collection<Profile> notifiedPeople) {

        List<Notification> notifications = new ArrayList<>();

        notifiedPeople.forEach(notifiedPerson -> {
            var notification = buildSimpleTravelNotification(notificationType, travel, notifiedPerson);
            notifications.add(notification);
        });

        return notifications;
    }

    private Notification buildSimpleTravelNotification(NotificationType notificationType,
        Travel travel, Profile notifiedPerson) {
        String messageData = buildSimpleMessageData(travel);

        return buildNotification(notificationType, notifiedPerson, messageData);
    }

    private Notification buildTwoPersonNotification(NotificationType notificationType, Travel travel,
        Profile notifiedPerson, Profile notifyingPerson) {

        String messageData = buildTwoPersonMessageData(travel, notifyingPerson);

        return buildNotification(notificationType, notifiedPerson, messageData);
    }


    private String buildSimpleMessageData(Travel travel) {
        return getSimpleMessageBuilder(travel)
            .build().toString();
    }

    private String buildTwoPersonMessageData(Travel travel, Profile notifyingPerson) {
        return getSimpleMessageBuilder(travel)
            .add("notifyingPerson", notifyingPerson.getFullName())
            .build().toString();
    }

    private JsonObjectBuilder getSimpleMessageBuilder(Travel travel) {
        return Json.createObjectBuilder()
            .add("from", travel.getFrom().getCode())
            .add("to", travel.getTo().getCode())
            .add("travelDate", travel.getDepartureDate().toString());
    }

    private Notification buildNotification(NotificationType notificationType, Profile notifiedPerson,
        String messageData) {
        return Notification.builder()
            .type(notificationType)
            .notifiedPerson(notifiedPerson)
            .messageData(messageData)
            .build();
    }
}

package eu.sharedtravel.app.components.notification.request.join.service;

import eu.sharedtravel.app.components.notification.model.NotificationType;
import eu.sharedtravel.app.components.notification.request.join.exceptions.DriverHasNoAccessToJoinRequestException;
import eu.sharedtravel.app.components.notification.request.join.exceptions.PassengerAlreadyAppliedForTravelException;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestStatus;
import eu.sharedtravel.app.components.notification.request.join.repository.JoinRequestNotificationRepository;
import eu.sharedtravel.app.components.notification.request.join.repository.predicate.JoinRequestNotificationPredicates;
import eu.sharedtravel.app.components.notification.service.NotificationService;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.TravelService;
import eu.sharedtravel.app.components.travel.service.dto.TravelOutputDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelWithIsAppliedOutputDto;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinRequestNotificationService {

    private final TravelService travelService;
    private final NotificationService notificationService;

    private final JoinRequestNotificationRepository joinRequestNotificationRepository;
    private final JoinRequestNotificationPredicates joinRequestNotificationPredicates;

    public JoinRequestNotification findJoinRequestNotification(Long id) {
        return joinRequestNotificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Join Request Notification with id %d not found!", id)));
    }

    public JoinRequestNotification getActiveJoinRequestNotification(Long id) {
        return joinRequestNotificationRepository.findOne(joinRequestNotificationPredicates.forActiveForId(id))
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Active Join Request Notification with id %d not found!", id)));
    }

    public List<JoinRequestNotification> getPendingJoinRequestNotifications(Travel travel) {
        return (List<JoinRequestNotification>) joinRequestNotificationRepository.findAll(
            joinRequestNotificationPredicates.forActiveForTravel(travel));
    }

    public List<JoinRequestNotification> getPendingJoinRequestNotificationsForTravelAndPassenger(Long travelId,
        Long passengerId) {
        return (List<JoinRequestNotification>) joinRequestNotificationRepository.findAll(
            joinRequestNotificationPredicates.forPendingAndTravelIdAndPassengerId(travelId, passengerId));
    }

    @Transactional
    public void createJoinTravelNotification(Travel travel, Profile passenger) {
        validatePassengerHasNotAppliedForTravel(passenger, travel);

        JoinRequestNotification joinRequest = buildJoinRequest(travel, passenger);

        joinRequestNotificationRepository.save(joinRequest);
    }

    private JoinRequestNotification buildJoinRequest(Travel travel, Profile passenger) {

        String messageData = Json.createObjectBuilder()
            .add("passengerName", String.format("%s %s", passenger.getFirstName(), passenger.getLastName()))
            .add("travelDate", travel.getDepartureDate().toString())
            .build().toString();

        return JoinRequestNotification.builder()
            .type(NotificationType.JOIN)
            .notifiedPerson(travel.getDriver())
            .messageData(messageData)
            .passenger(passenger)
            .travel(travel)
            .status(JoinRequestStatus.PENDING)
            .build();
    }

    private void validatePassengerHasNotAppliedForTravel(Profile passenger, Travel travel) {
        if (joinRequestNotificationRepository.exists(
            joinRequestNotificationPredicates.forPassengerOrDriverOfTravel(passenger, travel))) {
            throw new PassengerAlreadyAppliedForTravelException(passenger.getId(), travel.getId());
        }
    }

    public void updateTravelsWithApplicationData(String email, List<TravelWithIsAppliedOutputDto> travels) {
        List<Long> travelIds = travels.stream().map(TravelOutputDto::getId).collect(Collectors.toList());

        List<JoinRequestNotification> joinRequestNotifications = (List<JoinRequestNotification>) joinRequestNotificationRepository
            .findAll(joinRequestNotificationPredicates.forUserAndInTravelList(email, travelIds));

        mapApplicationsToTravels(joinRequestNotifications, travels);
    }

    private void mapApplicationsToTravels(List<JoinRequestNotification> joinRequestNotifications,
        List<TravelWithIsAppliedOutputDto> travels) {
        travels.forEach(travel ->
            joinRequestNotifications.forEach(joinRequestNotification -> {
                if (travel.getId().equals(joinRequestNotification.getTravel().getId())) {
                    travel.setApplied(true);
                }
            })
        );
    }

    @Transactional
    public void accept(JoinRequestNotification joinRequest) {
        updateJoinRequestStatus(joinRequest, JoinRequestStatus.APPROVED);

        notificationService.createJoinRequestStatusChangeNotification(joinRequest, NotificationType.REQUEST_APPROVED);

        travelService.addPassengerToTravel(joinRequest.getPassenger(), joinRequest.getTravel());
    }

    @Transactional
    public void reject(JoinRequestNotification joinRequest) {
        updateJoinRequestStatus(joinRequest, JoinRequestStatus.REJECTED);

        notificationService.createJoinRequestStatusChangeNotification(joinRequest, NotificationType.REQUEST_REJECTED);
    }

    @Transactional
    public void cancel(List<JoinRequestNotification> joinRequests) {
        joinRequests.forEach(joinRequest -> joinRequest.setStatus(JoinRequestStatus.CANCELED));
        joinRequestNotificationRepository.saveAll(joinRequests);
    }

    private void updateJoinRequestStatus(JoinRequestNotification joinRequest, JoinRequestStatus joinRequestStatus) {
        joinRequest.setStatus(joinRequestStatus);
        joinRequest.setProcessed(true);
        joinRequestNotificationRepository.save(joinRequest);
    }

    public void validateUserIsDriverOfRequest(UserDto driver, JoinRequestNotification joinRequest) {
        if (!joinRequest.getNotifiedPerson().getUser().getId().equals(driver.getId())) {
            throw new DriverHasNoAccessToJoinRequestException(driver.getId(), joinRequest.getId());
        }
    }
}

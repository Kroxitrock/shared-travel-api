package eu.sharedtravel.app.components.notification.request.join.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import eu.sharedtravel.app.components.notification.NotificationTestConstants;
import eu.sharedtravel.app.components.notification.NotificationTestMocks;
import eu.sharedtravel.app.components.notification.model.NotificationType;
import eu.sharedtravel.app.components.notification.request.join.exceptions.DriverHasNoAccessToJoinRequestException;
import eu.sharedtravel.app.components.notification.request.join.exceptions.PassengerAlreadyAppliedForTravelException;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestStatus;
import eu.sharedtravel.app.components.notification.request.join.repository.JoinRequestNotificationRepository;
import eu.sharedtravel.app.components.notification.request.join.repository.predicate.JoinRequestNotificationPredicates;
import eu.sharedtravel.app.components.notification.service.NotificationService;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.TravelTestMocks;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.TravelService;
import eu.sharedtravel.app.components.travel.service.dto.TravelWithIsAppliedOutputDto;
import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.UserTestMocks;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JoinRequestNotificationService.class})
class JoinRequestNotificationServiceTest {

    @Autowired
    private JoinRequestNotificationService joinRequestNotificationService;

    @MockBean
    private JoinRequestNotificationRepository joinRequestNotificationRepository;

    @MockBean
    private JoinRequestNotificationPredicates joinRequestNotificationPredicates;

    @MockBean
    private TravelService travelService;

    @MockBean
    private NotificationService notificationService;

    private Profile driver;
    private Profile passenger;
    private Travel travel;
    private JoinRequestNotification joinRequest;

    private BooleanExpression mockExpression;

    @BeforeEach
    public void setUp() {
        mockExpression = Expressions.asBoolean(true);
        driver = ProfileTestMocks.mockDriverProfile();
        passenger = ProfileTestMocks.mockUserProfile();
        travel = TravelTestMocks.mockTravel();
        joinRequest = NotificationTestMocks.mockJoinNotification();
    }

    @Test
    void givenTravelAndPassengerShouldCreateJoinNotification() {
        Mockito.when(joinRequestNotificationPredicates.forPassengerOrDriverOfTravel(passenger, travel))
            .thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.exists(mockExpression)).thenReturn(false);

        joinRequestNotificationService.createJoinTravelNotification(travel, passenger);

        ArgumentCaptor<JoinRequestNotification> argument = ArgumentCaptor.forClass(JoinRequestNotification.class);
        Mockito.verify(joinRequestNotificationRepository).save(argument.capture());

        Assertions.assertEquals(travel, argument.getValue().getTravel());
        Assertions.assertEquals(driver, argument.getValue().getNotifiedPerson());
        Assertions.assertEquals(passenger, argument.getValue().getPassenger());
    }

    @Test
    void givenPassengerAppliedForTravel_whenCreateJoinTravelNotification_shouldThrowPassengerAlreadyAppliedForTravelException() {
        Mockito.when(joinRequestNotificationPredicates.forPassengerOrDriverOfTravel(passenger, travel))
            .thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.exists(mockExpression)).thenReturn(true);

        Assertions.assertThrows(PassengerAlreadyAppliedForTravelException.class,
            () -> joinRequestNotificationService.createJoinTravelNotification(travel, passenger));
    }

    @Test
    void givenIdShouldGetJoinRequestNotificationWithThatId() {
        Mockito.when(joinRequestNotificationRepository.findById(NotificationTestConstants.ID))
            .thenReturn(Optional.of(NotificationTestMocks.mockJoinNotification()));

        var fetchedJoinNotification = joinRequestNotificationService.findJoinRequestNotification(
            NotificationTestConstants.ID);

        Assertions.assertNotNull(fetchedJoinNotification);
        Assertions.assertEquals(NotificationTestConstants.ID, fetchedJoinNotification.getId());
    }

    @Test
    void givenWrongIdShouldThrowEntityNotFoundException() {
        Mockito.when(joinRequestNotificationRepository.findById(NotificationTestConstants.ID))
            .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
            () -> joinRequestNotificationService.findJoinRequestNotification(NotificationTestConstants.ID));
    }


    @Test
    void givenIdOfActiveJoinRequestShouldReturnJoinRequestNotificationWithThatId() {
        Mockito.when(joinRequestNotificationPredicates.forActiveForId(NotificationTestConstants.ID))
            .thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.findOne(mockExpression))
            .thenReturn(Optional.of(NotificationTestMocks.mockJoinNotification()));

        var fetchedJoinNotification = joinRequestNotificationService.getActiveJoinRequestNotification(
            NotificationTestConstants.ID);

        Assertions.assertNotNull(fetchedJoinNotification);
        Assertions.assertEquals(NotificationTestConstants.ID, fetchedJoinNotification.getId());
    }

    @Test
    void givenIdOfInactiveJoinRequestShouldThrowEntityNotFoundException() {
        Mockito.when(joinRequestNotificationPredicates.forActiveForId(NotificationTestConstants.ID))
            .thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.findOne(mockExpression))
            .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
            () -> joinRequestNotificationService.getActiveJoinRequestNotification(NotificationTestConstants.ID));
    }

    @Test
    void givenTravelWithPendingJoinRequestsShouldReturnThoseJoinRequests() {
        Mockito.when(joinRequestNotificationPredicates.forActiveForTravel(travel))
            .thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.findAll(mockExpression))
            .thenReturn(Collections.singletonList(joinRequest));

        List<JoinRequestNotification> joinRequestNotifications = joinRequestNotificationService
            .getPendingJoinRequestNotifications(travel);

        Assertions.assertFalse(joinRequestNotifications.isEmpty());
    }

    @Test
    void givenTravelIdAndPassengerIdShouldReturnPendingJoinRequestsForThatTravel() {
        var joinRequests = Collections.singletonList(joinRequest);

        Mockito.when(joinRequestNotificationPredicates.forPendingAndTravelIdAndPassengerId(travel.getId(),
            passenger.getId())).thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.findAll(mockExpression))
            .thenReturn(joinRequests);

        var joinRequestNotifications = joinRequestNotificationService
            .getPendingJoinRequestNotificationsForTravelAndPassenger(travel.getId(), passenger.getId());

        Assertions.assertFalse(joinRequestNotifications.isEmpty());
        Assertions.assertEquals(joinRequests, joinRequestNotifications);
    }

    @Test
    void givenTravelsWithRequestDataShouldUpdateThoseTravelsWithApplicationData() {
        var travels = Collections.singletonList(
            TravelTestMocks.mockTravelWithIsAppliedOutputDto());

        Mockito.when(
            joinRequestNotificationPredicates.forUserAndInTravelList(Mockito.eq(UserTestConstants.USER_EMAIL),
                Mockito.anyList())).thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.findAll(mockExpression))
            .thenReturn(Collections.singletonList(NotificationTestMocks.mockJoinNotification()));

        joinRequestNotificationService.updateTravelsWithApplicationData(UserTestConstants.USER_EMAIL, travels);

        Assertions.assertTrue(travels.get(0).isApplied());
    }

    @Test
    void givenTravelsWithoutRequestDataShouldNotUpdateThoseTravelsWithApplicationData() {
        List<TravelWithIsAppliedOutputDto> travels = Collections.singletonList(
            TravelTestMocks.mockTravelWithIsAppliedOutputDto());

        joinRequest.getTravel().setId(-1L);

        Mockito.when(
            joinRequestNotificationPredicates.forUserAndInTravelList(Mockito.eq(UserTestConstants.USER_EMAIL),
                Mockito.anyList())).thenReturn(mockExpression);
        Mockito.when(joinRequestNotificationRepository.findAll(mockExpression))
            .thenReturn(Collections.singletonList(joinRequest));

        joinRequestNotificationService.updateTravelsWithApplicationData(UserTestConstants.USER_EMAIL, travels);

        Assertions.assertFalse(travels.get(0).isApplied());
    }

    @Test
    void givenJoinRequestShouldApproveAndAddPassengerToTravel() {
        joinRequestNotificationService.accept(joinRequest);

        verifyJoinRequestStatusUpdated(JoinRequestStatus.APPROVED);

        Mockito.verify(notificationService)
            .createJoinRequestStatusChangeNotification(joinRequest, NotificationType.REQUEST_APPROVED);

        ArgumentCaptor<Profile> passengerArgument = ArgumentCaptor.forClass(Profile.class);
        ArgumentCaptor<Travel> travelArgument = ArgumentCaptor.forClass(Travel.class);

        Mockito.verify(travelService).addPassengerToTravel(passengerArgument.capture(), travelArgument.capture());

        Assertions.assertEquals(joinRequest.getPassenger().getId(), passengerArgument.getValue().getId());
        Assertions.assertEquals(joinRequest.getTravel().getId(), travelArgument.getValue().getId());

    }

    @Test
    void givenJoinRequestShouldReject() {
        joinRequestNotificationService.reject(joinRequest);

        verifyJoinRequestStatusUpdated(JoinRequestStatus.REJECTED);

        Mockito.verify(notificationService)
            .createJoinRequestStatusChangeNotification(joinRequest, NotificationType.REQUEST_REJECTED);
    }

    @Test
    void givenJoinRequestShouldCancel() {
        var joinRequests = Collections.singletonList(joinRequest);

        joinRequestNotificationService.cancel(joinRequests);

        @SuppressWarnings("unchecked") // We know exactly what argument type is passed but cannot specify it
        ArgumentCaptor<List<JoinRequestNotification>> saveArgument = ArgumentCaptor.forClass(List.class);

        Mockito.verify(joinRequestNotificationRepository).saveAll(saveArgument.capture());

        Assertions.assertEquals(joinRequests, saveArgument.getValue());
    }

    private void verifyJoinRequestStatusUpdated(JoinRequestStatus joinRequestStatus) {
        ArgumentCaptor<JoinRequestNotification> saveArgument = ArgumentCaptor.forClass(JoinRequestNotification.class);

        Mockito.verify(joinRequestNotificationRepository).save(saveArgument.capture());

        Assertions.assertEquals(joinRequestStatus, saveArgument.getValue().getStatus());
        Assertions.assertTrue(saveArgument.getValue().isProcessed());
    }

    @Test
    void givenUserIsDriverOfRequestWhenValidatingShouldNotThrowExceptions() {
        var driver = UserTestMocks.mockUserDto();
        driver.setId(joinRequest.getNotifiedPerson().getUser().getId());

        Assertions.assertDoesNotThrow(
            () -> joinRequestNotificationService.validateUserIsDriverOfRequest(driver, joinRequest));
    }

    @Test
    void givenUserIsNotDriverOfRequestWhenValidatingShouldThrowDriverHasNoAccessToJoinRequestException() {
        var driver = UserTestMocks.mockUserDto();

        Assertions.assertThrows(DriverHasNoAccessToJoinRequestException.class,
            () -> joinRequestNotificationService.validateUserIsDriverOfRequest(driver, joinRequest));
    }
}

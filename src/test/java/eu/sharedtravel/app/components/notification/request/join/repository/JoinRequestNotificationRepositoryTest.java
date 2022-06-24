package eu.sharedtravel.app.components.notification.request.join.repository;

import eu.sharedtravel.app.components.location.repository.LocationRepository;
import eu.sharedtravel.app.components.notification.NotificationTestConstants;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestStatus;
import eu.sharedtravel.app.components.notification.request.join.repository.predicate.JoinRequestNotificationPredicates;
import eu.sharedtravel.app.components.profile.ProfileTestConstants;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.repository.ProfileRepository;
import eu.sharedtravel.app.components.travel.TravelTestConstants;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.repository.TravelRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Slf4j
class JoinRequestNotificationRepositoryTest {

    @Autowired
    private JoinRequestNotificationRepository joinRequestNotificationRepository;

    @Autowired
    private JoinRequestNotificationPredicates joinRequestNotificationPredicates;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private LocationRepository locationRepository;

    private JoinRequestNotification joinRequestNotification;

    private Travel travel;

    @BeforeEach
    public void setUp() {
        var driver = profileRepository.findById(ProfileTestConstants.DRIVER_ID).orElse(null);
        var passenger = profileRepository.findById(ProfileTestConstants.ID).orElse(null);
        var from = locationRepository.findById("BGDB").orElse(null);
        var to = locationRepository.findById("BGSO").orElse(null);

        travel = new Travel();
        travel.setDepartureDate(LocalDateTime.now().minusMinutes(1));
        travel.setFrom(from);
        travel.setTo(to);
        travel.setStatus(TravelTestConstants.STATUS);
        travel.setDriver(driver);

        travelRepository.save(travel);

        joinRequestNotification = new JoinRequestNotification();
        joinRequestNotification.setNotifiedPerson(driver);
        joinRequestNotification.setPassenger(passenger);
        joinRequestNotification.setTravel(travel);
        joinRequestNotification.setMessageData(NotificationTestConstants.MESSAGE_DATA);
        joinRequestNotification.setType(NotificationTestConstants.TYPE);
        joinRequestNotification.setStatus(JoinRequestStatus.PENDING);

        joinRequestNotificationRepository.save(joinRequestNotification);
    }

    @Test
    void givenEmailAndListOfTravelIdsShouldReturnJoinTravelRequests() {
        var email = joinRequestNotification.getPassenger().getUser().getEmail();
        var travelIds = Collections.singletonList(joinRequestNotification.getTravel().getId());
        var predicate = joinRequestNotificationPredicates.forUserAndInTravelList(email, travelIds);

        var fetchedJoinTravelRequest = (List<JoinRequestNotification>) joinRequestNotificationRepository.findAll(
            predicate);

        Assertions.assertFalse(CollectionUtils.isEmpty(fetchedJoinTravelRequest));
    }

    @Test
    void givenEmailAndListOfTravelIdsWhenStatusIsNotPendingShouldReturnEmpty() {
        joinRequestNotification.setStatus(JoinRequestStatus.CANCELED);
        joinRequestNotificationRepository.save(joinRequestNotification);

        var email = joinRequestNotification.getPassenger().getUser().getEmail();
        var travelIds = Collections.singletonList(joinRequestNotification.getTravel().getId());
        var predicate = joinRequestNotificationPredicates.forUserAndInTravelList(email, travelIds);

        var fetchedJoinTravelRequest = (List<JoinRequestNotification>) joinRequestNotificationRepository.findAll(
            predicate);

        Assertions.assertTrue(CollectionUtils.isEmpty(fetchedJoinTravelRequest));
    }

    @Test
    void givenTravelIdAndPassengerIdShouldReturnPendingJoinRequestsForThatTravelAndPassenger() {
        var joinRequestNotifications = joinRequestNotificationRepository.findAll(joinRequestNotificationPredicates
            .forPendingAndTravelIdAndPassengerId(travel.getId(), ProfileTestConstants.ID));

        Assertions.assertFalse(CollectionUtils.isEmpty((Collection<?>) joinRequestNotifications));
    }

    @Test
    void givenWrongTravelIdAndPassengerIdShouldReturnEmptyList() {
        var joinRequestNotifications = joinRequestNotificationRepository.findAll(joinRequestNotificationPredicates
            .forPendingAndTravelIdAndPassengerId(-1L, ProfileTestConstants.ID));

        Assertions.assertTrue(CollectionUtils.isEmpty((Collection<?>) joinRequestNotifications));
    }


    @Test
    void givenTravelIdAndWrongPassengerIdShouldReturnEmptyList() {
        var joinRequestNotifications = joinRequestNotificationRepository.findAll(joinRequestNotificationPredicates
            .forPendingAndTravelIdAndPassengerId(travel.getId(), ProfileTestConstants.DRIVER_ID));

        Assertions.assertTrue(CollectionUtils.isEmpty((Collection<?>) joinRequestNotifications));
    }

    @Test
    void givenAppliedPassengerAndTravelShouldReturnExists() {
        var exists = joinRequestNotificationRepository.exists(
            joinRequestNotificationPredicates.forPassengerOrDriverOfTravel(
                joinRequestNotification.getPassenger(), joinRequestNotification.getTravel()));

        Assertions.assertTrue(exists);
    }

    @Test
    void givenDriverAndTravelShouldReturnExists() {
        var passenger = ProfileTestMocks.mockDriverProfile();

        var exists = joinRequestNotificationRepository.exists(
            joinRequestNotificationPredicates.forPassengerOrDriverOfTravel(
                passenger, joinRequestNotification.getTravel()));

        Assertions.assertTrue(exists);
    }

    @Test
    void givenNotAppliedPassengerAndTravelShouldNotReturnExists() {
        var passenger = ProfileTestMocks.mockDriverProfile();
        passenger.setId(-1L);

        var exists = joinRequestNotificationRepository.exists(
            joinRequestNotificationPredicates.forPassengerOrDriverOfTravel(
                passenger, joinRequestNotification.getTravel()));

        Assertions.assertFalse(exists);
    }

    @Test
    void givenIdOfAPendingJoinRequestShouldReturnIt() {
        var joinRequest = joinRequestNotificationRepository.findOne(
            joinRequestNotificationPredicates.forActiveForId(joinRequestNotification.getId()));

        Assertions.assertTrue(joinRequest.isPresent());
        Assertions.assertEquals(joinRequest.get().getId(), joinRequestNotification.getId());
    }

    @Test
    void givenIdOfNonPendingJoinRequestShouldReturnEmpty() {
        joinRequestNotification.setStatus(JoinRequestStatus.APPROVED);
        joinRequestNotificationRepository.save(joinRequestNotification);

        var joinRequest = joinRequestNotificationRepository.findOne(
            joinRequestNotificationPredicates.forActiveForId(joinRequestNotification.getId()));

        Assertions.assertFalse(joinRequest.isPresent());
    }

    @Test
    void givenTravelShouldReturnAllActiveJoinRequestsForIt() {
        List<JoinRequestNotification> joinRequests = (List<JoinRequestNotification>) joinRequestNotificationRepository.findAll(
            joinRequestNotificationPredicates.forActiveForTravel(travel));

        Assertions.assertFalse(joinRequests.isEmpty());
    }

    @Test
    void givenTravelWithNoActiveJoinRequestsShouldReturnEmpty() {
        joinRequestNotification.setStatus(JoinRequestStatus.APPROVED);
        joinRequestNotificationRepository.save(joinRequestNotification);

        List<JoinRequestNotification> joinRequests = (List<JoinRequestNotification>) joinRequestNotificationRepository.findAll(
            joinRequestNotificationPredicates.forActiveForTravel(travel));

        Assertions.assertTrue(joinRequests.isEmpty());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public JoinRequestNotificationPredicates joinRequestNotificationPredicates() {
            return new JoinRequestNotificationPredicates();
        }
    }
}

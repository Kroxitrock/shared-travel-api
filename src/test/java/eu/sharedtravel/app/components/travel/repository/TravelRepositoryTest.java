package eu.sharedtravel.app.components.travel.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.sharedtravel.app.components.location.model.Location;
import eu.sharedtravel.app.components.location.repository.LocationRepository;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.repository.ProfileRepository;
import eu.sharedtravel.app.components.travel.TravelTestConstants;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.model.TravelStatus;
import eu.sharedtravel.app.components.travel.repository.predicate.TravelPredicates;
import eu.sharedtravel.app.components.travel.service.dto.TravelFilterDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TravelRepositoryTest {

    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private TravelPredicates travelPredicates;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ProfileRepository profileRepository;

    private Travel travel;

    @BeforeEach
    public void setUp() {
        Location from = locationRepository.findById("BGDB").orElse(null);
        Location to = locationRepository.findById("BGSO").orElse(null);
        Profile driver = profileRepository.findById(2L).orElse(null);

        travel = new Travel();
        travel.setDepartureDate(LocalDateTime.now().minusMinutes(1));
        travel.setFrom(from);
        travel.setTo(to);
        travel.setStatus(TravelTestConstants.STATUS);
        travel.setDriver(driver);

        travelRepository.save(travel);
    }

    @Test
    void givenCorrectIdAndActiveTravelShouldReturnTravelWithThatId() {
        travel.setDepartureDate(LocalDateTime.now().plusMinutes(10));
        travelRepository.save(travel);

        var fetchedTravel = travelRepository.findOne(travelPredicates.forTravelWithIdActive(travel.getId()));

        Assertions.assertTrue(fetchedTravel.isPresent());
        Assertions.assertEquals(fetchedTravel.get().getId(), travel.getId());
    }

    @Test
    void givenCorrectIdAndTravelNotPendingShouldReturnEmpty() {
        travel.setDepartureDate(LocalDateTime.now().plusMinutes(10));
        travel.setStatus(TravelStatus.CANCELED);
        travelRepository.save(travel);

        var fetchedTravel = travelRepository.findOne(travelPredicates.forTravelWithIdActive(travel.getId()));

        Assertions.assertFalse(fetchedTravel.isPresent());
    }

    @Test
    void givenCorrectIdAndTravelWithOldDepartureDateShouldReturnEmpty() {
        var fetchedTravel = travelRepository.findOne(travelPredicates.forTravelWithIdActive(travel.getId()));

        Assertions.assertFalse(fetchedTravel.isPresent());
    }

    @Test
    void givenIdAndDriverIdThenShouldReturnTravelWithThatIdAndDriverId() {
        Optional<Travel> fetchedTravel = travelRepository.findOne(
            travelPredicates.forIdAndDriverId(travel.getId(), travel.getDriver().getId()));

        Assertions.assertTrue(fetchedTravel.isPresent());
        Assertions.assertEquals(travel.getId(), fetchedTravel.get().getId());
    }

    @Test
    void givenFilterShouldReturnTravel() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1L);

        TravelFilterDto dto = new TravelFilterDto("BGDB", "BGSO", yesterday);
        BooleanExpression predicate = travelPredicates.forFromAndToAndAfterDateAndNotCanceled(dto);

        Optional<Travel> fetchedTravel = travelRepository.findOne(predicate);

        Assertions.assertTrue(fetchedTravel.isPresent());
        Assertions.assertEquals(travel, fetchedTravel.get());
    }

    @Test
    void givenMyTravelFilterShouldReturnTravel() {
        BooleanExpression predicate = travelPredicates
            .forDriverIdOrAndPassengerAndFutureOrPast(ProfileTestMocks.mockDriverProfile(), false, false);

        Optional<Travel> fetchedTravel = travelRepository.findOne(predicate);

        Assertions.assertTrue(fetchedTravel.isPresent());
        Assertions.assertEquals(travel, fetchedTravel.get());
    }

    @Test
    void givenTravelsActiveInTimeFrameShouldReturnThem() {
        travel.setDepartureDate(LocalDateTime.now().plusMinutes(10));
        travelRepository.save(travel);

        var startOfDay = LocalDate.now().atStartOfDay();
        var endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        var travels = (List<Travel>) travelRepository.findAll(
            travelPredicates.forActiveInTimeFrame(startOfDay, endOfDay));

        Assertions.assertFalse(travels.isEmpty());
    }

    @Test
    void givenTravelsActiveButNotInTimeFrameShouldNotReturn() {
        travel.setDepartureDate(LocalDateTime.now().plusMinutes(10));
        travelRepository.save(travel);

        var startOfDay = LocalDate.now().minusDays(1).atStartOfDay();
        var endOfDay = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

        var travels = (List<Travel>) travelRepository.findAll(
            travelPredicates.forActiveInTimeFrame(startOfDay, endOfDay));

        Assertions.assertTrue(travels.isEmpty());
    }

    @Test
    void givenTravelsNotActiveInTimeFrameShouldNotReturn() {
        var startOfDay = LocalDate.now().minusDays(1).atStartOfDay();
        var endOfDay = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

        var travels = (List<Travel>) travelRepository.findAll(
            travelPredicates.forActiveInTimeFrame(startOfDay, endOfDay));

        Assertions.assertTrue(travels.isEmpty());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TravelPredicates travelPredicates() {
            return new TravelPredicates();
        }
    }
}

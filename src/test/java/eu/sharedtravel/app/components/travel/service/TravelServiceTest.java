package eu.sharedtravel.app.components.travel.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import eu.sharedtravel.app.common.Constants;
import eu.sharedtravel.app.components.location.LocationTestConstants;
import eu.sharedtravel.app.components.notification.service.NotificationService;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.travel.TravelTestConstants;
import eu.sharedtravel.app.components.travel.TravelTestMocks;
import eu.sharedtravel.app.components.travel.exception.JoinTravelDateException;
import eu.sharedtravel.app.components.travel.exception.JoinTravelStatusException;
import eu.sharedtravel.app.components.travel.exception.StatusChangeException;
import eu.sharedtravel.app.components.travel.exception.UserNotPassengerOfTravelException;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.model.TravelStatus;
import eu.sharedtravel.app.components.travel.repository.TravelRepository;
import eu.sharedtravel.app.components.travel.repository.predicate.TravelPredicates;
import eu.sharedtravel.app.components.travel.service.dto.TravelFilterDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelInputDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelUpdateDto;
import eu.sharedtravel.app.components.travel.service.mapper.TravelInputDtoMapper;
import eu.sharedtravel.app.components.travel.service.mapper.TravelInputDtoMapperImpl;
import eu.sharedtravel.app.components.travel.service.mapper.TravelUpdateDtoMapper;
import eu.sharedtravel.app.components.travel.service.mapper.TravelUpdateDtoMapperImpl;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TravelService.class, TravelInputDtoMapperImpl.class, TravelUpdateDtoMapperImpl.class})
class TravelServiceTest {

    @Autowired
    private TravelService travelService;

    @Autowired
    private TravelInputDtoMapper travelInputDtoMapper;

    @Autowired
    private TravelUpdateDtoMapper travelUpdateDtoMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private TravelRepository travelRepository;

    @MockBean
    private TravelPredicates travelPredicates;


    private Travel travel;

    private BooleanExpression mockExpression;

    @BeforeEach
    public void setUp() {
        travel = TravelTestMocks.mockTravel();
        mockExpression = Expressions.asBoolean(true);
    }

    @Test
    void givenTravelFilterShouldReturnFilteredList() {
        Page<Travel> mockTravelList = TravelTestMocks.mockTravelPage();
        TravelFilterDto travelFilterDto = new TravelFilterDto(LocationTestConstants.SOF_CODE,
            LocationTestConstants.DB_CODE, LocalDateTime.now());
        Pageable pageable = PageRequest.of(1, 20);

        Mockito.when(travelPredicates.defaultSort()).thenReturn(Sort.by("id"));
        Mockito.when(travelPredicates.forFromAndToAndAfterDateAndNotCanceled(travelFilterDto))
            .thenReturn(mockExpression);
        Mockito.when(travelRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
            .thenReturn(mockTravelList);

        Page<Travel> output = travelService.findFilteredTravels(travelFilterDto, pageable);

        Assertions.assertNotNull(output);
        Assertions.assertFalse(output.isEmpty());
    }

    @Test
    void givenMyTravelsFilterShouldReturnMyTravels() {
        Page<Travel> mockTravelList = TravelTestMocks.mockTravelPage();
        Pageable pageable = PageRequest.of(1, 20);

        Mockito.when(travelPredicates.defaultSort()).thenReturn(Sort.by("id"));
        Mockito.when(travelPredicates
            .forDriverIdOrAndPassengerAndFutureOrPast(Mockito.any(Profile.class), Mockito.anyBoolean(),
                Mockito.anyBoolean())).thenReturn(mockExpression);
        Mockito.when(travelRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
            .thenReturn(mockTravelList);

        Page<Travel> output = travelService.findMyTravels(false, false, pageable, ProfileTestMocks.mockDriverProfile());

        Assertions.assertNotNull(output);
        Assertions.assertFalse(output.isEmpty());
    }

    @Test
    void givenThereAreTravelsForTodayShouldReturnThoseTravels() {
        var travels = Collections.singletonList(travel);

        Mockito.when(travelPredicates.forActiveInTimeFrame(Mockito.any(), Mockito.any()))
            .thenReturn(mockExpression);
        Mockito.when(travelRepository.findAll(mockExpression)).thenReturn(travels);

        var response = travelService.getTravelsForToday();

        Assertions.assertFalse(response.isEmpty());
        Assertions.assertEquals(travels, response);
    }

    @Test
    void givenIdShouldReturnTravelWithSameId() {
        Mockito.when(travelRepository.findById(travel.getId())).thenReturn(Optional.of(travel));

        Travel fetchedTravel = travelService.getTravel(travel.getId());

        Assertions.assertEquals(fetchedTravel.getId(), travel.getId());
    }

    @Test
    void givenWrongIdShouldThrowEntityNotFoundException() {
        Long id = -1L;

        Mockito.when(travelRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> travelService.getTravel(id));
    }

    @Test
    void givenActiveTravelIdShouldReturnTravelWithSameId() {
        Mockito.when(travelPredicates.forTravelWithIdActive(travel.getId())).thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(mockExpression)).thenReturn(Optional.of(travel));

        Travel fetchedTravel = travelService.getActiveTravelById(travel.getId());

        Assertions.assertEquals(fetchedTravel.getId(), travel.getId());
    }

    @Test
    void givenInactiveOrMissingTravelIdShouldThrowEntityNotFoundException() {
        Long id = -1L;

        Mockito.when(travelPredicates.forTravelWithIdActive(id)).thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(mockExpression)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> travelService.getActiveTravelById(id));
    }


    @Test
    void givenIdAndDriverIdShouldReturnTravelWithSameId() {
        Mockito.when(travelPredicates.forIdAndDriverId(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(mockExpression);

        Mockito.when(travelRepository.findOne(Mockito.any(BooleanExpression.class))).thenReturn(Optional.of(travel));

        Travel fetchedTravel = travelService.getTravel(travel.getId(), travel.getDriver().getId());

        Assertions.assertEquals(fetchedTravel.getId(), travel.getId());
    }

    @Test
    void givenWrongPredicateForIdAndDriverIdShouldThrowEntityNotFoundException() {
        Long travelId = Constants.INVALID_ID;
        Long driverId = Constants.INVALID_ID;

        Mockito.when(travelPredicates.forIdAndDriverId(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(mockExpression);

        Mockito.when(travelRepository.findOne(Mockito.any(BooleanExpression.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
            () -> travelService.getTravel(travelId, driverId));
    }

    @Test
    void givenTravelShouldMapAndSaveAndReturnIt() {
        TravelInputDto dto = new TravelInputDto(
            travel.getFrom().getCode(),
            travel.getTo().getCode(),
            travel.getDepartureDate());

        Mockito.when(travelRepository.save(Mockito.any())).thenAnswer(invocation -> {
            Travel travel = invocation.getArgument(0);
            travel.setId(TravelTestConstants.ID);
            return travel;
        });

        Travel output = travelService.saveTravel(dto, travel.getDriver());

        Assertions.assertEquals(travel, output);
        Assertions.assertEquals(TravelStatus.PENDING, travel.getStatus());
        Assertions.assertEquals(travel.getDriver(), output.getDriver());
    }

    @Test
    void givenTravelShouldMapAndUpdateAndReturnIt() {
        TravelUpdateDto dto = new TravelUpdateDto(
            travel.getFrom().getCode(),
            travel.getTo().getCode(),
            travel.getDepartureDate(),
            travel.getStatus());

        Mockito.when(travelPredicates.forIdAndDriverId(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(Mockito.any(BooleanExpression.class))).thenReturn(Optional.of(travel));
        Mockito.when(travelRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Assertions.assertEquals(travel,
            travelService.updateTravel(travel.getId(), dto, travel.getDriver().getId()));
    }

    @Test
    void givenTravelIdAndCanceledStatusAndProfileShouldUpdateTravelStatus() {
        List<Profile> applicants = Collections.singletonList(ProfileTestMocks.mockUserProfile());
        ProfileDto driver = ProfileTestMocks.mockDriverProfileDto();

        Mockito.when(travelRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Travel output = travelService.cancelTravel(travel, driver, applicants);

        Mockito.verify(notificationService).createTravelCanceledNotifications(travel);
        Mockito.verify(notificationService).createAppliedTravelCanceledNotifications(travel, applicants);

        Assertions.assertEquals(travel.getId(), output.getId());
        Assertions.assertEquals(TravelStatus.CANCELED, output.getStatus());
    }

    @Test
    void givenTravelIdAndProfileShouldAddProfileToPassengers() {
        travel.setDepartureDate(LocalDateTime.now().plusDays(1));

        Mockito.when(travelRepository.findById(travel.getId())).thenReturn(Optional.of(travel));
        Mockito.when(travelRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Travel output = travelService.addPassengerToTravel(ProfileTestMocks.mockUserProfile(), travel);

        Assertions.assertEquals(travel.getId(), output.getId());
        Assertions.assertEquals(1, output.getPassengers().size());
    }

    @Test
    void givenTravelIdAndCanceledStatusAndWrongProfileShouldThrowStatusChangeException() {
        ProfileDto profileDto = ProfileTestMocks.mockUserProfileDto();

        Assertions.assertThrows(StatusChangeException.class,
            () -> travelService.cancelTravel(travel, profileDto, Collections.emptyList()));
    }

    @Test
    void givenTravelStatusNotPendingShouldThrowJoinTravelStatusException() {
        travel.setStatus(TravelStatus.CANCELED);

        Mockito.when(travelRepository.findById(travel.getId())).thenReturn(Optional.of(travel));

        Assertions.assertThrows(JoinTravelStatusException.class,
            () -> travelService.addPassengerToTravel(ProfileTestMocks.mockUserProfile(), travel));
    }

    @Test
    void givenTravelInThePastShouldThrowJoinTravelDateException() {
        Mockito.when(travelPredicates.forTravelWithIdActive(travel.getId())).thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(mockExpression)).thenReturn(Optional.of(travel));

        Assertions.assertThrows(JoinTravelDateException.class,
            () -> travelService.addPassengerToTravel(ProfileTestMocks.mockUserProfile(), travel));
    }

    @Test
    void givenPassengerAndTravelIdWhenRemovedByThemselfShouldRemovePassengerFromTravel() {
        var passenger = ProfileTestMocks.mockUserProfile();
        var travel = TravelTestMocks.mockTravel();

        Mockito.when(travelPredicates.forTravelWithIdActive(travel.getId())).thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(mockExpression)).thenReturn(Optional.of(travel));

        travelService.removePassengerFromTravel(passenger, travel, true);

        Mockito.verify(travelRepository).save(travel);
        Mockito.verify(notificationService).createPassengerLeftNotification(travel, passenger);

        Assertions.assertTrue(travel.getPassengers().isEmpty());
    }

    @Test
    void givenPassengerAndTravelIdWhenNotRemovedByThemselfShouldRemovePassengerFromTravel() {
        var passenger = ProfileTestMocks.mockUserProfile();
        var travel = TravelTestMocks.mockTravel();

        Mockito.when(travelPredicates.forTravelWithIdActive(travel.getId())).thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(mockExpression)).thenReturn(Optional.of(travel));

        travelService.removePassengerFromTravel(passenger, travel, false);

        Mockito.verify(travelRepository).save(travel);
        Mockito.verify(notificationService).createPassengerKickedNotification(travel, passenger);

        Assertions.assertTrue(travel.getPassengers().isEmpty());
    }

    @Test
    void givenPersonNotPassengerWhenRemovePassengerFromTravelShouldThrowException() {
        var passenger = ProfileTestMocks.mockUserProfile();
        var travel = TravelTestMocks.mockTravel();
        travel.setPassengers(Collections.emptySet());

        Mockito.when(travelPredicates.forTravelWithIdActive(travel.getId())).thenReturn(mockExpression);
        Mockito.when(travelRepository.findOne(mockExpression)).thenReturn(Optional.of(travel));

        Assertions.assertThrows(UserNotPassengerOfTravelException.class,
            () -> travelService.removePassengerFromTravel(passenger, travel, false));
    }
}

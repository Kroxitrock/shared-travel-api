package eu.sharedtravel.app.components.travel.service;

import eu.sharedtravel.app.common.page.PageableUtil;
import eu.sharedtravel.app.components.notification.service.NotificationService;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
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
import eu.sharedtravel.app.components.travel.service.mapper.TravelUpdateDtoMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final NotificationService notificationService;

    private final TravelRepository travelRepository;
    private final TravelPredicates travelPredicates;

    private final TravelInputDtoMapper travelInputDtoMapper;
    private final TravelUpdateDtoMapper travelUpdateDtoMapper;

    public Travel getTravel(Long id) {
        return travelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Travel with id %d not found!", id)));
    }

    public Travel getActiveTravelById(Long id) {
        return travelRepository.findOne(travelPredicates.forTravelWithIdActive(id))
            .orElseThrow(() -> new EntityNotFoundException(String.format("Travel with id %d is not active!", id)));
    }

    public Travel getTravel(Long id, Long driverId) {
        return travelRepository.findOne(travelPredicates.forIdAndDriverId(id, driverId))
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Travel with id %d and driver %d not found!", id, driverId)));
    }

    public Page<Travel> findFilteredTravels(TravelFilterDto dto, Pageable pageable) {
        pageable = PageableUtil.pageableWithDefaultSort(pageable, travelPredicates.defaultSort());

        return travelRepository.findAll(travelPredicates.forFromAndToAndAfterDateAndNotCanceled(dto), pageable);
    }

    public Page<Travel> findMyTravels(Boolean driverOnly, Boolean inFuture, Pageable pageable, Profile profile) {
        pageable = PageableUtil.pageableWithDefaultSort(pageable, travelPredicates.defaultSort());

        return travelRepository.findAll(travelPredicates
            .forDriverIdOrAndPassengerAndFutureOrPast(profile, driverOnly, inFuture), pageable);
    }

    public List<Travel> getTravelsForToday() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        return (List<Travel>) travelRepository.findAll(travelPredicates.forActiveInTimeFrame(startOfToday, endOfToday));
    }

    @Transactional
    public Travel saveTravel(TravelInputDto dto, Profile driver) {
        Travel travel = travelInputDtoMapper.travelInputDtoToTravel(dto);

        travel.setDriver(driver);
        travel.setStatus(TravelStatus.PENDING);

        return travelRepository.save(travel);
    }

    // FIXME: This method should probably update only the fields sent to id (PATCH), but that should be fixed when this functionality is needed.
    @Transactional
    public Travel updateTravel(Long id, TravelUpdateDto dto, Long driverId) {
        Travel fetchedTravel = getTravel(id, driverId);

        Travel updatedTravel = travelUpdateDtoMapper.updateTravelFromTravelUpdateDto(dto, fetchedTravel);

        return travelRepository.save(updatedTravel);
    }

    @Transactional
    public Travel cancelTravel(Travel travel, ProfileDto profile, List<Profile> applicants) {
        if (!travel.getDriver().getId().equals(profile.getId())) {
            throw new StatusChangeException(profile.getUserId(), travel.getId(), TravelStatus.CANCELED);
        }

        travel.setStatus(TravelStatus.CANCELED);
        travelRepository.save(travel);

        notificationService.createTravelCanceledNotifications(travel);
        notificationService.createAppliedTravelCanceledNotifications(travel, applicants);

        return travel;
    }

    @Transactional
    public Travel addPassengerToTravel(Profile passenger, Travel travel) {
        if (travel.getStatus() != TravelStatus.PENDING) {
            throw new JoinTravelStatusException(passenger.getId(), travel.getId(), travel.getStatus());
        }

        if (!travel.getDepartureDate().isAfter(LocalDateTime.now())) {
            throw new JoinTravelDateException(passenger.getId(), travel.getId(), travel.getDepartureDate());
        }

        travel.addPassenger(passenger);

        return travelRepository.save(travel);
    }

    @Transactional
    public void removePassengerFromTravel(Profile passenger, Travel travel, boolean removedByThemself) {
        if (travel.getPassengers().stream().noneMatch(p -> p.equals(passenger))) {
            throw new UserNotPassengerOfTravelException(passenger.getId(), travel.getId());
        }

        travel.getPassengers().remove(passenger);

        travelRepository.save(travel);

        if (removedByThemself) {
            notificationService.createPassengerLeftNotification(travel, passenger);
        } else {
            notificationService.createPassengerKickedNotification(travel, passenger);
        }
    }
}

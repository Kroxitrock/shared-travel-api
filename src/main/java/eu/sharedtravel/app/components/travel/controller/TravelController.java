package eu.sharedtravel.app.components.travel.controller;

import eu.sharedtravel.app.common.security.OptionalUser;
import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.service.JoinRequestNotificationService;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.travel.exception.UserNotDriverOfTravelException;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.model.TravelStatus;
import eu.sharedtravel.app.components.travel.service.TravelService;
import eu.sharedtravel.app.components.travel.service.dto.TravelFilterDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelInputDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelOutputDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelUpdateDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelWithIsAppliedOutputDto;
import eu.sharedtravel.app.components.travel.service.mapper.TravelOutputDtoMapper;
import eu.sharedtravel.app.components.travel.service.mapper.TravelWithIsAppliedOutputDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travels")
@RequiredArgsConstructor
@Tag(name = "Travel Controller", description = "Travel Endpoints")
public class TravelController {

    private final TravelService travelService;
    private final ProfileService profileService;
    private final JoinRequestNotificationService joinRequestNotificationService;

    private final TravelOutputDtoMapper travelOutputDtoMapper;
    private final TravelWithIsAppliedOutputDtoMapper travelWithIsAppliedOutputDtoMapper;

    @Operation(summary = "Get a list of filtered travels", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public Page<? extends TravelOutputDto> getAllFiltered(
        @ParameterObject Pageable pageable,
        @Valid TravelFilterDto travelFilterDto,
        @OptionalUser String email) {
        Page<Travel> travels = travelService.findFilteredTravels(travelFilterDto, pageable);

        if (email == null) {
            return new PageImpl<>(travels.stream().map(travelOutputDtoMapper::travelToTravelOutputDto)
                .collect(Collectors.toList()), pageable, travels.getTotalElements());
        }

        List<TravelWithIsAppliedOutputDto> extendedTravels = travels.stream()
            .map(travelWithIsAppliedOutputDtoMapper::travelToTravelWithIsAppliedOutputDto).collect(Collectors.toList());

        joinRequestNotificationService.updateTravelsWithApplicationData(email, extendedTravels);

        return new PageImpl<>(extendedTravels, pageable, travels.getTotalElements());
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get all travels for authenticated user", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/my")
    public Page<TravelOutputDto> getMyTravels(@ParameterObject Pageable pageable,
        @RequestParam @NotNull Boolean driverOnly,
        @RequestParam @NotNull Boolean inFuture,
        @ResolveUser Profile profile) {
        Page<Travel> travels = travelService.findMyTravels(driverOnly, inFuture, pageable, profile);

        return new PageImpl<>(travels.stream().map(travelOutputDtoMapper::travelToTravelOutputDto)
            .collect(Collectors.toList()), pageable, travels.getTotalElements());
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Save a new travel", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    @SuppressWarnings("squid:S4684") // The driver is coming from @ResolveUser and is thus safe
    public TravelOutputDto createTravel(@RequestBody @Valid TravelInputDto travelInputDto,
        @ResolveUser Profile driver) {
        Travel savedTravel = travelService.saveTravel(travelInputDto, driver);

        return travelOutputDtoMapper.travelToTravelOutputDto(savedTravel);
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Update an existing travel", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{id}")
    public TravelOutputDto updateTravel(@PathVariable Long id, @RequestBody @Valid TravelUpdateDto travelUpdateDto,
        @ResolveUser ProfileDto driver) {
        Travel updatedTravel = travelService.updateTravel(id, travelUpdateDto, driver.getId());

        return travelOutputDtoMapper.travelToTravelOutputDto(updatedTravel);
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Cancel travel by id", security = @SecurityRequirement(name = "JWT"))
    @PatchMapping("/{id}/cancel")
    public TravelOutputDto cancelTravelById(@PathVariable @NotNull Long id, @RequestParam @NotNull TravelStatus status,
        @ResolveUser ProfileDto profile) {
        Travel travel = travelService.getActiveTravelById(id);

        List<JoinRequestNotification> joinRequestNotifications = joinRequestNotificationService
            .getPendingJoinRequestNotifications(travel);

        List<Profile> applicants = joinRequestNotifications.stream()
            .map(JoinRequestNotification::getPassenger).collect(Collectors.toList());

        Travel updatedTravel = travelService.cancelTravel(travel, profile, applicants);

        return travelOutputDtoMapper.travelToTravelOutputDto(updatedTravel);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Leave travel by id", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{id}/leave")
    @SuppressWarnings("squid:S4684") // The passenger is coming from @ResolveUser and is thus safe
    public void leaveTravel(@PathVariable Long id, @ResolveUser Profile passenger) {
        Travel travel = travelService.getActiveTravelById(id);

        travelService.removePassengerFromTravel(passenger, travel, true);
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Kick passenger from travel by id", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{id}/passengers/{passengerId}/kick")
    @SuppressWarnings("squid:S4684") // The driver is coming from @ResolveUser and is thus safe
    public void kickPassengerFromTravel(@PathVariable Long id, @PathVariable Long passengerId,
        @ResolveUser Profile driver) {
        Travel travel = travelService.getActiveTravelById(id);
        Profile passenger = profileService.getProfile(passengerId);

        if (!travel.getDriver().equals(driver)) {
            throw new UserNotDriverOfTravelException(driver.getId(), travel.getId());
        }

        travelService.removePassengerFromTravel(passenger, travel, false);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Create a join request for an existing travel", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{id}/join")
    @SuppressWarnings("squid:S4684") // The passenger is coming from @ResolveUser and is thus safe
    public void requestJoinTravel(@PathVariable @NotNull Long id,
        @ResolveUser Profile passenger) {
        Travel travel = travelService.getActiveTravelById(id);

        joinRequestNotificationService.createJoinTravelNotification(travel, passenger);
    }
}

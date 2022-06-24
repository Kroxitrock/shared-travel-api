package eu.sharedtravel.app.components.notification.request.join.controller;

import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.service.JoinRequestNotificationService;
import eu.sharedtravel.app.components.notification.request.join.service.dto.JoinRequestNotificationOutputDto;
import eu.sharedtravel.app.components.notification.service.mapper.NotificationOutputDtoMapper;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.TravelService;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications/requests/join")
@RequiredArgsConstructor
@Tag(name = "Join request notification controller", description = "Join Request Endpoints")
public class JoinRequestNotificationController {

    private final NotificationOutputDtoMapper notificationOutputDtoMapper;

    private final JoinRequestNotificationService joinRequestNotificationService;
    private final TravelService travelService;

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Get join request notification by id", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{id}")
    public JoinRequestNotificationOutputDto getJoinRequestNotification(@PathVariable Long id,
        @ResolveUser UserDto driver) {
        JoinRequestNotification joinRequest = joinRequestNotificationService.findJoinRequestNotification(id);

        joinRequestNotificationService.validateUserIsDriverOfRequest(driver, joinRequest);

        return notificationOutputDtoMapper.joinNotificationToJoinNotificationOutputDto(joinRequest);
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Accept join request notification by id", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{id}/accept")
    public void acceptJoinRequest(@PathVariable Long id, @ResolveUser UserDto driver) {
        JoinRequestNotification joinRequest = getJoinRequestValidatedForDriver(id, driver);

        joinRequestNotificationService.accept(joinRequest);
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Reject join request notification by id", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{id}/reject")
    public void rejectJoinRequest(@PathVariable Long id, @ResolveUser UserDto driver) {
        JoinRequestNotification joinRequest = getJoinRequestValidatedForDriver(id, driver);

        joinRequestNotificationService.reject(joinRequest);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Cancel join request notification by travel id and logged in passenger", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/pending/travel/{travelId}/cancel")
    public void cancelJoinRequest(@PathVariable Long travelId, @ResolveUser UserDto passenger) {
        List<JoinRequestNotification> joinRequests = joinRequestNotificationService
            .getPendingJoinRequestNotificationsForTravelAndPassenger(travelId, passenger.getId());

        if (joinRequests.isEmpty()) {
            throw new EntityNotFoundException(
                String.format("No join request found for travelId %d and applicant with id %d!", travelId,
                    passenger.getId()));
        }

        joinRequestNotificationService.cancel(joinRequests);
    }

    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Get a list of all pending join request notifications for a travel", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/pending/travel/{travelId}")
    public List<JoinRequestNotificationOutputDto> getPendingJoinRequestsForTravel(@PathVariable Long travelId,
        @ResolveUser ProfileDto driver) {
        Travel travel = travelService.getTravel(travelId, driver.getId());

        List<JoinRequestNotification> joinRequestNotifications = joinRequestNotificationService
            .getPendingJoinRequestNotifications(travel);

        return joinRequestNotifications.stream()
            .map(notificationOutputDtoMapper::joinNotificationToJoinNotificationOutputDto)
            .collect(Collectors.toList());
    }

    private JoinRequestNotification getJoinRequestValidatedForDriver(Long joinRequestId, UserDto driver) {
        JoinRequestNotification joinRequest = joinRequestNotificationService.getActiveJoinRequestNotification(
            joinRequestId);

        joinRequestNotificationService.validateUserIsDriverOfRequest(driver, joinRequest);

        return joinRequest;
    }
}

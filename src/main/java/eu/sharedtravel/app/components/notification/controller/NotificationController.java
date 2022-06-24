package eu.sharedtravel.app.components.notification.controller;

import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.notification.service.NotificationService;
import eu.sharedtravel.app.components.notification.service.dto.NotificationOutputDto;
import eu.sharedtravel.app.components.notification.service.mapper.NotificationOutputDtoMapper;
import eu.sharedtravel.app.components.profile.model.Profile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Notification Endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    private final NotificationOutputDtoMapper notificationOutputDtoMapper;

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get all notifications for authenticated user", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public Page<NotificationOutputDto> getNotifications(
        @ParameterObject Pageable pageable,
        @ResolveUser Profile profile) {
        Page<Notification> notifications = notificationService.findNotifications(profile, pageable);

        return new PageImpl<>(notifications.stream()
            .map(notificationOutputDtoMapper::notificationToNotificationOutputDto)
            .collect(Collectors.toList()), pageable, notifications.getTotalElements());
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get notification count for authenticated user", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/count")
    public Long getNotificationCount(@ResolveUser Profile profile) {
        return notificationService.notificationCount(profile);
    }

}

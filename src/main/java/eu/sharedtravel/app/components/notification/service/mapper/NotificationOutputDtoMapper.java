package eu.sharedtravel.app.components.notification.service.mapper;

import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.service.dto.JoinRequestNotificationOutputDto;
import eu.sharedtravel.app.components.notification.service.dto.NotificationOutputDto;
import eu.sharedtravel.app.components.profile.service.mapper.ProfileOutputDtoMapper;
import eu.sharedtravel.app.components.travel.service.mapper.TravelOutputDtoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProfileOutputDtoMapper.class, TravelOutputDtoMapper.class})
public interface NotificationOutputDtoMapper {

    NotificationOutputDto notificationToNotificationOutputDto(Notification entity);

    JoinRequestNotificationOutputDto joinNotificationToJoinNotificationOutputDto(JoinRequestNotification entity);
}

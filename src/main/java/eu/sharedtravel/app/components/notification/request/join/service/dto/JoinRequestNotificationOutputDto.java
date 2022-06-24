package eu.sharedtravel.app.components.notification.request.join.service.dto;

import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestStatus;
import eu.sharedtravel.app.components.notification.service.dto.NotificationOutputDto;
import eu.sharedtravel.app.components.profile.service.dto.ProfileOutputDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelOutputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default Dto used for output operations with {@link JoinRequestNotification}
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JoinRequestNotificationOutputDto extends NotificationOutputDto {

    @Schema(description = "The notified person", required = true)
    @NotNull
    private ProfileOutputDto passenger;

    @Schema(description = "The notified person", required = true)
    @NotNull
    private TravelOutputDto travel;

    @Schema(description = "Status of the join travel request", required = true)
    @NotNull
    private JoinRequestStatus status;
}

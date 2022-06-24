package eu.sharedtravel.app.components.notification.service.dto;

import eu.sharedtravel.app.components.notification.model.NotificationType;
import eu.sharedtravel.app.components.profile.service.dto.ProfileOutputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Default dto used for output operations with {@link eu.sharedtravel.app.components.notification.model.Notification}
 */
@Data
public class NotificationOutputDto {

    @Schema(description = "Unique identifier of the Notification", example = "1", required = true)
    @NotNull
    private Long id;

    @Schema(description = "The notified person", required = true)
    @NotNull
    private ProfileOutputDto notifiedPerson;

    @Schema(description = "The type of the notification", required = true)
    @NotNull
    private NotificationType type;

    @Schema(description = "The data for the notification message", required = true)
    @NotBlank
    private String messageData;

    private boolean read;

    private boolean processed;

    @Schema(description = "Timestamp of when the notification was received", required = true)
    private LocalDateTime createdDate;
}

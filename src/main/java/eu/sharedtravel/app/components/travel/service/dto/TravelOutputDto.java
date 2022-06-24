package eu.sharedtravel.app.components.travel.service.dto;

import eu.sharedtravel.app.components.profile.service.dto.ProfileOutputDto;
import eu.sharedtravel.app.components.travel.model.TravelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Default dto used for output operations with {@link eu.sharedtravel.app.components.travel.model.Travel}
 */
@Data
@SuperBuilder
public class TravelOutputDto {

    @Schema(description = "Unique identifier of the Travel", example = "1", required = true)
    @NotNull
    private Long id;

    @Schema(description = "Code of the location from where the travel starts", example = "BGSO", required = true)
    @NotBlank
    private String from;

    @Schema(description = "Code of the location where the travel ends", example = "BGDB", required = true)
    @NotBlank
    private String to;

    @Schema(description = "Timestamp on which the travel will start", example = "2022-01-10T16:20:00.000")
    @NotNull
    private LocalDateTime departureDate;

    @Schema(description = "Status of the travel", example = "PENDING", required = true)
    @NotNull
    private TravelStatus status;

    @Schema(description = "Diver of the travel", required = true)
    @NotNull
    private ProfileOutputDto driver;

    @Schema(description = "Passengers of the  of the travel")
    private Set<ProfileOutputDto> passengers;
}

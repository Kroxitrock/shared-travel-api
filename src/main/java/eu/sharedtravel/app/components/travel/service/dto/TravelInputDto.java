package eu.sharedtravel.app.components.travel.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Default dto used for input operations with {@link eu.sharedtravel.app.components.travel.model.Travel}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelInputDto {

    @Schema(description = "Code of the location from where the travel starts", example = "BGSO", required = true)
    @NotBlank
    private String from;

    @Schema(description = "Code of the location where the travel ends", example = "BGDB", required = true)
    @NotBlank
    private String to;

    @Schema(description = "Timestamp on which the travel will start", example = "2022-09-10T16:20:00.000")
    @NotNull
    private LocalDateTime departureDate;
}

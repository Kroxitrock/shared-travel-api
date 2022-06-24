package eu.sharedtravel.app.components.travel.service.dto;

import eu.sharedtravel.app.components.travel.model.TravelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TravelUpdateDto extends TravelInputDto {

    @Schema(description = "Status of the travel", example = "CANCELED", required = true)
    @NotNull
    private TravelStatus status;

    public TravelUpdateDto(
        @NotBlank String from,
        @NotBlank String to,
        @NotNull LocalDateTime departureDate,
        @NotNull TravelStatus status) {
        super(from, to, departureDate);
        this.status = status;
    }
}

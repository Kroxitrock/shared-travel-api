package eu.sharedtravel.app.components.travel.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TravelWithIsAppliedOutputDto extends TravelOutputDto {

    @Schema(description = "Whether the current user has applied to the travel")
    boolean isApplied;

}

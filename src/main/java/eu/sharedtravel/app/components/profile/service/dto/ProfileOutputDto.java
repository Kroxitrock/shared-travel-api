package eu.sharedtravel.app.components.profile.service.dto;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsOutputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Default dto used for output operations with {@link Profile}
 */
@Data
@Builder
public class ProfileOutputDto {

    @Schema(description = "Unique identifier of the Profile", example = "1", required = true)
    @NotNull
    private Long id;

    @Schema(description = "First name of the Profile", example = "Alexander", required = true)
    @NotBlank
    private String firstName;

    @Schema(description = "Last name of the Profile", example = "Verbovskiy", required = true)
    @NotBlank
    private String lastName;

    @Schema(description = "Email of the Profile (also used as an username)", example = "sashoverbovskiy@gmail.com", required = true)
    @NotBlank
    private String email;

    @Schema(description = "Settings of the Profile", required = true)
    @NotNull
    private ProfileSettingsOutputDto profileSettings;
}

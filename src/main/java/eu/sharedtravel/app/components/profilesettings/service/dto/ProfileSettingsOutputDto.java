package eu.sharedtravel.app.components.profilesettings.service.dto;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Default dto used for output operations with {@link ProfileSettings}
 */
@Data
@Builder
public class ProfileSettingsOutputDto {

    @Schema(description = "Unique identifier of the Profile Settings", example = "1", required = true)
    @NotNull
    private Long id;

    @Schema(description = "Visibility of the profile email", example = "true", required = true)
    @NotNull
    private Boolean emailVisible;
}

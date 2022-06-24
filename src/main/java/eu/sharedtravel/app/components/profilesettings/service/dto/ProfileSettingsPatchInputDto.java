package eu.sharedtravel.app.components.profilesettings.service.dto;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Default dto used for patching input operations with {@link ProfileSettings}
 */
@Data
public class ProfileSettingsPatchInputDto {

    @Schema(description = "Visibility of the profile email", example = "true")
    private Boolean emailVisible;
}

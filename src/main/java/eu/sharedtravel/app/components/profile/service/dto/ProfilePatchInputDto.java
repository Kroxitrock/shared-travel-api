package eu.sharedtravel.app.components.profile.service.dto;

import eu.sharedtravel.app.components.profile.model.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Default dto used for patching input operations with {@link Profile}
 */
@Data
public class ProfilePatchInputDto {

    @Schema(description = "First name of the Profile", example = "Alexander")
    private String firstName;

    @Schema(description = "Last name of the Profile", example = "Verbovskiy")
    private String lastName;

    @Schema(description = "Email of the Profile (also used as an username)", example = "sashoverbovskiy@gmail.com")
    private String email;
}

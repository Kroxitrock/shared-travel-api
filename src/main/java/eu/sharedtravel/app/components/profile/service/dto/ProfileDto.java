package eu.sharedtravel.app.components.profile.service.dto;

import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsDto;
import eu.sharedtravel.app.components.user.model.UserAuthority;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * DTO representing {@link eu.sharedtravel.app.components.profile.model.Profile}. This dto should be used only for
 * internal work and only after it has been calculated by {@link eu.sharedtravel.app.common.security.UserResolver}
 */
@Data
public class ProfileDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 25)
    private String firstName;

    @NotBlank
    @Size(max = 25)
    private String lastName;

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotNull
    private ProfileSettingsDto profileSettings;

    private Set<UserAuthority> authorities;
}

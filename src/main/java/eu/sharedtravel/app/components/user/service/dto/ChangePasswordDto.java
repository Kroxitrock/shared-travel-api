package eu.sharedtravel.app.components.user.service.dto;

import eu.sharedtravel.app.components.user.validators.PasswordConstraint;
import eu.sharedtravel.app.components.user.validators.PasswordsMatchConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@PasswordsMatchConstraint(
    password = "password",
    passwordConfirmation = "passwordConfirmation"
)
@Data
@Builder
public class ChangePasswordDto {

    @Schema(description = "The old Password of the user used to authorize the user", example = "P@ssw0rd", required = true)
    @PasswordConstraint
    @NotBlank
    @Size(min = 8, max = 128)
    private String oldPassword;

    @Schema(description = "The new Password of the user", example = "P@ssw0rd", required = true)
    @PasswordConstraint
    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @Schema(description = "The new Password confirmation used to validate the user has entered a correct password", example = "P@ssw0rd", required = true)
    @NotBlank
    @Size(min = 8, max = 128)
    private String passwordConfirmation;
}

package eu.sharedtravel.app.components.user.service.dto;

import eu.sharedtravel.app.components.user.validators.PasswordConstraint;
import eu.sharedtravel.app.components.user.validators.PasswordsMatchConstraint;
import eu.sharedtravel.app.components.user.validators.UserEmailFormatConstraint;
import eu.sharedtravel.app.components.user.validators.UserUniqueEmailConstraint;
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
public class RegisterDto {

    @Schema(description = "Email (username) used for the register", example = "long@email.com", required = true)
    @NotBlank
    @Size(max = 255)
    @UserUniqueEmailConstraint
    @UserEmailFormatConstraint
    private String email;

    @Schema(description = "Password for the register", example = "P@ssw0rd", required = true)
    @PasswordConstraint
    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @Schema(description = "Password confirmation used to validate the user has entered a correct password", example = "P@ssw0rd", required = true)
    @NotBlank
    @Size(min = 8, max = 128)
    private String passwordConfirmation;

    @Schema(description = "First name of the user", example = "Alexander", required = true)
    @NotBlank
    @Size(max = 25)
    private String firstName;

    @Schema(description = "Last name of the user", example = "Verbovskiy", required = true)
    @NotBlank
    @Size(max = 25)
    private String lastName;
}

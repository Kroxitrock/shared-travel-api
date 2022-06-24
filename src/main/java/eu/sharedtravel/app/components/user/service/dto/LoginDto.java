package eu.sharedtravel.app.components.user.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDto {

    @Schema(description = "Email (username) used for the login", example = "long@email.com", required = true)
    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    @Schema(description = "Password for the login", example = "12345678", required = true)
    @NotBlank
    @Size(min = 8, max = 128)
    private String password;
}

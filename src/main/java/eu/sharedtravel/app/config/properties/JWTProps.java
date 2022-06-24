package eu.sharedtravel.app.config.properties;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTProps {

    @NotBlank
    private String issuer;

    @NotBlank
    private String keyStorePath;

    @NotBlank
    private String alias;

    @NotBlank
    private String storePassword;

    @NotBlank
    private String keyPassword;
}

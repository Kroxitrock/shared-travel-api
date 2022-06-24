package eu.sharedtravel.app.config.properties;

import javax.validation.Valid;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * The {@code ApplicationProperties} class is used to hold the application properties taken from the application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Validated
public class ApplicationProperties {

    @Valid
    private final CorsProps cors = new CorsProps();

    @Valid
    private final JWTProps jwt = new JWTProps();

}

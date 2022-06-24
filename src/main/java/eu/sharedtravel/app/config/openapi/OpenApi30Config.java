package eu.sharedtravel.app.config.openapi;

import eu.sharedtravel.app.common.security.OptionalUser;
import eu.sharedtravel.app.common.security.ResolveUser;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "My API", version = "v1"))
@SecurityScheme(
    name = "JWT",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApi30Config {

    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(ResolveUser.class, OptionalUser.class);
    }
}

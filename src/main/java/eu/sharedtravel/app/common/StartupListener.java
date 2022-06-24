package eu.sharedtravel.app.common;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    private int port;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    @Value("${spring.profiles.active}")
    private String activeProfile;


    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        log.info("\n---------------------------------------------"
            + "\nWelcome to Shared Travel API"
            + "\nActive profile: " + activeProfile.toUpperCase()
            + "\nDefault path:   http://localhost:" + port
            + "\nSwagger UI:     http://localhost:" + port + swaggerPath
            + "\n---------------------------------------------"
        );
    }
}

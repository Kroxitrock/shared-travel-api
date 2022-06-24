package eu.sharedtravel.app.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.config.properties.ApplicationProperties;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTGenerator {

    private final Algorithm jwtEncodingAlgorithm;
    private final ApplicationProperties properties;

    /**
     * Generates a JWT for the supplied user.
     *
     * @param user is the security user for whom the JWT is created.
     * @return the hashed string of the JWT
     */
    public String generateJWT(User user) {
        return JWT.create()
            .withIssuer(properties.getJwt().getIssuer())
            .withSubject(user.getEmail())
            .withExpiresAt(
                Date.from(LocalDateTime.now().plus(8, ChronoUnit.HOURS).atZone(ZoneId.systemDefault()).toInstant()))
            .sign(jwtEncodingAlgorithm);
    }
}

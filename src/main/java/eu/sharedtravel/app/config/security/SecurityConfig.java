package eu.sharedtravel.app.config.security;

import eu.sharedtravel.app.components.user.service.UserService;
import eu.sharedtravel.app.config.properties.ApplicationProperties;
import eu.sharedtravel.app.config.security.filter.JwtTokenFilter;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Security configuration class Sets up the JWT authentication and security rules.
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final HttpMethod[] allowedMethods = {
        HttpMethod.POST, HttpMethod.GET,
        HttpMethod.GET, HttpMethod.PATCH,
        HttpMethod.DELETE, HttpMethod.OPTIONS
    };

    private final UserService userService;
    private final JwtTokenFilter jwtTokenFilter;

    /**
     * Configure user details service
     */
    @Override
    @SuppressWarnings("squid:S5344") // The security configuration uses BCrypt as we have a new bean for that
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    /**
     * Configure HTTP security for the project
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http.cors().and().csrf().disable();

        // Set session management to stateless
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Set unauthorized requests exception handler
        http
            .exceptionHandling()
            .authenticationEntryPoint(
                (request, response, ex) -> response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ex.getMessage()
                )
            );

        // Make every endpoint public as they will be secured using Method Security
        http.authorizeRequests()
            .antMatchers("/**").permitAll();

        // Add JWT token filter
        http.addFilterBefore(
            jwtTokenFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    }

    // Expose the authentication manager as it is not exposed by default
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS configuration. Enables PATCH method and applies it to each endpoint. Restricts allowed origins
     */
    @Bean
    public CorsFilter corsFilter(ApplicationProperties properties) {
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        Arrays.stream(properties.getCors().getAllowed()).forEach(config::addAllowedOrigin);
        config.addAllowedHeader("*");
        Arrays.stream(allowedMethods).forEach(config::addAllowedMethod);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

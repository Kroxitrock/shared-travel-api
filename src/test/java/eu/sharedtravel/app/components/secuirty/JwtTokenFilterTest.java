package eu.sharedtravel.app.components.secuirty;

import com.auth0.jwt.JWTVerifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.repository.UserRepository;
import eu.sharedtravel.app.components.user.repository.predicate.UserPredicates;
import eu.sharedtravel.app.config.properties.ApplicationProperties;
import eu.sharedtravel.app.config.security.JWTConfig;
import eu.sharedtravel.app.config.security.JWTGenerator;
import eu.sharedtravel.app.config.security.filter.JwtTokenFilter;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ApplicationProperties.class)
@SpringBootTest(classes = {JwtTokenFilter.class, JWTConfig.class, JWTGenerator.class})
class JwtTokenFilterTest {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private JWTGenerator jwtGenerator;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserPredicates userPredicates;

    @MockBean
    private ProfileService profileService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;
    private User user;
    private BooleanExpression mockExpression;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();

        user = UserTestMocks.mockNormalUser();
        mockExpression = Expressions.asBoolean(true);
    }

    @Test
    void givenAuthenticatedRequestSetUserDetails() throws ServletException, IOException {
        String userJWT = jwtGenerator.generateJWT(UserTestMocks.mockNormalUser());
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + userJWT);

        Mockito.when(userPredicates.forEmail(Mockito.anyString())).thenReturn(mockExpression);
        Mockito.when(userRepository.findOne(mockExpression)).thenReturn(Optional.of(user));

        jwtTokenFilter.doFilter(request, response, chain);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        Assertions.assertNotNull(securityContext.getAuthentication());
    }

    @Test
    void givenUnauthenticatedRequestDoNotSetUserDetails() throws ServletException, IOException {
        jwtTokenFilter.doFilter(request, response, chain);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        Assertions.assertNull(securityContext.getAuthentication());
    }


    @Test
    void givenBadAuthenticationInRequestDoNotSetUserDetails() throws ServletException, IOException {
        String userJWT = "wrongjwt";

        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + userJWT);
        jwtTokenFilter.doFilter(request, response, chain);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        Assertions.assertNull(securityContext.getAuthentication());
    }

}

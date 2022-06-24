package eu.sharedtravel.app.components.user.service;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.repository.UserRepository;
import eu.sharedtravel.app.components.user.repository.predicate.UserPredicates;
import eu.sharedtravel.app.config.properties.ApplicationProperties;
import eu.sharedtravel.app.config.security.JWTConfig;
import eu.sharedtravel.app.config.security.JWTGenerator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ApplicationProperties.class)
@SpringBootTest(classes = {UserService.class, JWTConfig.class, JWTGenerator.class})
class UserServiceTest {

    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private JWTVerifier jwtVerifier;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationProperties properties;
    @Autowired
    private Algorithm jwtEncodingAlgorithm;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserPredicates userPredicates;

    @MockBean
    private ProfileService profileService;

    private User user;
    private BooleanExpression mockExpression;


    @BeforeEach
    public void setUp() {
        user = UserTestMocks.mockNormalUser();
        mockExpression = Expressions.asBoolean(true);
    }

    @Test
    void givenUsernameShouldReturnUserWithSaidUsername() {
        Mockito.when(userPredicates.forEmail(UserTestConstants.USER_EMAIL)).thenReturn(mockExpression);
        Mockito.when(userRepository.findOne(mockExpression)).thenReturn(Optional.of(user));

        UserDetails output = userService.loadUserByUsername(UserTestConstants.USER_EMAIL);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(output.getUsername(), user.getUsername());
    }


    @Test
    void givenWrongUsernameShouldThrowUsernameNotFoundException() {
        Mockito.when(userPredicates.forEmail(UserTestConstants.USER_EMAIL)).thenReturn(mockExpression);
        Mockito.when(userRepository.findOne(mockExpression)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(UserTestConstants.USER_EMAIL));
    }

    @Test
    void givenCorrectUserShouldReturnTrue() {
        Mockito.when(userPredicates.forEmail(user.getEmail())).thenReturn(mockExpression);
        Mockito.when(userRepository.exists(mockExpression)).thenReturn(true);

        Assertions.assertTrue(userService.userExists(user.getEmail()));
    }

    @Test
    void givenWrongUserShouldReturnFalse() {
        Mockito.when(userPredicates.forEmail(user.getEmail())).thenReturn(mockExpression);
        Mockito.when(userRepository.exists(mockExpression)).thenReturn(false);

        Assertions.assertFalse(userService.userExists(user.getEmail()));
    }

    @Test
    void givenRegisterDtoShouldCreateUser() {
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(I -> {
            User argumentUser = I.getArgument(0);
            argumentUser.setId(UserTestConstants.USER_ID);
            return argumentUser;
        });

        String response = userService.register(UserTestMocks.mockRegisterDto());

        Mockito.verify(profileService).createProfileForUser(Mockito.eq(user), Mockito.any(), Mockito.any());

        Assertions.assertEquals(3, response.split("\\.").length);
    }

    @Test
    void givenUserShouldGenerateJWT() {
        String output = jwtGenerator.generateJWT(user);

        Assertions.assertTrue(StringUtils.isNotBlank(output));
        Assertions.assertNotNull(jwtVerifier.verify(output));
    }

    @Test
    void givenChangePasswordDtoShouldChangePassword() {
        var user = UserTestMocks.mockNormalUser();
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(I -> I.getArgument(0));

        userService.changePassword(UserTestMocks.mockChangePasswordDto(), user);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(UserTestConstants.NEW_PASSWORD, user.getPassword());
    }
}

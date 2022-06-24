package eu.sharedtravel.app.components.user.controller;

import static eu.sharedtravel.app.common.Constants.BEARER_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sharedtravel.app.components.profile.ProfileTestConstants;
import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.service.UserService;
import eu.sharedtravel.app.components.user.service.dto.ChangePasswordDto;
import eu.sharedtravel.app.components.user.service.dto.LoginDto;
import eu.sharedtravel.app.components.user.service.dto.RegisterDto;
import eu.sharedtravel.app.config.security.JWTGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTGenerator jwtGenerator;

    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void givenValidLoginRequestShouldReturnJwtString() throws Exception {
        LoginDto loginRequest = LoginDto.builder()
            .email(UserTestConstants.USER_EMAIL)
            .password(UserTestConstants.PASSWORD)
            .build();

        User user = UserTestMocks.mockNormalUser();
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        mockMvc
            .perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void givenValidRegisterDtoShouldRegister() throws Exception {
        RegisterDto registerDto = UserTestMocks.mockRegisterDto();

        Mockito.when(userService.register(Mockito.any())).thenReturn(UserTestConstants.JWT);

        mockMvc
            .perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is(UserTestConstants.JWT)));
    }

    @Test
    void givenEmailAlreadyTakenShouldNotRegister() throws Exception {
        RegisterDto registerDto = UserTestMocks.mockRegisterDto();

        Mockito.when(userService.userExists(registerDto.getEmail())).thenReturn(true);

        mockMvc
            .perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenPasswordMismatchedShouldNotRegister() throws Exception {
        RegisterDto registerDto = UserTestMocks.mockRegisterDto();
        registerDto.setPasswordConfirmation(registerDto.getPassword() + "2");

        mockMvc
            .perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenPasswordTooSimpleShouldNotRegister() throws Exception {
        RegisterDto registerDto = RegisterDto.builder()
            .email(UserTestConstants.USER_EMAIL)
            .password("12345678")
            .passwordConfirmation("12345678")
            .firstName(ProfileTestConstants.FIRST_NAME)
            .lastName(ProfileTestConstants.LAST_NAME)
            .build();

        mockMvc
            .perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidChangePasswordShouldChangePassword() throws Exception {
        ChangePasswordDto changePasswordDto = UserTestMocks.mockChangePasswordDto();

        Mockito.doNothing().when(userService).changePassword(changePasswordDto, UserTestMocks.mockNormalUser());

        mockMvc
            .perform(patch("/users/me/password")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDto)))
            .andExpect(status().isOk());
    }

    @Test
    void givenInvalidChangePasswordShouldReturnUnauthorized() throws Exception {
        ChangePasswordDto changePasswordDto = UserTestMocks.mockChangePasswordDto();
        changePasswordDto.setOldPassword("P@ssw0rd-wrong");

        mockMvc
            .perform(patch("/users/me/password")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void givenSameChangePasswordShouldReturnUnauthorized() throws Exception {
        ChangePasswordDto changePasswordDto = UserTestMocks.mockChangePasswordDto();
        changePasswordDto.setOldPassword(UserTestConstants.NEW_PASSWORD);

        mockMvc
            .perform(patch("/users/me/password")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDto)))
            .andExpect(status().isUnauthorized());
    }
}

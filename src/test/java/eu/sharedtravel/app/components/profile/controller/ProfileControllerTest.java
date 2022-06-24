package eu.sharedtravel.app.components.profile.controller;

import static eu.sharedtravel.app.common.Constants.BEARER_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sharedtravel.app.components.profile.ProfileTestConstants;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.profile.service.dto.ProfilePatchInputDto;
import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.config.security.JWTGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("squid:S2699")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTGenerator jwtGenerator;

    private Profile profile;

    @BeforeEach
    public void setUp() {
        profile = ProfileTestMocks.mockUserProfile();
    }

    @Test
    void whenGettingMyProfileShouldReturnProfile() throws Exception {
        Mockito.when(profileService.getProfile(Mockito.any(User.class))).thenReturn(profile);

        mockMvc.perform(get("/profiles/me")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(ProfileTestConstants.ID.intValue())))
            .andExpect(jsonPath("$.email", Matchers.is(UserTestConstants.USER_EMAIL)));
    }

    @Test
    void givenIdShouldReturnUserWithThatIdAndShouldMap() throws Exception {
        Mockito.when(profileService.getProfile(Mockito.anyLong())).thenReturn(profile);

        mockMvc.perform(get("/profiles/" + profile.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(ProfileTestConstants.ID.intValue())))
            .andExpect(jsonPath("$.email", Matchers.is(UserTestConstants.USER_EMAIL)));
    }

    @Test
    void givenIdShouldUpdatePassedFieldsAndReturnUserWithThatIdAndShouldMap() throws Exception {
        ProfilePatchInputDto dto = new ProfilePatchInputDto();
        dto.setFirstName(ProfileTestConstants.FIRST_NAME + "2");

        Mockito.when(profileService.patchUpdateProfile(Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
            ProfilePatchInputDto inputDto = invocation.getArgument(1);
            profile.setFirstName(inputDto.getFirstName());
            return profile;
        });

        mockMvc.perform(patch("/profiles/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(ProfileTestConstants.ID.intValue())))
            .andExpect(jsonPath("$.firstName", Matchers.is(profile.getFirstName())));
    }
}

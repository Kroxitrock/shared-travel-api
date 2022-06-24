package eu.sharedtravel.app.components.profilesettings.controller;

import static eu.sharedtravel.app.common.Constants.BEARER_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestConstants;
import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestMocks;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.service.ProfileSettingsService;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsPatchInputDto;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsOutputDtoMapper;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsOutputDtoMapperImpl;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.config.security.JWTGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProfileSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileSettingsService profileSettingsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTGenerator jwtGenerator;

    private ProfileSettings profileSettings;

    @BeforeEach
    public void setUp() {
        profileSettings = ProfileSettingsTestMocks.mockProfileSettings();
    }

    @Test
    void givenIdShouldReturnProfileSettingsWithThatIdAndShouldMap() throws Exception {
        Mockito.when(profileSettingsService.getProfileSettings(Mockito.anyLong())).thenReturn(profileSettings);

        mockMvc.perform(get("/profiles/me/settings/")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(ProfileSettingsTestConstants.USER_ID.intValue())))
            .andExpect(jsonPath("$.emailVisible", Matchers.is(ProfileSettingsTestConstants.USER_EMAIL_VISIBLE)));
    }

    @Test
    void givenIdShouldUpdatePassedFieldsAndReturnProfileSettingsWithThatIdAndShouldMap() throws Exception {
        ProfileSettingsPatchInputDto dto = new ProfileSettingsPatchInputDto();
        dto.setEmailVisible(false);

        Mockito.when(profileSettingsService.updateProfileSettingsPatch(Mockito.anyLong(), Mockito.any()))
            .thenAnswer(I -> {
                profileSettings.setEmailVisible(false);
                return profileSettings;
            });

        mockMvc.perform(patch("/profiles/me/settings/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockNormalUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(ProfileSettingsTestConstants.USER_ID.intValue())))
            .andExpect(jsonPath("$.emailVisible", Matchers.is(false)));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ProfileSettingsOutputDtoMapper profileSettingsOutputDtoMapper() {
            return new ProfileSettingsOutputDtoMapperImpl();
        }
    }
}

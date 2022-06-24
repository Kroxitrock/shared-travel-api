package eu.sharedtravel.app.components.profilesettings.service;

import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestConstants;
import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestMocks;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.repository.ProfileSettingsRepository;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsPatchInputDto;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsPatchInputDtoMapper;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsPatchInputDtoMapperImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@ExtendWith(MockitoExtension.class)
public class ProfileSettingsServiceTest {

    ProfileSettings profileSettings;
    @Mock
    private ProfileSettingsRepository profileSettingsRepository;
    @Mock
    private ProfileSettingsPatchInputDtoMapper profileSettingsPatchInputDtoMapper;
    @Autowired
    @InjectMocks
    private ProfileSettingsService profileSettingsService;

    @BeforeEach
    public void setUp() {
        profileSettings = ProfileSettingsTestMocks.mockProfileSettings();
    }

    @Test
    void shouldFindProfileSettingsByIdAndReturnIt() {
        Mockito.when(profileSettingsRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(profileSettings));

        ProfileSettings fetchedProfileSettings = profileSettingsService.getProfileSettings(
            ProfileSettingsTestConstants.USER_ID);

        Assertions.assertEquals(profileSettings.getId(), fetchedProfileSettings.getId());
        Assertions.assertEquals(profileSettings.getEmailVisible(), fetchedProfileSettings.getEmailVisible());
    }

    @Test
    void shouldPatchUpdateProfileSettingsByIdAndReturnIt() {
        ProfileSettingsPatchInputDto dto = new ProfileSettingsPatchInputDto();
        dto.setEmailVisible(false);

        Mockito.when(profileSettingsRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(profileSettings));
        Mockito.when(profileSettingsRepository.save(Mockito.any()))
            .thenAnswer(I -> {
                profileSettings.setEmailVisible(false);
                return profileSettings;
            });

        ProfileSettings updatedProfileSettings =
            profileSettingsService.updateProfileSettingsPatch(profileSettings.getId(), dto);

        Assertions.assertEquals(profileSettings.getId(), updatedProfileSettings.getId());
        Assertions.assertEquals(false, updatedProfileSettings.getEmailVisible());
        Assertions.assertEquals(dto.getEmailVisible(), updatedProfileSettings.getEmailVisible());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ProfileSettingsPatchInputDtoMapper profileSettingsPatchInputDtoMapper() {
            return new ProfileSettingsPatchInputDtoMapperImpl();
        }
    }
}

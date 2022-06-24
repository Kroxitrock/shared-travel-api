package eu.sharedtravel.app.components.profilesettings.repository;

import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestConstants;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProfileSettingsRepositoryTest {

    @Autowired
    private ProfileSettingsRepository profileSettingsRepository;

    private ProfileSettings profileSettings;

    @BeforeEach
    public void setUp() {
        profileSettings = new ProfileSettings();
        profileSettings.setId(ProfileSettingsTestConstants.USER_ID);

        profileSettingsRepository.save(profileSettings);
    }

    @Test
    void givenProfileSettingsToAddShouldReturnIt() {
        Assertions.assertNotNull(profileSettings.getId());
    }

    @Test
    void givenIdThenShouldReturnProfileSettingsWithThatId() {
        ProfileSettings fetchedProfileSettings = profileSettingsRepository.getById(profileSettings.getId());

        Assertions.assertEquals(profileSettings.getId(), fetchedProfileSettings.getId());
    }
}

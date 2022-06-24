package eu.sharedtravel.app.components.profilesettings;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;

public class ProfileSettingsTestMocks {

    public static ProfileSettings mockProfileSettings() {
        ProfileSettings profileSettings = new ProfileSettings();
        profileSettings.setId(ProfileSettingsTestConstants.USER_ID);

        return profileSettings;
    }
}

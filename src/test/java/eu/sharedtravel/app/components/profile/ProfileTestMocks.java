package eu.sharedtravel.app.components.profile;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profile.service.dto.ProfileOutputDto;
import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestConstants;
import eu.sharedtravel.app.components.profilesettings.ProfileSettingsTestMocks;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsDto;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsOutputDto;
import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.model.UserAuthority;
import java.util.HashSet;
import java.util.Set;

public class ProfileTestMocks {

    public static Profile mockUserProfile() {
        Profile profile = new Profile();
        profile.setId(ProfileTestConstants.ID);
        profile.setFirstName(ProfileTestConstants.FIRST_NAME);
        profile.setLastName(ProfileTestConstants.LAST_NAME);

        profile.setUser(UserTestMocks.mockNormalUser());

        ProfileSettings profileSettings = ProfileSettingsTestMocks.mockProfileSettings();

        profile.setProfileSettings(profileSettings);

        return profile;
    }

    public static Profile mockDriverProfile() {
        Profile profile = new Profile();
        profile.setId(ProfileTestConstants.DRIVER_ID);
        profile.setFirstName(ProfileTestConstants.FIRST_NAME);
        profile.setLastName(ProfileTestConstants.LAST_NAME);

        profile.setUser(UserTestMocks.mockDriverUser());

        ProfileSettings profileSettings = ProfileSettingsTestMocks.mockProfileSettings();

        profile.setProfileSettings(profileSettings);

        return profile;
    }

    public static ProfileDto mockUserProfileDto() {
        ProfileDto profile = new ProfileDto();
        profile.setId(ProfileTestConstants.ID);
        profile.setFirstName(ProfileTestConstants.FIRST_NAME);
        profile.setLastName(ProfileTestConstants.LAST_NAME);

        profile.setUserId(UserTestConstants.USER_ID);
        profile.setEmail(UserTestConstants.USER_EMAIL);
        profile.setPassword(UserTestConstants.PASSWORD);

        ProfileSettingsDto profileSettingsDto = new ProfileSettingsDto();
        profileSettingsDto.setId(ProfileSettingsTestConstants.USER_ID);
        profileSettingsDto.setEmailVisible(ProfileSettingsTestConstants.USER_EMAIL_VISIBLE);
        profile.setProfileSettings(profileSettingsDto);

        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(UserAuthority.USER);

        profile.setAuthorities(authoritySet);

        return profile;
    }

    public static ProfileDto mockDriverProfileDto() {
        ProfileDto profile = new ProfileDto();
        profile.setId(ProfileTestConstants.DRIVER_ID);
        profile.setFirstName(ProfileTestConstants.DRIVER_FIRST_NAME);
        profile.setLastName(ProfileTestConstants.DRIVER_LAST_NAME);

        profile.setUserId(UserTestConstants.DRIVER_USER_ID);
        profile.setEmail(UserTestConstants.DRIVER_EMAIL);
        profile.setPassword(UserTestConstants.PASSWORD);

        ProfileSettingsDto profileSettingsDto = new ProfileSettingsDto();
        profileSettingsDto.setId(ProfileSettingsTestConstants.DRIVER_ID);
        profileSettingsDto.setEmailVisible(ProfileSettingsTestConstants.DRIVER_EMAIL_VISIBLE);
        profile.setProfileSettings(profileSettingsDto);

        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(UserAuthority.USER);
        authoritySet.add(UserAuthority.DRIVER);

        profile.setAuthorities(authoritySet);

        return profile;
    }

    public static ProfileOutputDto mockProfileOutputDto() {
        return ProfileOutputDto.builder()
            .id(ProfileTestConstants.ID)
            .firstName(ProfileTestConstants.FIRST_NAME)
            .lastName(UserTestConstants.USER_EMAIL)
            .profileSettings(
                ProfileSettingsOutputDto.builder()
                    .id(ProfileSettingsTestConstants.USER_ID)
                    .emailVisible(ProfileSettingsTestConstants.USER_EMAIL_VISIBLE)
                    .build()
            ).build();
    }

    public static ProfileOutputDto mockDriverProfileOutputDto() {
        return ProfileOutputDto.builder()
            .id(ProfileTestConstants.DRIVER_ID)
            .firstName(ProfileTestConstants.DRIVER_FIRST_NAME)
            .lastName(UserTestConstants.DRIVER_EMAIL)
            .profileSettings(
                ProfileSettingsOutputDto.builder()
                    .id(ProfileSettingsTestConstants.DRIVER_ID)
                    .emailVisible(ProfileSettingsTestConstants.DRIVER_EMAIL_VISIBLE)
                    .build()
            ).build();
    }
}

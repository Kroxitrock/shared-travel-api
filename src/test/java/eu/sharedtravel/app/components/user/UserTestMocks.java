package eu.sharedtravel.app.components.user;

import eu.sharedtravel.app.components.profile.ProfileTestConstants;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.model.UserAuthority;
import eu.sharedtravel.app.components.user.service.dto.ChangePasswordDto;
import eu.sharedtravel.app.components.user.service.dto.RegisterDto;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserTestMocks {

    public static User mockNormalUser() {

        User user = new User();
        user.setId(UserTestConstants.USER_ID);
        user.setEmail(UserTestConstants.USER_EMAIL);
        user.setPassword(UserTestConstants.PASSWORD);

        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(UserAuthority.USER);
        user.setAuthorities(authoritySet);

        return user;
    }

    public static User mockDriverUser() {

        User user = new User();
        user.setId(UserTestConstants.DRIVER_USER_ID);
        user.setEmail(UserTestConstants.DRIVER_EMAIL);
        user.setPassword(UserTestConstants.PASSWORD);

        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(UserAuthority.USER);
        authoritySet.add(UserAuthority.DRIVER);
        user.setAuthorities(authoritySet);

        return user;
    }

    public static UserDto mockUserDto() {
        return UserDto.builder()
            .id(UserTestConstants.USER_ID)
            .email(UserTestConstants.USER_EMAIL)
            .password(UserTestConstants.PASSWORD)
            .authorities(new HashSet<>(Collections.singletonList(UserAuthority.USER)))
            .build();
    }

    public static UserDto mockDriverUserDto() {
        return UserDto.builder()
            .id(UserTestConstants.DRIVER_USER_ID)
            .email(UserTestConstants.DRIVER_EMAIL)
            .password(UserTestConstants.PASSWORD)
            .authorities(new HashSet<>(Arrays.asList(UserAuthority.USER, UserAuthority.DRIVER)))
            .build();
    }

    public static RegisterDto mockRegisterDto() {
        return RegisterDto.builder()
            .email(UserTestConstants.USER_EMAIL)
            .password(UserTestConstants.PASSWORD)
            .passwordConfirmation(UserTestConstants.PASSWORD)
            .firstName(ProfileTestConstants.FIRST_NAME)
            .lastName(ProfileTestConstants.LAST_NAME)
            .build();
    }

    public static ChangePasswordDto mockChangePasswordDto() {
        return ChangePasswordDto.builder()
            .oldPassword("P@ssw0rd")
            .password(UserTestConstants.NEW_PASSWORD)
            .passwordConfirmation(UserTestConstants.NEW_PASSWORD)
            .build();
    }
}

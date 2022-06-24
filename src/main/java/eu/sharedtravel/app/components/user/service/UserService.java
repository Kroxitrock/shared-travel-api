package eu.sharedtravel.app.components.user.service;

import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.model.UserAuthority;
import eu.sharedtravel.app.components.user.repository.UserRepository;
import eu.sharedtravel.app.components.user.repository.predicate.UserPredicates;
import eu.sharedtravel.app.components.user.service.dto.ChangePasswordDto;
import eu.sharedtravel.app.components.user.service.dto.RegisterDto;
import eu.sharedtravel.app.config.security.JWTGenerator;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Security User service used for authenticating users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPredicates userPredicates;

    private final ProfileService profileService;

    private final JWTGenerator jwtGenerator;

    /**
     * Loads a security user by username which in this case is the email.
     *
     * @param email username of the user in the form of their email.
     * @return Authenticated user.
     * @throws UsernameNotFoundException - In case user is missing from the database.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
            .findOne(userPredicates.forEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found!", email)));
    }

    public boolean userExists(String email) {
        return userRepository.exists(userPredicates.forEmail(email));
    }

    /**
     * Creates a user and a matching profile in the database based on the input
     *
     * @param registerDto data for the user to be registered
     * @return JWT generated for the new user
     */
    @Transactional
    public String register(RegisterDto registerDto) {
        log.trace("Registering user {}...", registerDto.getEmail());
        User user = User.builder()
            .email(registerDto.getEmail())
            .password(registerDto.getPassword())
            .authorities(Collections.singleton(UserAuthority.USER))
            .build();

        userRepository.save(user);
        profileService.createProfileForUser(user, registerDto.getFirstName(), registerDto.getLastName());

        log.trace("User {} successfully registered!", user);
        return jwtGenerator.generateJWT(user);
    }

    /**
     * Updates the user's password if provided old password is correct.
     *
     * @param changePasswordDto data for the user to change password
     * @param user              The user to be updated
     */
    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto, User user) {
        user.setPassword(changePasswordDto.getPassword());

        log.trace("User {} successfully changed password!", user);
        userRepository.save(user);
    }
}

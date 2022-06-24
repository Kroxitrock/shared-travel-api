package eu.sharedtravel.app.components.profile.repository;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.repository.predicate.ProfilePredicates;
import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilePredicates profilePredicates;

    @Test
    void givenValidUserShouldReturnProfile() {
        User user = userRepository.getById(UserTestConstants.USER_ID);

        Optional<Profile> profile = profileRepository.findOne(profilePredicates.forUser(user));

        Assertions.assertTrue(profile.isPresent());
        Assertions.assertEquals(profile.get().getUser().getId(), user.getId());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ProfilePredicates profilePredicates() {
            return new ProfilePredicates();
        }
    }
}

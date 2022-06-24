package eu.sharedtravel.app.components.user.repository;


import eu.sharedtravel.app.components.user.UserTestConstants;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.repository.predicate.UserPredicates;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPredicates userPredicates;

    @Test
    void givenCorrectEmailShouldReturnUser() {
        String email = UserTestConstants.USER_EMAIL;

        Optional<User> output = userRepository.findOne(userPredicates.forEmail(email));

        Assertions.assertTrue(output.isPresent());
    }

    @Test
    void givenWrongEmailShouldReturnEmpty() {
        String email = "wrong@email.com";

        Optional<User> output = userRepository.findOne(userPredicates.forEmail(email));

        Assertions.assertFalse(output.isPresent());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserPredicates userPredicates() {
            return new UserPredicates();
        }
    }
}

package eu.sharedtravel.app.components.notification.repository;

import eu.sharedtravel.app.components.notification.NotificationTestConstants;
import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.notification.repository.predicate.NotificationPredicates;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.repository.ProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPredicates notificationPredicates;

    @Autowired
    private ProfileRepository profileRepository;

    private Notification notification;

    private Profile driver;

    @BeforeEach
    public void setUp() {
        driver = profileRepository.findById(1L).orElse(null);

        notification = new Notification();
        notification.setNotifiedPerson(driver);
        notification.setMessageData(NotificationTestConstants.MESSAGE_DATA);
        notification.setType(NotificationTestConstants.TYPE);
        notification.setRead(true);

        notificationRepository.save(notification);
    }

    @Test
    void givenNotificationToAddShouldReturnIt() {
        Assertions.assertNotNull(notification.getId());
    }

    @Test
    void givenIdThenShouldReturnNotificationWithThatId() {
        var fetchedNotification = notificationRepository.getById(notification.getId());

        Assertions.assertEquals(notification.getId(), fetchedNotification.getId());
    }

    @Test
    void givenProfileShouldReturnPagedNotificationsForThatProfile() {
        var predicate = notificationPredicates.forProfile(driver);
        var fetchedPage = notificationRepository.findAll(predicate, Pageable.ofSize(10));

        Assertions.assertNotNull(fetchedPage);
        Assertions.assertEquals(1, fetchedPage.getContent().size());
        Assertions.assertEquals(notification.getId(), fetchedPage.getContent().get(0).getId());
    }

    @Test
    void givenProfileAndNotReadShouldReturnEmptyPageNotificationsForThatProfileAndNotRead() {
        var predicate = notificationPredicates.forProfileAndNotRead(driver);
        var fetchedPage = notificationRepository.findAll(predicate, Pageable.ofSize(10));

        Assertions.assertNotNull(fetchedPage);
        Assertions.assertEquals(0, fetchedPage.getContent().size());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public NotificationPredicates notificationPredicates() {
            return new NotificationPredicates();
        }
    }
}

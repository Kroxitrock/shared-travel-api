package eu.sharedtravel.app.components.notification.repository.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.sharedtravel.app.components.notification.model.QNotification;
import eu.sharedtravel.app.components.profile.model.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class NotificationPredicates {

    private final QNotification qNotification;

    public NotificationPredicates() {
        this.qNotification = QNotification.notification;
    }

    public BooleanExpression forProfile(Profile profile) {
        return qNotification.notifiedPerson.eq(profile);
    }

    public BooleanExpression forProfileAndNotRead(Profile profile) {
        return forProfile(profile).and(forNotRead());
    }

    public BooleanExpression forId(Long id) {
        return qNotification.id.eq(id);
    }

    public Sort defaultSort() {
        return Sort.by("id").descending();
    }

    private BooleanExpression forNotRead() {
        return qNotification.read.eq(false);
    }
}

package eu.sharedtravel.app.components.profile.repository.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.sharedtravel.app.components.profile.model.QProfile;
import eu.sharedtravel.app.components.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class ProfilePredicates {

    private final QProfile qProfile;

    public ProfilePredicates() {
        this.qProfile = QProfile.profile;
    }

    public BooleanExpression forUser(User user) {
        return qProfile.user.eq(user);
    }
}

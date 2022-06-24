package eu.sharedtravel.app.components.user.repository.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.sharedtravel.app.components.user.model.QUser;
import org.springframework.stereotype.Component;

@Component
public class UserPredicates {

    private final QUser qUser;

    public UserPredicates() {
        qUser = QUser.user;
    }

    public BooleanExpression forEmail(String email) {
        return qUser.email.eq(email);
    }
}

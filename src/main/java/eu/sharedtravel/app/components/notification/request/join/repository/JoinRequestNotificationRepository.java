package eu.sharedtravel.app.components.notification.request.join.repository;

import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinRequestNotificationRepository extends JpaRepository<JoinRequestNotification, Long>,
    QuerydslPredicateExecutor<JoinRequestNotification> {

}

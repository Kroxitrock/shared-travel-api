package eu.sharedtravel.app.components.notification.repository;

import eu.sharedtravel.app.components.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
    QuerydslPredicateExecutor<Notification> {

}

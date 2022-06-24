package eu.sharedtravel.app.components.notification.request.join.repository.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestStatus;
import eu.sharedtravel.app.components.notification.request.join.model.QJoinRequestNotification;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.model.Travel;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JoinRequestNotificationPredicates {

    private final QJoinRequestNotification qJoinRequestNotification;

    public JoinRequestNotificationPredicates() {
        this.qJoinRequestNotification = QJoinRequestNotification.joinRequestNotification;
    }

    public BooleanExpression forUserAndInTravelList(String email, List<Long> travelIds) {
        return forTravelInList(travelIds)
            .and(forUserWithEmail(email))
            .and(forJoinRequestPending());
    }

    private BooleanExpression forTravelInList(List<Long> travelIds) {
        return qJoinRequestNotification.travel.id.in(travelIds);
    }

    private BooleanExpression forUserWithEmail(String email) {
        return qJoinRequestNotification.passenger.user.email.eq(email);
    }

    public BooleanExpression forPendingAndTravelIdAndPassengerId(Long travelId, Long passengerId) {
        return forJoinRequestPending()
            .and(forTravelId(travelId))
            .and(forPassengerId(passengerId));
    }

    public BooleanExpression forPassengerOrDriverOfTravel(Profile passenger, Travel travel) {
        return forTravel(travel)
            .and(forPassengerOrDriver(passenger))
            .and(forJoinRequestPending());
    }

    public BooleanExpression forActiveForTravel(Travel travel) {
        return forTravel(travel)
            .and(forJoinRequestPending());
    }

    private BooleanExpression forTravel(Travel travel) {
        return qJoinRequestNotification.travel.eq(travel);
    }

    private BooleanExpression forTravelId(Long travelId) {
        return qJoinRequestNotification.travel.id.eq(travelId);
    }

    private BooleanExpression forPassengerOrDriver(Profile profile) {
        return forPassenger(profile)
            .or(forDriver(profile));
    }

    private BooleanExpression forPassengerId(Long passengerId) {
        return qJoinRequestNotification.passenger.id.eq(passengerId);
    }

    private BooleanExpression forPassenger(Profile profile) {
        return qJoinRequestNotification.passenger.eq(profile);
    }

    private BooleanExpression forDriver(Profile profile) {
        return qJoinRequestNotification.notifiedPerson.eq(profile);
    }

    public BooleanExpression forActiveForId(Long id) {
        return qJoinRequestNotification.id.eq(id)
            .and(forJoinRequestPending());
    }

    private BooleanExpression forJoinRequestPending() {
        return qJoinRequestNotification.status.eq(JoinRequestStatus.PENDING);
    }
}

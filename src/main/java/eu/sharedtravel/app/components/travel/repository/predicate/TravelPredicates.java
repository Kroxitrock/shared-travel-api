package eu.sharedtravel.app.components.travel.repository.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.model.QTravel;
import eu.sharedtravel.app.components.travel.model.TravelStatus;
import eu.sharedtravel.app.components.travel.service.dto.TravelFilterDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class TravelPredicates {

    private final QTravel qTravel;

    public TravelPredicates() {
        this.qTravel = QTravel.travel;
    }


    public BooleanExpression forTravelWithIdActive(Long id) {
        return forId(id)
            .and(forTravelActive());
    }

    public BooleanExpression forId(Long id) {
        return qTravel.id.eq(id);
    }

    public BooleanExpression forIdAndDriverId(Long id, Long driverId) {
        return forId(id).and(forDriverId(driverId));
    }

    public BooleanExpression forDriverIdOrAndPassengerAndFutureOrPast(Profile profile, Boolean driverOnly,
        Boolean inFuture) {
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression expression = forDriverIdOrAndPassenger(profile, driverOnly);

        return Boolean.TRUE.equals(inFuture) ? expression.and(forAfterDate(now)) : expression.and(forBeforeDate(now));
    }

    public BooleanExpression forDriverIdOrAndPassenger(Profile profile, Boolean driverOnly) {
        return Boolean.TRUE.equals(driverOnly) ? forDriverId(profile.getId())
            : forDriverId(profile.getId()).or(forPassenger(profile));
    }

    public BooleanExpression forPassenger(Profile passenger) {
        return qTravel.passengers.contains(passenger);
    }

    public BooleanExpression forDriverId(Long driverId) {
        return qTravel.driver.id.eq(driverId);
    }

    public BooleanExpression forFromAndToAndAfterDateAndNotCanceled(TravelFilterDto dto) {
        return forFrom(dto.getFrom())
            .and(forTo(dto.getTo()))
            .and(forAfterDate(dto.getDepartureDate()))
            .and(forStatusNotCanceled());
    }

    public BooleanExpression forActiveInTimeFrame(LocalDateTime from, LocalDateTime to) {
        return forTravelActive()
            .and(qTravel.departureDate.goe(from))
            .and(qTravel.departureDate.loe(to));
    }

    private BooleanExpression forAfterDate(LocalDateTime date) {
        return qTravel.departureDate.after(date);
    }

    private BooleanExpression forBeforeDate(LocalDateTime date) {
        return qTravel.departureDate.before(date);
    }

    private BooleanExpression forTo(String code) {
        return qTravel.to.code.eq(code);
    }

    private BooleanExpression forFrom(String code) {
        return qTravel.from.code.eq(code);
    }

    private BooleanExpression forStatusNotCanceled() {
        return qTravel.status.ne(TravelStatus.CANCELED);
    }

    private BooleanExpression forTravelActive() {
        return qTravel.status.eq(TravelStatus.PENDING)
            .and(qTravel.departureDate.goe(LocalDateTime.now()));
    }

    public Sort defaultSort() {
        return Sort.by("departureDate", "id");
    }
}

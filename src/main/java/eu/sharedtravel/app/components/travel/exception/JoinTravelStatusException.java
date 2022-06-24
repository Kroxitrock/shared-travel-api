package eu.sharedtravel.app.components.travel.exception;

import eu.sharedtravel.app.components.travel.model.TravelStatus;

/**
 * Exception thrown when the user requests to join a travel that has status different from PENDING
 */
public class JoinTravelStatusException extends RuntimeException {

    private static final String MESSAGE = "The logged in user with id %d can not request to join travel %d, due to status not being PENDING: %s!";

    public JoinTravelStatusException(Long userId, Long travelId, TravelStatus travelStatus) {
        super(String.format(MESSAGE, userId, travelId, travelStatus));
    }
}

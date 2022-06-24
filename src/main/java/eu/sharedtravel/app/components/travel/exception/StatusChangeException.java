package eu.sharedtravel.app.components.travel.exception;

import eu.sharedtravel.app.components.travel.model.TravelStatus;

/**
 * Exception thrown when the user tries to change the travel status to a status they have no right to
 */
public class StatusChangeException extends RuntimeException {

    private static final String MESSAGE = "The logged in user with id %d has no authority to change the status of travel %d to %s!";

    public StatusChangeException(Long userId, Long travelId, TravelStatus travelStatus) {
        super(String.format(MESSAGE, userId, travelId, travelStatus));
    }
}

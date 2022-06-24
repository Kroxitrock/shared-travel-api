package eu.sharedtravel.app.components.travel.exception;

import java.time.LocalDateTime;

/**
 * Exception thrown when the user requests to join a travel that has departure date in the past
 */
public class JoinTravelDateException extends RuntimeException {

    private static final String MESSAGE = "The logged in user with id %d can not request to join travel %d, due to departure date being in the past: %s!";

    public JoinTravelDateException(Long userId, Long travelId, LocalDateTime localDateTime) {
        super(String.format(MESSAGE, userId, travelId, localDateTime.toString()));
    }
}

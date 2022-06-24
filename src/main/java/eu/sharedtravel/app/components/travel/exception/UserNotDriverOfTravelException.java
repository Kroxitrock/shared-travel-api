package eu.sharedtravel.app.components.travel.exception;

public class UserNotDriverOfTravelException extends RuntimeException {

    private static final String MESSAGE = "User with id %d is not a driver of travel with id %d!";

    public UserNotDriverOfTravelException(Long driverId, Long travelId) {
        super(String.format(MESSAGE, driverId, travelId));
    }

}

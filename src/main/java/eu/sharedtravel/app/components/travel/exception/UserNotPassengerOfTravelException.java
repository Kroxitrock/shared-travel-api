package eu.sharedtravel.app.components.travel.exception;

public class UserNotPassengerOfTravelException extends RuntimeException {

    private static final String MESSAGE = "User with id %d is not a passenger of travel with id %d!";

    public UserNotPassengerOfTravelException(Long passengerId, Long travelId) {
        super(String.format(MESSAGE, passengerId, travelId));
    }
}

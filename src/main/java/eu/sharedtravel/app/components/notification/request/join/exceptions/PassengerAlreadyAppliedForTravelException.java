package eu.sharedtravel.app.components.notification.request.join.exceptions;

public class PassengerAlreadyAppliedForTravelException extends RuntimeException {

    private static final String MESSAGE = "Passenger with id %d has already applied for travel with id %d!";


    public PassengerAlreadyAppliedForTravelException(Long passengerId, Long travelId) {
        super(String.format(MESSAGE, passengerId, travelId));
    }
}

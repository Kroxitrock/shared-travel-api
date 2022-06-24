package eu.sharedtravel.app.components.notification.request.join.exceptions;

public class DriverHasNoAccessToJoinRequestException extends RuntimeException {

    private static final String MESSAGE = "Driver with id %d has no access to join request with id %d!";

    public DriverHasNoAccessToJoinRequestException(Long driverId, Long joinRequestId) {
        super(String.format(MESSAGE, driverId, joinRequestId));
    }
}

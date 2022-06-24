package eu.sharedtravel.app.components.notification.model;

/**
 * Enum to be used for {@link Notification#getType()}
 */
public enum NotificationType {
    /**
     * Notification for the driver of a travel, when a passenger requests to join
     */
    JOIN,
    /**
     * Notification for the passenger, when the driver accepts their travel request
     */
    REQUEST_APPROVED,
    /**
     * Notification for the passenger, when the driver declines their travel request
     */
    REQUEST_REJECTED,
    /**
     * Notification for the passenger, that the driver has canceled the travel
     */
    TRAVEL_CANCELED,
    /**
     * Notification for the applicant that the travel they applied for is canceled
     */
    APPLIED_TRAVEL_CANCELED,
    /**
     * Notification for the driver, that the passenger has left the travel
     */
    PASSENGER_LEFT,
    /**
     * Notification for the passenger, that they have been removed from the travel by the driver
     */
    PASSENGER_KICKED,
    /**
     * Notification for the driver, that their travel is today
     */
    DRIVER_TRAVEL_TODAY,
    /**
     * Notification for the passenger, that their travel is today
     */
    PASSENGER_TRAVEL_TODAY
}

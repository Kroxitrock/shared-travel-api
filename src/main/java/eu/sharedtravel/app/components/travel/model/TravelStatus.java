package eu.sharedtravel.app.components.travel.model;

/**
 * Enum to be used for {@link Travel} {@link Travel#getStatus()} {@link #PENDING} {@link #CANCELED}
 */
public enum TravelStatus {
    /**
     * Status indicating that the travel is published but is yet to happen. Assigned by default when creating travel.
     */
    PENDING,
    /**
     * Status indicating the driver has canceled this travel.
     */
    CANCELED
}

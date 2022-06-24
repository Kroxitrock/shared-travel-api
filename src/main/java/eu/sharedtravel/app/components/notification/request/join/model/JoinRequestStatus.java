package eu.sharedtravel.app.components.notification.request.join.model;

/**
 * Enum for the status of a join request notification.
 */
public enum JoinRequestStatus {
    /**
     * The request is still active and has not been reviewed by the driver.
     */
    PENDING,
    /**
     * The request has been approved by the driver and the requester is now a passenger of a travel
     */
    APPROVED,
    /**
     * The driver has rejected the requester from joining the travel
     */
    REJECTED,
    /**
     * The applicant has canceled their request to join the travel
     */
    CANCELED
}

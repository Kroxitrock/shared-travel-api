package eu.sharedtravel.app.common.exception.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST),
    JOIN_TRAVEL_DATE(HttpStatus.BAD_REQUEST),
    JOIN_TRAVEL_STATUS(HttpStatus.BAD_REQUEST),
    ALREADY_APPLIED(HttpStatus.BAD_REQUEST),
    NO_ACCESS_TO_JOIN_REQUEST(HttpStatus.FORBIDDEN),
    NO_ACCESS_TO_TRAVEL(HttpStatus.FORBIDDEN),
    STATUS_CHANGE(HttpStatus.FORBIDDEN);

    HttpStatus httpStatus;

    ExceptionType(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}

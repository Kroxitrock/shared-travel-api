package eu.sharedtravel.app.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sharedtravel.app.common.exception.dto.ExceptionResponse;
import eu.sharedtravel.app.common.exception.dto.ExceptionType;
import eu.sharedtravel.app.components.notification.request.join.exceptions.DriverHasNoAccessToJoinRequestException;
import eu.sharedtravel.app.components.notification.request.join.exceptions.PassengerAlreadyAppliedForTravelException;
import eu.sharedtravel.app.components.travel.exception.JoinTravelDateException;
import eu.sharedtravel.app.components.travel.exception.JoinTravelStatusException;
import eu.sharedtravel.app.components.travel.exception.StatusChangeException;
import eu.sharedtravel.app.components.travel.exception.UserNotDriverOfTravelException;
import eu.sharedtravel.app.components.travel.exception.UserNotPassengerOfTravelException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = DriverHasNoAccessToJoinRequestException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(DriverHasNoAccessToJoinRequestException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.NO_ACCESS_TO_JOIN_REQUEST);
    }

    @ExceptionHandler(value = UserNotDriverOfTravelException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(UserNotDriverOfTravelException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.NO_ACCESS_TO_TRAVEL);
    }

    @ExceptionHandler(value = UserNotPassengerOfTravelException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(UserNotPassengerOfTravelException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.NO_ACCESS_TO_TRAVEL);
    }

    @ExceptionHandler(value = StatusChangeException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(StatusChangeException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.STATUS_CHANGE);
    }

    @ExceptionHandler(value = PassengerAlreadyAppliedForTravelException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(PassengerAlreadyAppliedForTravelException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.ALREADY_APPLIED);
    }

    @ExceptionHandler(value = JoinTravelStatusException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(JoinTravelStatusException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.JOIN_TRAVEL_STATUS);
    }

    @ExceptionHandler(value = JoinTravelDateException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(JoinTravelDateException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.JOIN_TRAVEL_DATE);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(EntityNotFoundException exception) {
        return buildSimpleExceptionResponse(exception, ExceptionType.ENTITY_NOT_FOUND);
    }

    @SneakyThrows
    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        Map<String, String> violations = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            violations.put(error.getCode(), error.getDefaultMessage());
        }

        String violationsData = objectMapper.writeValueAsString(violations);

        ExceptionResponse response = ExceptionResponse.builder()
            .exceptionType(ExceptionType.CONSTRAINT_VIOLATION)
            .timestamp(LocalDateTime.now())
            .message(violationsData)
            .build();

        return new ResponseEntity<>(response, response.getExceptionType().getHttpStatus());
    }

    private ResponseEntity<ExceptionResponse> buildSimpleExceptionResponse(Exception exception,
        ExceptionType exceptionType) {
        ExceptionResponse response = ExceptionResponse.builder()
            .exceptionType(exceptionType)
            .message(exception.getMessage())
            .timestamp(LocalDateTime.now())
            .build();

        return buildExceptionResponseEntity(response);
    }

    private ResponseEntity<ExceptionResponse> buildExceptionResponseEntity(ExceptionResponse response) {
        return new ResponseEntity<>(response, response.getExceptionType().getHttpStatus());
    }

}

package eu.sharedtravel.app.common.exception.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse {

    private ExceptionType exceptionType;
    private String message;
    private LocalDateTime timestamp;

}

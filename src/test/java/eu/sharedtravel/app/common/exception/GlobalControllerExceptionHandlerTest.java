package eu.sharedtravel.app.common.exception;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sharedtravel.app.common.exception.dto.ExceptionResponse;
import eu.sharedtravel.app.common.exception.dto.ExceptionType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {GlobalControllerExceptionHandler.class})
class GlobalControllerExceptionHandlerTest {

    @Autowired
    private GlobalControllerExceptionHandler exceptionHandler;

    @MockBean
    private ObjectMapper objectMapper;

    @Test
    @SuppressWarnings("unchecked")
    void givenMethodArgumentNotValidExceptionShouldHandleIt()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, JsonProcessingException, NoSuchFieldException {
        var exception = Mockito.mock(MethodArgumentNotValidException.class);
        var objectError = Mockito.mock(ObjectError.class);
        var bindingResult = Mockito.mock(BindingResult.class);
        var mockCode = "MOCK_CODE";
        var mockMessage = "mock";

        Mockito.when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        Mockito.when(objectError.getCode()).thenReturn(mockCode);
        Mockito.when(objectError.getDefaultMessage()).thenReturn(mockMessage);

        Field modifiersField = BindException.class.getDeclaredField("bindingResult");
        modifiersField.setAccessible(true);
        modifiersField.set(exception, bindingResult);

        Map<String, String> violations = new HashMap<>();
        violations.put(mockCode, mockMessage);

        Mockito.when(objectMapper.writeValueAsString(violations)).thenReturn(mockMessage);

        Method method = GlobalControllerExceptionHandler.class.getDeclaredMethod("handleMethodArgumentNotValid",
            MethodArgumentNotValidException.class, HttpHeaders.class, HttpStatus.class, WebRequest.class);
        method.setAccessible(true);

        ResponseEntity<ExceptionResponse> response = (ResponseEntity<ExceptionResponse>) method
            .invoke(exceptionHandler, exception, Mockito.mock(HttpHeaders.class), HttpStatus.BAD_REQUEST,
                Mockito.mock(WebRequest.class));

        Assertions.assertEquals(response.getStatusCode(), ExceptionType.CONSTRAINT_VIOLATION.getHttpStatus());
        Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), mockMessage);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenExceptionAndExceptionTypeShouldBuildSimpleExceptionResponse()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var exception = Mockito.mock(Exception.class);
        Mockito.when(exception.getMessage()).thenReturn("mock");

        Method method = GlobalControllerExceptionHandler.class.getDeclaredMethod("buildSimpleExceptionResponse",
            Exception.class, ExceptionType.class);
        method.setAccessible(true);
        ResponseEntity<ExceptionResponse> response = (ResponseEntity<ExceptionResponse>)
            method.invoke(exceptionHandler, exception, ExceptionType.CONSTRAINT_VIOLATION);

        Assertions.assertEquals(response.getStatusCode(), ExceptionType.CONSTRAINT_VIOLATION.getHttpStatus());
        Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), exception.getMessage());
    }
}

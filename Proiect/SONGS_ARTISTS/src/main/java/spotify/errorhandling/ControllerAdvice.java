package spotify.errorhandling;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.customexceptions.UnprocessableContentException;
import spotify.view.responses.ExceptionResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    // for invalid syntax for query params
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(javax.validation.ConstraintViolationException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }


    @ExceptionHandler({ConflictException.class, SQLIntegrityConstraintViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(RuntimeException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // trying to get resource that doesn't exist
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundExceptions(EntityNotFoundException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({UnprocessableContentException.class})
    public ResponseEntity<ExceptionResponse> handleUnprocessableContentException(UnprocessableContentException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // for invalid dto request properties
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String details = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        String details;
        if (ex.getMessage().contains("details")) {
            details = ex.getLocalizedMessage().split("details")[1].replaceAll("[^a-zA-Z0-9 -]", "");
        } else {
            details = ex.getLocalizedMessage().split("error")[1].replaceAll("[^a-zA-Z0-9 -]", "");
        }

        HttpStatus status = ex.getStatusCode();
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }
}
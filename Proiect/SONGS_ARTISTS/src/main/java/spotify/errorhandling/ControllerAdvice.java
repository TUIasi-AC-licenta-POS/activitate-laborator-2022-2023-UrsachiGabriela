package spotify.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.customexceptions.UnprocessableContentException;
import spotify.errorhandling.utils.ExceptionMessage;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    // for invalid syntax for query params
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConstraintViolationException(javax.validation.ConstraintViolationException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }

    @ExceptionHandler({ConflictException.class, SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ExceptionMessage> handleConflictExceptions(RuntimeException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }

    // trying to get resource that doesn't exist
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleEntityNotFoundExceptions(EntityNotFoundException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }

    @ExceptionHandler({UnprocessableContentException.class})
    public ResponseEntity<ExceptionMessage> handleUnprocessableContentException(UnprocessableContentException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }

    // for invalid dto request properties
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String details = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionMessage, status);
    }
}

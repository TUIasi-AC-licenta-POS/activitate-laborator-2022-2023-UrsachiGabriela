package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.view.responses.ExceptionResponse;

@ControllerAdvice
public class ConflictAdvice {
    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(ConflictException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionResponse, status);
    }
}

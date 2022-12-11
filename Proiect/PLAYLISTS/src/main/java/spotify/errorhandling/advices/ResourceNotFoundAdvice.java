package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import spotify.errorhandling.customexceptions.CollectionNotFoundException;
import spotify.errorhandling.customexceptions.DocumentNotFoundException;
import spotify.view.responses.ExceptionResponse;

@ControllerAdvice
public class ResourceNotFoundAdvice {

    @ExceptionHandler({DocumentNotFoundException.class, CollectionNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundExceptions(RuntimeException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionResponse, status);
    }
}
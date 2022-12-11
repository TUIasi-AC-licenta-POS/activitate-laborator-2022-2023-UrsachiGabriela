package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.utils.ExceptionMessage;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class EntityNotFoundAdvice {
//    @ResponseBody
//    @ExceptionHandler(EntityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    String entityNotFoundHandler(EntityNotFoundException ex) {
//        return ex.getMessage();
//    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleEntityNotFoundExceptions(EntityNotFoundException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }
}
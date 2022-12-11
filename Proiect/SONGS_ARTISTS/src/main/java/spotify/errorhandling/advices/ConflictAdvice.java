package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.utils.ExceptionMessage;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ConflictAdvice {
//    @ResponseBody
//    @ExceptionHandler({ConflictException.class, SQLIntegrityConstraintViolationException.class})
//    @ResponseStatus(HttpStatus.CONFLICT)
//    String conflictHandler(Exception ex) {
//        return ex.getMessage();
//    }


    @ExceptionHandler({ConflictException.class, SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ExceptionMessage> handleConflictExceptions(RuntimeException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }
}

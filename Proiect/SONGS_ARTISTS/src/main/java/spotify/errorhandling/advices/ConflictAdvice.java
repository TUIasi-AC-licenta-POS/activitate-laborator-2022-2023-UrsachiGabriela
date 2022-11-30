package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spotify.errorhandling.customexceptions.ConflictException;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ConflictAdvice {
    @ResponseBody
    @ExceptionHandler({ConflictException.class, SQLIntegrityConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    String conflictHandler(Exception ex) {
        return ex.getMessage();
    }

}

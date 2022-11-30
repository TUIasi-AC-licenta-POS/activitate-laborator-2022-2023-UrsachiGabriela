package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

// for invalid query params
@ControllerAdvice
public class BadRequestAdvice {
    @ResponseBody
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String constraintViolationHandler(javax.validation.ConstraintViolationException ex) {
        return ex.getMessage();
    }

}
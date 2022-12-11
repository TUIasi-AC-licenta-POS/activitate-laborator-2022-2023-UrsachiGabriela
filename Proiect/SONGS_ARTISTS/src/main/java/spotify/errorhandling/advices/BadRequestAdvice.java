package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.utils.ExceptionMessage;

// for invalid query params
@ControllerAdvice
public class BadRequestAdvice {
//    @ResponseBody
//    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    String constraintViolationHandler(javax.validation.ConstraintViolationException ex) {
//        return ex.getMessage();
//    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConstraintViolationException(javax.validation.ConstraintViolationException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }
}
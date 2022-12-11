package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.customexceptions.UnprocessableContentException;
import spotify.errorhandling.utils.ExceptionMessage;

@ControllerAdvice
public class UnprocessableContentAdvice {
//    @ResponseBody
//    @ExceptionHandler(UnprocessableContentException.class)
//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
//    String unprocessableContentHandler(UnprocessableContentException ex) {
//        return ex.getMessage();
//    }

    @ExceptionHandler({UnprocessableContentException.class})
    public ResponseEntity<ExceptionMessage> handleUnprocessableContentException(UnprocessableContentException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionMessage exceptionMessage = new ExceptionMessage(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionMessage, status);
    }
}

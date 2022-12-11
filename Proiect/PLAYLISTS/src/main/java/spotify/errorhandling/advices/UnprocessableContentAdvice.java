package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import spotify.errorhandling.customexceptions.UnprocessableContentException;
import spotify.view.responses.ExceptionResponse;

@ControllerAdvice
public class UnprocessableContentAdvice {

    @ExceptionHandler({UnprocessableContentException.class})
    public ResponseEntity<ExceptionResponse> handleUnprocessableContentException(UnprocessableContentException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionResponse, status);
    }
}

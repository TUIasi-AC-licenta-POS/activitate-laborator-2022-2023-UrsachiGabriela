package spotify.errorhandling.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import spotify.errorhandling.customexceptions.UnprocessableContentException;

@ControllerAdvice
public class UnprocessableContentAdvice {
    @ResponseBody
    @ExceptionHandler(UnprocessableContentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String unprocessableContentHandler(UnprocessableContentException ex) {
        return ex.getMessage();
    }

}

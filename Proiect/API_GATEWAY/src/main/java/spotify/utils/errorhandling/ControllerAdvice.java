package spotify.utils.errorhandling;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {


    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        if (ex.getResponseBodyAsString().isEmpty()) {
            return new ResponseEntity<>(null, ex.getStatusCode());
        }
        return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
    }

    @ExceptionHandler(SoapFaultClientException.class)
    public ResponseEntity<ExceptionResponse> handleSOAPClientErrorException(SoapFaultClientException ex) {
        if (Objects.equals(ex.getMessage(), "Invalid token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return null;
    }


}

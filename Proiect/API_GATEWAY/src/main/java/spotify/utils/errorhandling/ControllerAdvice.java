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

        if (Objects.equals(ex.getMessage(), "Forbidden")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(Objects.equals(ex.getMessage(), "Invalid role")){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.name(), HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid role"));
        }

        if(Objects.equals(ex.getMessage(), "Too weak password")){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.name(), HttpStatus.UNPROCESSABLE_ENTITY.value(), "Too weak password"));
        }

        if(Objects.equals(ex.getMessage(), "This username already exists")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponse(HttpStatus.CONFLICT.name(), HttpStatus.CONFLICT.value(), "Name is already taken"));
        }

        return null;
    }

    @ExceptionHandler(InvalidEnumException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidEnumException(InvalidEnumException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

}

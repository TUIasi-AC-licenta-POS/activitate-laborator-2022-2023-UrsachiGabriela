package com.spotify.playlists.utils.errorhandling;

import com.spotify.playlists.utils.errorhandling.customexceptions.ConflictException;
import com.spotify.playlists.utils.errorhandling.customexceptions.ResourceNotFoundException;
import com.spotify.playlists.view.responses.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    // for invalid query params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(ConflictException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundExceptions(RuntimeException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // for invalid dto request fields
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String details = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        String details;
        if (ex.getMessage().contains("details")) {
            details = ex.getLocalizedMessage().split("details")[1].replaceAll("[^a-zA-Z0-9 -]", "");
        } else {
            details = "Bad request";
        }

        HttpStatus status = ex.getStatusCode();
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

}

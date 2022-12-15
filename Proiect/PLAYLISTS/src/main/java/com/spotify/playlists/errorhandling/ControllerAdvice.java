package com.spotify.playlists.errorhandling;

import com.spotify.playlists.errorhandling.customexceptions.CollectionNotFoundException;
import com.spotify.playlists.errorhandling.customexceptions.ConflictException;
import com.spotify.playlists.errorhandling.customexceptions.DocumentNotFoundException;
import com.spotify.playlists.errorhandling.customexceptions.UnprocessableContentException;
import com.spotify.playlists.view.responses.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    // for invalid query params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(ConflictException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({DocumentNotFoundException.class, CollectionNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundExceptions(RuntimeException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({UnprocessableContentException.class})
    public ResponseEntity<ExceptionResponse> handleUnprocessableContentException(UnprocessableContentException ex){
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(),status.value(),details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // for invalid dto request properties
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String details = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }
}

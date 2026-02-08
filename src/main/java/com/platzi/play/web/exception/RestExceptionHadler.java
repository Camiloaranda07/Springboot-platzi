package com.platzi.play.web.exception;

import com.platzi.play.domain.exception.MovieAlreadyExistsException;
import com.platzi.play.domain.exception.MovieNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHadler {

    @ExceptionHandler(MovieAlreadyExistsException.class)
    public ResponseEntity<Error> handlerException(MovieAlreadyExistsException ex) {
        Error error = new Error("movie-already-exits", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler
    public ResponseEntity<Error> handlerException(MovieNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error("movie-not-found", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Error>> handException(MethodArgumentNotValidException ex) {
        List<Error> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.add(new Error(error.getField(), error.getDefaultMessage()));
        });

        return ResponseEntity.badRequest().body(errors);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handlerException(Exception ex) {
        Error error = new Error("unknown-error", ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
}

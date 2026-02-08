package com.platzi.play.web.exception;

import com.platzi.play.domain.exception.MovieAlreadyExistsException;
import com.platzi.play.domain.exception.MovieNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}

package com.platzi.play.domain.exception;

public class MovieNotFound extends RuntimeException {
    public MovieNotFound() {
        super("Pelicula no encontrada.");
    }
}

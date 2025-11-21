package com.platzi.play.domain.dto;

import com.platzi.play.domain.Genre;

import java.time.LocalDate;

public record MovieDto(
        String tittle,
        Integer duration,
        Genre genre,
        LocalDate releaseDate,
        Double rating,
        String state
        ) {

}

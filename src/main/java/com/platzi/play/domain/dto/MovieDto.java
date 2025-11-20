package com.platzi.play.domain.dto;

import java.time.LocalDate;

public record MovieDto(
        String tittle,
        Integer duration,
        String genre,
        LocalDate releaseDate,
        Double rating
        ) {

}

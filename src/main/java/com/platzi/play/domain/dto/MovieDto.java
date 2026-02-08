package com.platzi.play.domain.dto;

import com.platzi.play.domain.Genre;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MovieDto(
        @NotBlank(message = "El titulo no puede estar vacio.")
        String title,
        @Min(value = 1, message = "La duracion no puede ser menor a 1 minuto")
        @Max(value = 300, message = "La duracion no puede ser mayor a 300 minutos")
        Integer duration,
        @NotNull(message = "El genero es obligatorio.")
        Genre genre,
        @PastOrPresent(message = "La fecha de lanzamiento debe ser anterior o igual a la actual.")
        LocalDate releaseDate,
        @Min(value = 0, message = "La rating no puede ser menor que 0")
        @Max(value = 5, message = "La rating no puede ser mayor que 5")
        Double rating,
        @NotNull(message = "El estado debe ser informado.")
        String state
        ) {

}

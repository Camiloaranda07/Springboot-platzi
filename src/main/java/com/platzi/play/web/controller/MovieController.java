package com.platzi.play.web.controller;

import com.platzi.play.domain.dto.MovieDto;
import com.platzi.play.domain.dto.SuggestRequestDto;
import com.platzi.play.domain.dto.UpdateMovieDto;
import com.platzi.play.domain.service.MovieService;
import com.platzi.play.domain.service.PlatziPlayAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movies")
@Tag(name = "Movies", description = "Operation about movies of PlatziPlay.")
public class MovieController {
    private final MovieService movieService;
    private final PlatziPlayAiService aiService;

    public MovieController(MovieService movieService, PlatziPlayAiService aiService) {
        this.movieService = movieService;
        this.aiService = aiService;
    }

    @GetMapping
    @Operation(
            summary = "Obtener todas las peliculas existentes.",
            description = "Retorna todas las peliculas existentes en base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Peliculas encontradas"),
            }
    )
    public ResponseEntity<List<MovieDto>> getAll() {
        return ResponseEntity.ok(this.movieService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener una pelicula por su identificador.",
            description = "Retorna la pelicula que coincida con el identificador enviado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pelicula encontrada"),
                    @ApiResponse(responseCode = "404", description = "Pelicula no encontrada", content = @Content)
            }
    )
    public ResponseEntity<MovieDto> getById(@Parameter(description = "Identificador de pelicula a recuperar", example = "9") @PathVariable long id) {
        MovieDto movieDto = this.movieService.getById(id);

        if (movieDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movieDto);
    }

    @PostMapping("/suggest")
    @Operation(
            summary = "Pelicula sugerida dentro de la base de datos.",
            description = "Retorna peliculas sugeridas segun tus intereses.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pelicula sugerida")
            }
    )
    public ResponseEntity<String> generateMoviesSuggestion(@Parameter(description = "Intereses del usuario.", example = "accion,comedia") @RequestBody SuggestRequestDto suggestRequestDto) {
        return ResponseEntity.ok(this.aiService.generateMoviesSuggestion(suggestRequestDto.userPreferences()));
    }

    @PostMapping
    @Operation(
            summary = "Agrega peliculas a la base de datos.",
            description = "Agrega peliculas en la base de datos local",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Nuevo recurso agregado con exito"),
                    @ApiResponse(responseCode = "400", description = "Faltan parametros obligatorios.", content = @Content)
            }

    )
    public ResponseEntity<MovieDto> add(@Parameter(description = "Pelicula a agregar.", example = "{ title: Marti supreme, duration: 160, genre: ACTION," +
            "releaseDate: 2026-01-24, rating: 4.8, state: D }") @RequestBody @Valid MovieDto movieDto) {
        MovieDto movieDtoResponse = this.movieService.add(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movieDtoResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actuliza la informacion de una pelicula.",
            description = "Modifica la informacion de una pelicula en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso actualizado con exito."),
                    @ApiResponse(responseCode = "400", description = "No cumple con los parametros solicitados", content = @Content)
            }

    )
    public ResponseEntity<MovieDto> update(@Parameter(description = "Identificador de la pelicula a actualizar.", example = "10" +
            "{title: Marti supreme, releaseDate: 2015-06-29, rating: 4.8 }") @PathVariable Long id, @RequestBody @Valid UpdateMovieDto updateMovieDto) {
        return ResponseEntity.ok(this.movieService.update(id, updateMovieDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Borra la informacion de una pelicula.",
            description = "Borra los datos de una pelicula de la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso eliminado con exito."),
                    @ApiResponse(responseCode = "404", description = "Pelicula no encontrada", content = @Content)
            }

    )
    public ResponseEntity<Void> delete(@Parameter(description = "Identificador de la pelicula a borrar.", example = "10") @PathVariable Long id) {
        this.movieService.delete(id);
        return ResponseEntity.ok().build();
    }
}

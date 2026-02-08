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
@Tag(name = "Movies", description = "Operation about movies of PlatziPlay." )
public class MovieController {
    private final MovieService movieService;
    private final PlatziPlayAiService aiService;

    public MovieController(MovieService movieService, PlatziPlayAiService aiService) {
        this.movieService = movieService;
        this.aiService = aiService;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAll() {
        return ResponseEntity.ok(this.movieService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a movie by Id.",
            description = "Return a movie by Id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pelicula encontrada"),
                    @ApiResponse(responseCode = "404", description = "Pelicula no encontrada", content = @Content)
            }
    )
    public ResponseEntity<MovieDto> getById(@Parameter(description = "Identificado de pelicula a operar") @PathVariable long id) {
        MovieDto movieDto = this.movieService.getById(id);

        if (movieDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movieDto);
    }

    @PostMapping("/suggest")
    public ResponseEntity<String> generateMoviesSuggestion(@RequestBody SuggestRequestDto suggestRequestDto) {
        return ResponseEntity.ok(this.aiService.generateMoviesSuggestion(suggestRequestDto.userPreferences()));
    }

    @PostMapping
    public ResponseEntity<MovieDto> add(@RequestBody @Valid MovieDto movieDto) {
        MovieDto movieDtoResponse = this.movieService.add(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movieDtoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> update(@PathVariable Long id, @RequestBody @Valid UpdateMovieDto updateMovieDto) {
        return ResponseEntity.ok(this.movieService.update(id, updateMovieDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.movieService.delete(id);
        return ResponseEntity.ok().build();
    }
}

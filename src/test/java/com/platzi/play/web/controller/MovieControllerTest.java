package com.platzi.play.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platzi.play.domain.Genre;
import com.platzi.play.domain.dto.MovieDto;
import com.platzi.play.domain.dto.UpdateMovieDto;
import com.platzi.play.domain.exception.MovieNotFound;
import com.platzi.play.domain.service.MovieService;
import com.platzi.play.domain.service.PlatziPlayAiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private PlatziPlayAiService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    // Test fixtures
    private MovieDto createTestMovieDto() {
        return new MovieDto(
                "The Matrix",
                120,
                Genre.SCI_FI,
                LocalDate.of(1999, 3, 31),
                4.8,
                "D"
        );
    }

    private MovieDto createTestMovieDtoWithTitle(String title) {
        return new MovieDto(
                title,
                120,
                Genre.ACTION,
                LocalDate.of(2000, 1, 1),
                4.5,
                "D"
        );
    }

    // ===================== GET ALL TESTS =====================
    @Test
    void shouldReturnAllMovies_returns200() throws Exception {
        // arrange
        List<MovieDto> movies = Arrays.asList(
                createTestMovieDto(),
                createTestMovieDtoWithTitle("Inception")
        );
        when(movieService.getAll()).thenReturn(movies);

        // act & assert
        mockMvc.perform(get("/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", equalTo("The Matrix")))
                .andExpect(jsonPath("$[1].title", equalTo("Inception")));

        verify(movieService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyList_whenNoMovies() throws Exception {
        // arrange
        when(movieService.getAll()).thenReturn(Collections.emptyList());

        // act & assert
        mockMvc.perform(get("/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(movieService, times(1)).getAll();
    }

    // ===================== GET BY ID TESTS =====================
    @Test
    void shouldReturnMovie_whenMovieExists_returns200() throws Exception {
        // arrange
        long movieId = 1L;
        MovieDto movieDto = createTestMovieDto();
        when(movieService.getById(movieId)).thenReturn(movieDto);

        // act & assert
        mockMvc.perform(get("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("The Matrix")))
                .andExpect(jsonPath("$.genre", equalTo("SCI_FI")))
                .andExpect(jsonPath("$.duration", equalTo(120)));

        verify(movieService, times(1)).getById(movieId);
    }

    @Test
    void shouldReturnNotFound_whenMovieNotExists_returns404() throws Exception {
        // arrange
        long movieId = 999L;
        when(movieService.getById(movieId)).thenReturn(null);

        // act & assert
        mockMvc.perform(get("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).getById(movieId);
    }

    @Test
    void shouldHandleException_whenServiceThrows_returns500() throws Exception {
        // arrange
        long movieId = 1L;
        when(movieService.getById(movieId)).thenThrow(new RuntimeException("Database error"));

        // act & assert
        mockMvc.perform(get("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(movieService, times(1)).getById(movieId);
    }

    // ===================== POST CREATE TESTS =====================
    @Test
    void shouldCreateMovie_whenValidData_returns201() throws Exception {
        // arrange
        MovieDto inputDto = createTestMovieDto();
        MovieDto responseDto = createTestMovieDto();
        when(movieService.add(any(MovieDto.class))).thenReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(inputDto);

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", equalTo("The Matrix")))
                .andExpect(jsonPath("$.genre", equalTo("SCI_FI")));

        verify(movieService, times(1)).add(any(MovieDto.class));
    }

    @Test
    void shouldReturnBadRequest_whenTitleIsBlank_returns400() throws Exception {
        // arrange
        String jsonRequest = "{\"title\": \"\", \"duration\": 120, \"genre\": \"SCI_FI\", \"releaseDate\": \"1999-03-31\", \"rating\": 4.8, \"state\": \"D\"}";

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).add(any());
    }

    @Test
    void shouldReturnBadRequest_whenGenreIsMissing_returns400() throws Exception {
        // arrange
        String jsonRequest = "{\"title\": \"The Matrix\", \"duration\": 120, \"releaseDate\": \"1999-03-31\", \"rating\": 4.8, \"state\": \"D\"}";

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).add(any());
    }

    @Test
    void shouldReturnBadRequest_whenStateIsMissing_returns400() throws Exception {
        // arrange
        String jsonRequest = "{\"title\": \"The Matrix\", \"duration\": 120, \"genre\": \"SCI_FI\", \"releaseDate\": \"1999-03-31\", \"rating\": 4.8}";

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).add(any());
    }

    @Test
    void shouldReturnBadRequest_whenDurationOutOfRange_returns400() throws Exception {
        // arrange
        String jsonRequest = "{\"title\": \"The Matrix\", \"duration\": 301, \"genre\": \"SCI_FI\", \"releaseDate\": \"1999-03-31\", \"rating\": 4.8, \"state\": \"D\"}";

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).add(any());
    }

    @Test
    void shouldReturnBadRequest_whenRatingOutOfRange_returns400() throws Exception {
        // arrange
        String jsonRequest = "{\"title\": \"The Matrix\", \"duration\": 120, \"genre\": \"SCI_FI\", \"releaseDate\": \"1999-03-31\", \"rating\": 6.0, \"state\": \"D\"}";

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).add(any());
    }

    @Test
    void shouldReturnBadRequest_whenReleaseDateInFuture_returns400() throws Exception {
        // arrange
        String jsonRequest = "{\"title\": \"The Matrix\", \"duration\": 120, \"genre\": \"SCI_FI\", \"releaseDate\": \"2099-03-31\", \"rating\": 4.8, \"state\": \"D\"}";

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).add(any());
    }

    @Test
    void shouldHandleServiceException_whenMovieAlreadyExists_returns500() throws Exception {
        // arrange
        MovieDto inputDto = createTestMovieDto();
        when(movieService.add(any(MovieDto.class)))
                .thenThrow(new RuntimeException("La pelicula The Matrix ya existe."));

        String jsonRequest = objectMapper.writeValueAsString(inputDto);

        // act & assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isInternalServerError());

        verify(movieService, times(1)).add(any(MovieDto.class));
    }

    // ===================== PUT UPDATE TESTS =====================
    @Test
    void shouldUpdateMovie_whenValidData_returns200() throws Exception {
        // arrange
        long movieId = 1L;
        UpdateMovieDto updateDto = new UpdateMovieDto(
                "The Matrix Reloaded",
                LocalDate.of(2003, 5, 15),
                4.7
        );
        MovieDto responseDto = new MovieDto(
                "The Matrix Reloaded",
                120,
                Genre.SCI_FI,
                LocalDate.of(2003, 5, 15),
                4.7,
                "D"
        );
        when(movieService.update(eq(movieId), any(UpdateMovieDto.class))).thenReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        // act & assert
        mockMvc.perform(put("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("The Matrix Reloaded")))
                .andExpect(jsonPath("$.rating", closeTo(4.7, 0.01)));

        verify(movieService, times(1)).update(eq(movieId), any(UpdateMovieDto.class));
    }

    @Test
    void shouldReturnBadRequest_whenUpdateTitleIsBlank_returns400() throws Exception {
        // arrange
        long movieId = 1L;
        String jsonRequest = "{\"title\": \"\", \"releaseDate\": \"2003-05-15\", \"rating\": 4.7}";

        // act & assert
        mockMvc.perform(put("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).update(anyLong(), any());
    }

    @Test
    void shouldReturnBadRequest_whenUpdateReleaseDateInFuture_returns400() throws Exception {
        // arrange
        long movieId = 1L;
        String jsonRequest = "{\"title\": \"Updated Title\", \"releaseDate\": \"2099-05-15\", \"rating\": 4.7}";

        // act & assert
        mockMvc.perform(put("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).update(anyLong(), any());
    }

    @Test
    void shouldHandleServiceException_whenMovieNotFoundDuringUpdate_returns404() throws Exception {
        // arrange
        long movieId = 999L;
        UpdateMovieDto updateDto = new UpdateMovieDto(
                "Some Title",
                LocalDate.of(2003, 5, 15),
                4.7
        );
        when(movieService.update(eq(movieId), any(UpdateMovieDto.class)))
                .thenThrow(new MovieNotFound());

        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        // act & assert
        mockMvc.perform(put("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).update(eq(movieId), any(UpdateMovieDto.class));
    }

    // ===================== DELETE TESTS =====================
    @Test
    void shouldDeleteMovie_whenMovieExists_returns200() throws Exception {
        // arrange
        long movieId = 1L;
        doNothing().when(movieService).delete(movieId);

        // act & assert
        mockMvc.perform(delete("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(movieService, times(1)).delete(movieId);
    }

    @Test
    void shouldHandleException_whenMovieNotFoundDuringDelete_returns404() throws Exception {
        // arrange
        long movieId = 999L;
        doThrow(new MovieNotFound()).when(movieService).delete(movieId);

        // act & assert
        mockMvc.perform(delete("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).delete(movieId);
    }

}


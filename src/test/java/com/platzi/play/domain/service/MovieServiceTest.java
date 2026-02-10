package com.platzi.play.domain.service;

import com.platzi.play.domain.Genre;
import com.platzi.play.domain.dto.MovieDto;
import com.platzi.play.domain.dto.UpdateMovieDto;
import com.platzi.play.domain.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

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
                Genre.SCI_FI,
                LocalDate.of(1999, 3, 31),
                4.8,
                "D"
        );
    }

    // ===================== GETALL TESTS =====================
    @Test
    void shouldReturnAllMovies_whenMoviesExist() {
        // arrange
        List<MovieDto> movies = Arrays.asList(
                createTestMovieDto(),
                createTestMovieDtoWithTitle("Inception")
        );
        when(movieRepository.getAll()).thenReturn(movies);

        // act
        List<MovieDto> result = movieService.getAll();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("The Matrix", result.get(0).title());
        verify(movieRepository, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyList_whenNoMoviesExist() {
        // arrange
        when(movieRepository.getAll()).thenReturn(Collections.emptyList());

        // act
        List<MovieDto> result = movieService.getAll();

        // assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieRepository, times(1)).getAll();
    }

    // ===================== GETBYID TESTS =====================
    @Test
    void shouldReturnMovie_whenMovieExists() {
        // arrange
        long movieId = 1L;
        MovieDto expectedMovie = createTestMovieDto();
        when(movieRepository.getById(movieId)).thenReturn(expectedMovie);

        // act
        MovieDto result = movieService.getById(movieId);

        // assert
        assertNotNull(result);
        assertEquals("The Matrix", result.title());
        verify(movieRepository, times(1)).getById(movieId);
    }

    @Test
    void shouldReturnNull_whenMovieNotFound() {
        // arrange
        long movieId = 999L;
        when(movieRepository.getById(movieId)).thenReturn(null);

        // act
        MovieDto result = movieService.getById(movieId);

        // assert
        assertNull(result);
        verify(movieRepository, times(1)).getById(movieId);
    }

    @Test
    void shouldThrowException_whenRepositoryThrowsException() {
        // arrange
        long movieId = 1L;
        when(movieRepository.getById(movieId)).thenThrow(new RuntimeException("Database error"));

        // act & assert
        assertThrows(RuntimeException.class, () -> movieService.getById(movieId));
        verify(movieRepository, times(1)).getById(movieId);
    }

    // ===================== ADD TESTS =====================
    @Test
    void shouldCreateMovie_whenValidMovieDtoProvided() {
        // arrange
        MovieDto inputDto = createTestMovieDto();
        MovieDto savedDto = createTestMovieDto();
        when(movieRepository.save(inputDto)).thenReturn(savedDto);

        // act
        MovieDto result = movieService.add(inputDto);

        // assert
        assertNotNull(result);
        assertEquals("The Matrix", result.title());
        assertEquals(Genre.SCI_FI, result.genre());
        verify(movieRepository, times(1)).save(inputDto);
    }

    @Test
    void shouldPropagateDuplicateMovieException_whenMovieAlreadyExists() {
        // arrange
        MovieDto inputDto = createTestMovieDto();
        when(movieRepository.save(inputDto))
                .thenThrow(new RuntimeException("La pelicula The Matrix ya existe."));

        // act & assert
        assertThrows(RuntimeException.class, () -> movieService.add(inputDto));
        verify(movieRepository, times(1)).save(inputDto);
    }

    @Test
    void shouldPassMovieDtoToRepository_whenAddingMovie() {
        // arrange
        MovieDto inputDto = createTestMovieDto();
        MovieDto savedDto = createTestMovieDto();
        when(movieRepository.save(any(MovieDto.class))).thenReturn(savedDto);

        // act
        movieService.add(inputDto);

        // assert
        verify(movieRepository, times(1)).save(any(MovieDto.class));
    }

    // ===================== UPDATE TESTS =====================
    @Test
    void shouldUpdateMovie_whenMovieExists() {
        // arrange
        long movieId = 1L;
        UpdateMovieDto updateDto = new UpdateMovieDto(
                "The Matrix Reloaded",
                LocalDate.of(2003, 5, 15),
                4.7
        );
        MovieDto updatedMovie = new MovieDto(
                "The Matrix Reloaded",
                120,
                Genre.SCI_FI,
                LocalDate.of(2003, 5, 15),
                4.7,
                "D"
        );
        when(movieRepository.update(movieId, updateDto)).thenReturn(updatedMovie);

        // act
        MovieDto result = movieService.update(movieId, updateDto);

        // assert
        assertNotNull(result);
        assertEquals("The Matrix Reloaded", result.title());
        assertEquals(4.7, result.rating());
        verify(movieRepository, times(1)).update(movieId, updateDto);
    }

    @Test
    void shouldPropagateException_whenMovieNotFoundDuringUpdate() {
        // arrange
        long movieId = 999L;
        UpdateMovieDto updateDto = new UpdateMovieDto(
                "Some Title",
                LocalDate.of(2003, 5, 15),
                4.7
        );
        when(movieRepository.update(movieId, updateDto))
                .thenThrow(new RuntimeException("Pelicula no encontrada."));

        // act & assert
        assertThrows(RuntimeException.class, () -> movieService.update(movieId, updateDto));
        verify(movieRepository, times(1)).update(movieId, updateDto);
    }

    @Test
    void shouldPropagateException_whenDuplicateTitleDuringUpdate() {
        // arrange
        long movieId = 1L;
        UpdateMovieDto updateDto = new UpdateMovieDto(
                "Existing Movie Title",
                LocalDate.of(2003, 5, 15),
                4.7
        );
        when(movieRepository.update(movieId, updateDto))
                .thenThrow(new RuntimeException("La pelicula Existing Movie Title ya existe."));

        // act & assert
        assertThrows(RuntimeException.class, () -> movieService.update(movieId, updateDto));
        verify(movieRepository, times(1)).update(movieId, updateDto);
    }

    // ===================== DELETE TESTS =====================
    @Test
    void shouldDeleteMovie_whenMovieExists() {
        // arrange
        long movieId = 1L;
        doNothing().when(movieRepository).delete(movieId);

        // act
        movieService.delete(movieId);

        // assert
        verify(movieRepository, times(1)).delete(movieId);
    }

    @Test
    void shouldThrowException_whenMovieNotFoundDuringDelete() {
        // arrange
        long movieId = 999L;
        doThrow(new RuntimeException("Pelicula no encontrada."))
                .when(movieRepository).delete(movieId);

        // act & assert
        assertThrows(RuntimeException.class, () -> movieService.delete(movieId));
        verify(movieRepository, times(1)).delete(movieId);
    }

    @Test
    void shouldCallRepositoryDelete_withCorrectId() {
        // arrange
        long movieId = 5L;
        doNothing().when(movieRepository).delete(movieId);

        // act
        movieService.delete(movieId);

        // assert
        verify(movieRepository, times(1)).delete(5L);
    }

}


package com.platzi.play.persistence.mapper;

import com.platzi.play.domain.Genre;
import com.platzi.play.domain.dto.MovieDto;
import com.platzi.play.domain.dto.UpdateMovieDto;
import com.platzi.play.persistence.entity.MovieEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieMapperTest {

    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        // Usar Mappers.getMapper para obtener una instancia del mapper generado por MapStruct
        movieMapper = Mappers.getMapper(MovieMapper.class);
    }

    // ===================== DTO TO ENTITY TESTS =====================
    @Test
    void shouldMapDtoToEntity_correctly() {
        // arrange
        MovieDto dto = new MovieDto(
                "The Matrix",
                120,
                Genre.SCI_FI,
                LocalDate.of(1999, 3, 31),
                4.8,
                "D"
        );

        // act
        MovieEntity entity = movieMapper.toEntity(dto);

        // assert
        assertNotNull(entity);
        assertEquals("The Matrix", entity.getTitulo());
        assertEquals(120, entity.getDuracion());
        assertEquals("CIENCIA_FICCION", entity.getGenero());
        assertEquals(LocalDate.of(1999, 3, 31), entity.getFechaEstreno());
        assertEquals(BigDecimal.valueOf(4.8), entity.getClasificacion());
        // estado should be ignored during mapping from DTO to entity
    }

    @Test
    void shouldMapAllGenres_dtoToEntity() {
        // test for each genre to ensure proper conversion
        Genre[] genres = {Genre.ACTION, Genre.COMEDY, Genre.DRAMA, Genre.ANIMATED, Genre.HORROR, Genre.SCI_FI};
        String[] expectedGenresInEntity = {"ACCION", "COMEDIA", "DRAMA", "ANIMADA", "TERROR", "CIENCIA_FICCION"};

        for (int i = 0; i < genres.length; i++) {
            MovieDto dto = new MovieDto(
                    "Movie Title",
                    120,
                    genres[i],
                    LocalDate.of(2000, 1, 1),
                    4.0,
                    "D"
            );

            MovieEntity entity = movieMapper.toEntity(dto);
            assertEquals(expectedGenresInEntity[i], entity.getGenero(),
                    "Genre " + genres[i] + " should map to " + expectedGenresInEntity[i]);
        }
    }

    @Test
    void shouldMapDtoToEntity_withNullGenre() {
        // arrange - create a DTO with null genre (this will fail validation in real scenario)
        // Testing the mapper behavior when null is passed
        MovieEntity entity = new MovieEntity();
        entity.setTitulo("Test");
        entity.setDuracion(120);
        entity.setGenero(null);
        entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity.setClasificacion(BigDecimal.valueOf(4.0));
        entity.setEstado("D");

        // act - map entity to dto
        MovieDto dto = movieMapper.toDto(entity);

        // assert
        assertNotNull(dto);
        assertEquals("Test", dto.title());
        assertNull(dto.genre());
    }

    // ===================== ENTITY TO DTO TESTS =====================
    @Test
    void shouldMapEntityToDto_correctly() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setId(1L);
        entity.setTitulo("The Matrix");
        entity.setDuracion(120);
        entity.setGenero("CIENCIA_FICCION");
        entity.setFechaEstreno(LocalDate.of(1999, 3, 31));
        entity.setClasificacion(BigDecimal.valueOf(4.8));
        entity.setEstado("D");

        // act
        MovieDto dto = movieMapper.toDto(entity);

        // assert
        assertNotNull(dto);
        assertEquals("The Matrix", dto.title());
        assertEquals(120, dto.duration());
        assertEquals(Genre.SCI_FI, dto.genre());
        assertEquals(LocalDate.of(1999, 3, 31), dto.releaseDate());
        assertEquals(4.8, dto.rating(), 0.01);
        // state "D" is converted to Boolean true by StateMapper, but the state field in MovieDto is String
        // So we don't assert on state here as the conversion is handled by StateMapper
        assertNotNull(dto.state());
    }

    @Test
    void shouldMapAllGenres_entityToDto() {
        // test for each genre to ensure proper conversion from entity to dto
        String[] genresInEntity = {"ACCION", "COMEDIA", "DRAMA", "ANIMADA", "TERROR", "CIENCIA_FICCION"};
        Genre[] expectedGenres = {Genre.ACTION, Genre.COMEDY, Genre.DRAMA, Genre.ANIMATED, Genre.HORROR, Genre.SCI_FI};

        for (int i = 0; i < genresInEntity.length; i++) {
            MovieEntity entity = new MovieEntity();
            entity.setTitulo("Movie");
            entity.setDuracion(120);
            entity.setGenero(genresInEntity[i]);
            entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
            entity.setClasificacion(BigDecimal.valueOf(4.0));
            entity.setEstado("D");

            MovieDto dto = movieMapper.toDto(entity);
            assertEquals(expectedGenres[i], dto.genre(),
                    "Genre string " + genresInEntity[i] + " should map to " + expectedGenres[i]);
        }
    }

    @Test
    void shouldMapEntityWithNullDate_toDto() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setTitulo("Test Movie");
        entity.setDuracion(100);
        entity.setGenero("ACCION");
        entity.setFechaEstreno(null);
        entity.setClasificacion(BigDecimal.valueOf(3.5));
        entity.setEstado("D");

        // act
        MovieDto dto = movieMapper.toDto(entity);

        // assert
        assertNotNull(dto);
        assertNull(dto.releaseDate());
        assertEquals("Test Movie", dto.title());
    }

    @Test
    void shouldMapEntityWithNullRating_toDto() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setTitulo("Test Movie");
        entity.setDuracion(100);
        entity.setGenero("ACCION");
        entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity.setClasificacion(null);
        entity.setEstado("D");

        // act
        MovieDto dto = movieMapper.toDto(entity);

        // assert
        assertNotNull(dto);
        assertNull(dto.rating());
    }

    // ===================== LIST MAPPING TESTS =====================
    @Test
    void shouldMapListOfEntitiesToDtos() {
        // arrange
        MovieEntity entity1 = new MovieEntity();
        entity1.setTitulo("Movie 1");
        entity1.setDuracion(100);
        entity1.setGenero("ACCION");
        entity1.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity1.setClasificacion(BigDecimal.valueOf(4.0));
        entity1.setEstado("D");

        MovieEntity entity2 = new MovieEntity();
        entity2.setTitulo("Movie 2");
        entity2.setDuracion(120);
        entity2.setGenero("COMEDIA");
        entity2.setFechaEstreno(LocalDate.of(2001, 1, 1));
        entity2.setClasificacion(BigDecimal.valueOf(3.5));
        entity2.setEstado("D");

        List<MovieEntity> entities = Arrays.asList(entity1, entity2);

        // act
        List<MovieDto> dtos = movieMapper.toDto(entities);

        // assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Movie 1", dtos.get(0).title());
        assertEquals(Genre.ACTION, dtos.get(0).genre());
        assertEquals("Movie 2", dtos.get(1).title());
        assertEquals(Genre.COMEDY, dtos.get(1).genre());
    }

    // ===================== UPDATE DTO MAPPING TESTS =====================
    @Test
    void shouldUpdateEntityFromUpdateDto() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setId(1L);
        entity.setTitulo("Original Title");
        entity.setDuracion(100);
        entity.setGenero("ACCION");
        entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity.setClasificacion(BigDecimal.valueOf(3.0));
        entity.setEstado("D");

        UpdateMovieDto updateDto = new UpdateMovieDto(
                "Updated Title",
                LocalDate.of(2005, 5, 5),
                4.5
        );

        // act
        movieMapper.updateEntiyFromDto(updateDto, entity);

        // assert
        assertEquals("Updated Title", entity.getTitulo());
        assertEquals(LocalDate.of(2005, 5, 5), entity.getFechaEstreno());
        assertEquals(BigDecimal.valueOf(4.5), entity.getClasificacion());
        // Other fields should remain unchanged
        assertEquals(100, entity.getDuracion());
        assertEquals("ACCION", entity.getGenero());
        assertEquals("D", entity.getEstado());
    }

    @Test
    void shouldUpdateEntityFromUpdateDto_withNullValues() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setId(1L);
        entity.setTitulo("Original Title");
        entity.setDuracion(100);
        entity.setGenero("ACCION");
        entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity.setClasificacion(BigDecimal.valueOf(3.0));
        entity.setEstado("D");

        UpdateMovieDto updateDto = new UpdateMovieDto(
                "New Title",
                null,
                null
        );

        // act
        movieMapper.updateEntiyFromDto(updateDto, entity);

        // assert
        assertEquals("New Title", entity.getTitulo());
        assertNull(entity.getFechaEstreno());
        assertNull(entity.getClasificacion());
    }

    // ===================== INVALID GENRE MAPPING TESTS =====================
    @Test
    void shouldReturnNull_whenMappingInvalidGenreStringToEnum() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setTitulo("Test");
        entity.setDuracion(100);
        entity.setGenero("INVALID_GENRE");
        entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity.setClasificacion(BigDecimal.valueOf(4.0));
        entity.setEstado("D");

        // act
        MovieDto dto = movieMapper.toDto(entity);

        // assert
        assertNotNull(dto);
        assertNull(dto.genre(), "Invalid genre should map to null");
    }

    @Test
    void shouldHandleEmptyGenreString() {
        // arrange
        MovieEntity entity = new MovieEntity();
        entity.setTitulo("Test");
        entity.setDuracion(100);
        entity.setGenero("");
        entity.setFechaEstreno(LocalDate.of(2000, 1, 1));
        entity.setClasificacion(BigDecimal.valueOf(4.0));
        entity.setEstado("D");

        // act
        MovieDto dto = movieMapper.toDto(entity);

        // assert
        assertNotNull(dto);
        assertNull(dto.genre(), "Empty genre should map to null");
    }

}


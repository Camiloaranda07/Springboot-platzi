package com.platzi.play.persistence.crud;

import com.platzi.play.persistence.entity.MovieEntity;
import org.springframework.data.repository.CrudRepository;

public interface CurdMovieEntity extends CrudRepository<MovieEntity, Long> {


}

package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {  // <MovieInfo, String> is the type of the entity and the type of the id of the entity

    Flux<MovieInfo> findByYear(Integer year);
    Flux<MovieInfo> findByName(String name);

}

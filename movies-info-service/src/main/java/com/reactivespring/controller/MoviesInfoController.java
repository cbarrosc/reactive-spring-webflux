package com.reactivespring.controller;

import com.reactivespring.MoviesInfoServiceApplication;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;

    @GetMapping("/movie-info")
    public Flux<MovieInfo> getAllMoviesInfo() {
        return moviesInfoService.getAllMoviesInfo().log();
    }

    @GetMapping("/movie-info/{id}")
    public Mono<MovieInfo> getMoviesInfoById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id).log();
    }

    @PostMapping("/movie-info")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody  MovieInfo movieInfo) {
       return moviesInfoService.addMovieInfo(movieInfo).log();
    }

    @PutMapping("/movie-info/{id}")
    public Mono<MovieInfo> updateMovieInfo(@PathVariable String id, @RequestBody MovieInfo updatedMovieInfo) {
        return moviesInfoService.updateMovieInfo(id, updatedMovieInfo).log();
    }

    @DeleteMapping("/movie-info/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id).log();
    }

}

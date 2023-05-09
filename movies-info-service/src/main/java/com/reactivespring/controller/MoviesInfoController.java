package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
@Slf4j
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;

    Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().latest();

    @GetMapping("/movie-info")
    public Flux<MovieInfo> getAllMoviesInfo(@RequestParam(value= "year", required = false) Integer year, @RequestParam(value= "name", required = false) String name) {

        log.info("Year is : {}", year);
        if (year != null) {
            return moviesInfoService.getAllMoviesInfoByYear(year).log();
        }

        log.info("Name is : {}", name);
        if(name != null){
            return moviesInfoService.getAllMoviesInfoByName(name).log();
        }

        return moviesInfoService.getAllMoviesInfo().log();
    }

    @GetMapping("/movie-info/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMoviesInfoById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @GetMapping(value = "/movie-info/stream" , produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMoviesInfoById() {
        return moviesInfoSink.asFlux();
    }

    @PostMapping("/movie-info")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
       return moviesInfoService.addMovieInfo(movieInfo)
               .doOnNext(moviesInfoSink::tryEmitNext)
               .log();
    }

    @PutMapping("/movie-info/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id, @RequestBody MovieInfo updatedMovieInfo) {
        return moviesInfoService.updateMovieInfo(id, updatedMovieInfo)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("/movie-info/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id).log();
    }

}

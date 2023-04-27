package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;
    private final ReviewReactiveRepository reviewReactiveRepository;


    public Mono<ServerResponse> addReview(ServerRequest request) {

        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);

    }

    private void validate(Review review) {
       var constraintViolations = validator.validate(review);
       log.info("constraintViolations : {}", constraintViolations);
        if (constraintViolations.size() > 0) {
            var errorMessage = constraintViolations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
       }
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {

        var movieInfoId = serverRequest.queryParam("movieInfoId");

        Flux<Review> reviewsFlux;
        if(movieInfoId.isPresent()){
            reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
        }else{
            reviewsFlux = reviewReactiveRepository.findAll();
        }
        return buildReviewResponse(reviewsFlux);

    }

    private static Mono<ServerResponse> buildReviewResponse(Flux<Review> reviewsFlux) {
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(review1 -> {
                            review.setReviewId(review1.getReviewId());
                            review.setRating(review1.getRating());
                            return review;
                        })
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(ServerResponse.ok()::bodyValue)
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {

        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewReactiveRepository.delete(review)
                        .then(ServerResponse.noContent().build())
                );
    }
}

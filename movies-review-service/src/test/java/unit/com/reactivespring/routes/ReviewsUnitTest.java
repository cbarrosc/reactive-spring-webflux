package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    static  String REVIEWS_URL = "/v1/reviews";

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        //when

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview!=null;
                    assert savedReview.getReviewId()!=null;
                });
    }

    @Test
    void addReview_validation() {
        //given
        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        //when

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("movieInfoId.null : please pass a valid movieInfoId,rating.negative : please pass a non-negative value");
    }

    @Test
    void getReviews() {

        var reviewsList = List.of(
                new Review("1", 1L, "Awesome Movie", 9.0),
                new Review("2", 1L, "Awesome Movie1", 9.0),
                new Review("3", 2L, "Excellent Movie", 8.0));
        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewsList));

        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var reviews = reviewEntityExchangeResult.getResponseBody();
                    assert reviews!=null;
                    assert reviews.size()==3;
                });
    }

    @Test
    void updateReview() {

        var reviewId = "1";
        var review = new Review("1", 1L, "Awesome Movie", 8.0);
        when(reviewReactiveRepository.findById(reviewId))
                .thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie", 9.0)));
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));


        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assert updatedReview!=null;
                    assert updatedReview.getReviewId().equals(reviewId);
                });
    }

    @Test
    void deleteReview() {
        var reviewId = "1";
        when(reviewReactiveRepository.findById(reviewId))
                .thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie", 9.0)));
        when(reviewReactiveRepository.delete(isA(Review.class)))
                .thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }
}

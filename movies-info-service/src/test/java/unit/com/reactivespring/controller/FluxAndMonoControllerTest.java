package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;



    @Test
    void flux() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void flux_approach2() {
       var flux = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class)
                .getResponseBody();

       StepVerifier.create(flux)
               .expectNext(1,2,3,4)
               .verifyComplete();
    }

    @Test
    void flux_approach3() {
       webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
               .expectBodyList(Integer.class)
               .consumeWith(listEntityExchangeResult -> {
                   var responseBody = listEntityExchangeResult.getResponseBody();
                   assert (Objects.requireNonNull(responseBody).size() == 4);
               });
    }
    @Test
    void mono() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    assert (Objects.requireNonNull(responseBody).equals("hello world"));
                });
    }

    @Test
    void stream() {
        var longFlux = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFlux)
                .expectNext(0L,1L,2L,3L)
                .thenCancel()
                .verify();
    }
}
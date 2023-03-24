package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {
    @Id
    private String movieInfoId; // movieId
    @NotBlank(message = "movieInfo.name must be provided")
    private String name;   // movie name
    @NotNull
    @Positive(message = "movieInfo.year must be a positive value")
    private Integer year; // year of release
    private List<@NotBlank(message = "movieInfo.cast must be present") String> cast; // list of actors
    private LocalDate release_date; // release date
}

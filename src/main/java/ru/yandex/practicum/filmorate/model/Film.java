package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonDeserialize(using = CustomDurationDeserializer.class)
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration duration;
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();
    @JsonProperty("mpa")
    private Rating rating;
    private List<Genre> genres = new ArrayList<>();
    private List<Director> directors = new ArrayList<>();
}

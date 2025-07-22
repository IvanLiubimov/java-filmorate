package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;

    @NotBlank(message = "Review content cannot be empty")
    @Size(min = 10, max = 2000)
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    @Builder.Default
    private Integer useful = 0;
}
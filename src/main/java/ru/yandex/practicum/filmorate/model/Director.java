package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class Director {
    private Long id;
    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}

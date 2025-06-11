package ru.yandex.practicum.filmorate.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String description;
    private Integer errorCode;
}

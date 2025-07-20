package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {


    // Обработка ошибок валидации (400)
    @ExceptionHandler({
            ValidationException.class,
            ConditionsNotMetException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(RuntimeException ex) {

        return ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .error(ex.getMessage())
                .build();
    }

    // Обработка "Не найдено" (404)
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {

        return ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .build();
    }

    // Обработка всех остальных ошибок (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Exception ex) {

        return ErrorResponse.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal server error") // Не показываем детали клиенту
                .build();
    }

   /* @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleReviewNotFound(ReviewNotFoundException ex) {
        return ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .build();
    }*/
}

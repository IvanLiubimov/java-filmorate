package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(ConditionsNotMetException.class)
 @ResponseStatus(HttpStatus.BAD_REQUEST)
 public ErrorResponse handleValidationException(ConditionsNotMetException ex) {
     return ErrorResponse.builder()
             .errorCode(HttpStatus.BAD_REQUEST.value())
             .description(ex.getMessage())
             .build();
 }

 @ExceptionHandler(NotFoundException.class)  // или другое исключение для "не найдено"
 @ResponseStatus(HttpStatus.NOT_FOUND)
 public ErrorResponse handleNotFoundException(NotFoundException ex) {
     return ErrorResponse.builder()
             .errorCode(HttpStatus.NOT_FOUND.value())
             .description(ex.getMessage())
             .build();
 }

 @ExceptionHandler
 @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 public ErrorResponse handleUncaught(Exception exception) {
     return ErrorResponse.builder().errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
             .description(exception.getMessage()).build();
 }

    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidation(ValidationException ex) {
     return ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.value())
                .description(ex.getMessage())
                .build();
    }

}

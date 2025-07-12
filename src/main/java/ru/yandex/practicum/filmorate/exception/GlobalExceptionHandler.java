package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleCustomValidationException(ValidationException ex) {
        return ErrorResponse
                .builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    public ErrorResponse handleConditionsNotMetException(ConditionsNotMetException ex) {
        return ErrorResponse
                .builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return ErrorResponse
                .builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex) {
        return ErrorResponse
                .builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }
}

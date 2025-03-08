package com.springBootApp.RestApiExample.exceptions;

import com.springBootApp.RestApiExample.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorDto> handleResponseStatusException(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    ErrorDto errorDto =
        ErrorDto.builder()
            .message(ex.getReason())
            .code(status.value())
            .error(status.getReasonPhrase())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorDto, status);
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<ErrorDto> handleDateTimeParseException(DateTimeParseException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ErrorDto errorDto =
        ErrorDto.builder()
            .message(ex.getMessage())
            .code(status.value())
            .error("Invalid date format")
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorDto, status);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    ErrorDto errorDto =
        ErrorDto.builder()
            .message(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage())
            .code(status.value())
            .error(status.getReasonPhrase())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorDto, status);
  }
}

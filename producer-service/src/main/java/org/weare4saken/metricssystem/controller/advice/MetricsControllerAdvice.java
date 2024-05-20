package org.weare4saken.metricssystem.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class MetricsControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();

        List<FieldError> fieldErrors = result.getFieldErrors();
        String errorsAsString = fieldErrors.stream()
                .map(error -> "Error: %s - %s".formatted(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(System.lineSeparator()));

        ProblemDetail problemDetail = e.getBody();
        problemDetail.setDetail(errorsAsString);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseException(ResponseStatusException e) {
        return ResponseEntity.of(e.getBody()).build();
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAnyException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        return ResponseEntity.of(problemDetail).build();
    }
}

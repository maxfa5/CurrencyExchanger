package org.currency.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.currency.DTO.ErrorResponseDTO;
import org.currency.exception.CurrencyNotFoundException;
import org.currency.exception.ExchangeRateNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccessException(DataAccessException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Currency with this code or sign already exists");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCurrencyNotFoundException(CurrencyNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(400, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleExchangeRateNotFoundException(ExchangeRateNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(404, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
} 
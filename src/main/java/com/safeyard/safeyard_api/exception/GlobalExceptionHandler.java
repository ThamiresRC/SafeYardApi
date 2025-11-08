package com.safeyard.safeyard_api.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
        log.debug("Recurso nao encontrado: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        var msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        if (msg == null || msg.isBlank()) {
            msg = "Erro de validação nos dados enviados.";
        }

        log.debug("Validação falhou: {}", msg);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> badJson(HttpMessageNotReadableException ex) {
        String root = (ex.getMostSpecificCause() != null)
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        String msg = "JSON invalido ou corpo da requisição nao pode ser lido"
                + (root != null ? (": " + root) : ".");

        log.debug("Bad JSON: {}", msg);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException ex) {
        String param = ex.getName();
        String value = String.valueOf(ex.getValue());
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "tipo esperado";

        String msg = "Parametro invalido: '" + param + "' com valor '" + value
                + "'. Esperado tipo " + requiredType + ".";

        log.debug("Type mismatch: {}", msg);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> constraint(DataIntegrityViolationException ex) {
        String msg = "Operação invalida: violação de integridade de dados (registro duplicado ou referencia invalida).";
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Erro interno nao tratado", ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

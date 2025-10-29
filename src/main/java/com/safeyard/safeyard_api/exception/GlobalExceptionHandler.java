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

    // 404 – não encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
        log.debug("Recurso não encontrado: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 400 – validação de @Valid (body com bean validation)
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

    // 400 – JSON malformado/parse inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> badJson(HttpMessageNotReadableException ex) {
        String root = (ex.getMostSpecificCause() != null)
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        String msg = "JSON inválido ou corpo da requisição não pôde ser lido"
                + (root != null ? (": " + root) : ".");

        log.debug("Bad JSON: {}", msg);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 400 – tipo de parâmetro incorreto (ex.: ?page=abc onde deveria ser número)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException ex) {
        String param = ex.getName();
        String value = String.valueOf(ex.getValue());
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "tipo esperado";

        String msg = "Parâmetro inválido: '" + param + "' com valor '" + value
                + "'. Esperado tipo " + requiredType + ".";

        log.debug("Type mismatch: {}", msg);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 409 – violação de integridade (unique, FK, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> constraint(DataIntegrityViolationException ex) {
        // Mensagem amigável genérica; se quiser, pode inspecionar o SQLState para personalizar
        String msg = "Operação inválida: violação de integridade de dados (registro duplicado ou referência inválida).";
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                msg,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 500 – fallback genérico (sem vazar detalhes)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Erro interno não tratado", ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

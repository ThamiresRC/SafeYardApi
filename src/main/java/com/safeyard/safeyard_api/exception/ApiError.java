package com.safeyard.safeyard_api.exception;


import java.time.LocalDateTime;

public record ApiError(
    int status,
    String message,
    LocalDateTime timestamp
) {}

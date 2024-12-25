package com.drbrosdev.klinkrest.activity.exception;

import com.drbrosdev.klinkrest.activity.exception.model.Error;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerControllerActivity {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Error> handleNullPointerException(Exception ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createErrorResponse(
                        HttpStatus.BAD_GATEWAY.toString(),
                        null));
    }

    private Error createErrorResponse(
            String code,
            @Nullable String message) {
        return new Error()
                .id(randomUUID().toString())
                .code(code)
                .message(message)
                .details(emptyList())
                .timestamp(now());
    }
}

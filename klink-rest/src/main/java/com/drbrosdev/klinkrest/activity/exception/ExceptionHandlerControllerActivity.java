package com.drbrosdev.klinkrest.activity.exception;

import com.drbrosdev.klinkrest.activity.exception.model.Error;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.print.attribute.standard.Severity;
import java.io.Serializable;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerControllerActivity {

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<Error> handleNullPointerException(NullPointerException ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createErrorResponse(
                        ofNullable(ex.getMessage())
                                .orElse("NullPointerException"),
                        asList(ex.getStackTrace())));
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Error> handleRuntimeException(RuntimeException ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createErrorResponse(
                        ofNullable(ex.getMessage())
                                .orElse("RuntimeException"),
                        asList(ex.getStackTrace())));
    }

    @ExceptionHandler(value = {MissingPathVariableException.class})
    public ResponseEntity<Error> handleMissingPathVariableException(MissingPathVariableException ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(
                        HttpStatus.BAD_REQUEST.toString(),
                        ex.getMessage(),
                        asList(ex.getStackTrace())));
    }

    private Error createErrorResponse(String message, List<Serializable> details) {
        return createErrorResponse(
                null,
                message,
                details);
    }

    private Error createErrorResponse(
            String code,
            @Nullable String message,
            List<Serializable> details) {
        return new Error()
                .id(randomUUID().toString())
                .code(code)
                .message(message)
                .details(details)
                .severity(Severity.ERROR)
                .timestamp(now());
    }

}

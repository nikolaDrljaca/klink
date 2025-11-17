package com.drbrosdev.klinkrest.activity.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class ExceptionHandlerControllerActivity {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Void> handleExceptions(Exception ex) {
        if (log.isDebugEnabled()) {
            log.error("DETAIL error:", ex);
        }
        log.error(
                "Error: {}",
                ex.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .build();
    }
}

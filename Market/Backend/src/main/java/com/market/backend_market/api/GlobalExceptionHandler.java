
package com.market.backend_market.api;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateKey(
            DuplicateKeyException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error",
                        "Podany login lub adres e-mail jest już zajęty"
                ));
    }
}

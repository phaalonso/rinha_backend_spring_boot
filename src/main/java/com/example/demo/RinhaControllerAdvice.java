package com.example.demo;

import com.example.demo.exceptions.NotFoundHttpException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RinhaControllerAdvice {
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessage> uniqueKeyError() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessage("Esse nick já está sendo utilizado"));
    }

    @ExceptionHandler(NotFoundHttpException.class)
    public ResponseEntity<?> notFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> errorMessage(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessage(ex.getMessage()));
    }
}

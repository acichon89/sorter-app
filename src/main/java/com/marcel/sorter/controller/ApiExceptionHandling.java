package com.marcel.sorter.controller;

import com.marcel.sorter.service.NoRackAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandling {

    @ExceptionHandler(NoRackAvailableException.class)
    public ResponseEntity handle(NoRackAvailableException exc) {
        return ResponseEntity.unprocessableEntity().body(exc.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity handle(ObjectOptimisticLockingFailureException exc) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service is to busy right now");
    }
}

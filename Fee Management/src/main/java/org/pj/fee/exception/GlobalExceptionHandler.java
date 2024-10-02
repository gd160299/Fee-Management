package org.pj.fee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("responseTime", ex.getTimestamp());
        responseBody.put("code", ex.getCode());
        responseBody.put("msg", ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.valueOf(ex.getCode()));
    }
}
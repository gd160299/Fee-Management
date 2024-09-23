package org.pj.fee.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", ex.getTimestamp());
        responseBody.put("status", ex.getCode());
        responseBody.put("error", ex.getMessage());
        responseBody.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(responseBody, HttpStatus.valueOf(ex.getCode()));
    }
}
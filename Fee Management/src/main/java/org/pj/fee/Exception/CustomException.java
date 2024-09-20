package org.pj.fee.Exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomException extends RuntimeException {

    private final int code;
    private final LocalDateTime timestamp;

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

}


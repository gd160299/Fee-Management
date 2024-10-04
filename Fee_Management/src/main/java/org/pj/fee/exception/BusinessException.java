package org.pj.fee.exception;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final Timestamp responseTime;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.responseTime = new Timestamp(System.currentTimeMillis());
    }
}


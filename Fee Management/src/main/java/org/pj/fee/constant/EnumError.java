package org.pj.fee.constant;

import lombok.Getter;

@Getter
public enum EnumError {
    DUPLICATE_REQUEST_ID(409, "Duplicate requestId: %s."),
    INVALID_REQUEST_TIME(400, "RequestTime is not within 10 minutes of current time."),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    ;

    private final int code;
    private final String message;

    EnumError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}

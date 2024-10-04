package org.pj.fee.constant;

import lombok.Getter;

@Getter
public enum EnumError {
    DUPLICATE_REQUEST_ID(409, "Duplicate requestId: %s."),
    INVALID_REQUEST_TIME(400, "RequestTime is not within 10 minutes of current time."),
    ADD_FEE_COMMAND_ERROR(500, "Failed to add fee command"),
    PROCESS_FEE_COMMAND_ERROR(500, "Failed to process fee command"),
    FEE_UPDATE_JOB_ERROR(500, "Fee update job failed"),
    FEE_COMMAND_SEND_MSG_ERROR(500, "Failed to send message from requestId [%s]"),
    FEE_COMMAND_RECEIVE_MSG_ERROR(500, "Failed to receive message from requestId [%s]"),
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

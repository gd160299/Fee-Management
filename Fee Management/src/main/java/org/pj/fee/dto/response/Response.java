package org.pj.fee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private int status;
    private String message;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private T data;
}

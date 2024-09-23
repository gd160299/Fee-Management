package org.pj.fee.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private T data;
}

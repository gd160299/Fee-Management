package org.pj.fee.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FeeCommandDTO {
    private String requestId;
    private String requestTime;
    private String commandCode;
    private int totalRecord;
    private BigDecimal totalFee;
    private String createdUser;
    private String createdDate;
}


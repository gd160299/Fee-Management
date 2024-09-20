package org.pj.fee.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FEE_TRANSACTION")
@Data
public class FeeTransaction {
    @Id
    private Long id;
    private String transactionCode;
    private String commandCode;
    private BigDecimal feeAmount;
    private String status;
    private String accountNumber;
    private int totalScan;
    private String remark;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

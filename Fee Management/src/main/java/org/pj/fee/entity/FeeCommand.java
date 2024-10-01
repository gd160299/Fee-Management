package org.pj.fee.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FEE_COMMAND")
@Data
public class FeeCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_command_seq_gen")
    @SequenceGenerator(name = "fee_command_seq_gen", sequenceName = "FEE_COMMAND_SEQ", allocationSize = 1)
    private Long id;
    private String commandCode;
    private int totalRecord;
    private BigDecimal totalFee;
    private String createdUser;
    private LocalDateTime createdDate;
}

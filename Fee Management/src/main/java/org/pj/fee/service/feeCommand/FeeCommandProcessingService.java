package org.pj.fee.service.feeCommand;

import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.entity.FeeTransaction;
import org.pj.fee.constant.EnumTransactionStatus;
import org.pj.fee.repository.FeeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeeCommandProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandProcessingService.class);

    private final FeeTransactionRepository feeTransactionRepository;

    public FeeCommandProcessingService(FeeTransactionRepository feeTransactionRepository) {
        this.feeTransactionRepository = feeTransactionRepository;
    }

    public void processFeeCommand(FeeCommandDTO feeCommandDto) {
        logger.info("Begin processFeeCommand with requestId: {}", feeCommandDto.getRequestId());

        // Lấy thông tin các giao dịch theo commandCode
        List<FeeTransaction> transactions = feeTransactionRepository.findByCommandCode(feeCommandDto.getCommandCode());
        logger.info("Retrieved transactions with count: {}", transactions.size());

        // Cập nhật các giao dịch
        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(1);
            transaction.setModifiedDate(LocalDateTime.now());
            transaction.setStatus(EnumTransactionStatus.THU_PHI.getCode());
        }
        feeTransactionRepository.saveAll(transactions);
        logger.info("Updated transactions with count: {}", transactions.size());

        logger.info("End processFeeCommand for requestId: {}", feeCommandDto.getRequestId());
    }
}


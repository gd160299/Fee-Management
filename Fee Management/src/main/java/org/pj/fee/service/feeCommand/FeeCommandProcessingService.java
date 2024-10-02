package org.pj.fee.service.feeCommand;

import lombok.extern.slf4j.Slf4j;
import org.pj.fee.constant.EnumError;
import org.pj.fee.constant.EnumTransactionStatus;
import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.entity.FeeTransaction;
import org.pj.fee.exception.BusinessException;
import org.pj.fee.repository.FeeTransactionRepository;
import org.pj.fee.service.IFeeCommandProcessingService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class FeeCommandProcessingService implements IFeeCommandProcessingService {

    private final FeeTransactionRepository feeTransactionRepository;

    public FeeCommandProcessingService(FeeTransactionRepository feeTransactionRepository) {
        this.feeTransactionRepository = feeTransactionRepository;
    }

    public void processFeeCommand(FeeCommandDTO feeCommandDto) {
        log.info("Begin processFeeCommand: {}", feeCommandDto);
        try{
            // Lấy thông tin các giao dịch theo commandCode
            List<FeeTransaction> transactions = feeTransactionRepository.findByCommandCode(feeCommandDto.getCommandCode());
            log.info("Retrieved transactions with count: {}", transactions.size());

            // Cập nhật các giao dịch
            for (FeeTransaction transaction : transactions) {
                transaction.setTotalScan(1);
                transaction.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                transaction.setStatus(EnumTransactionStatus.THU_PHI.getCode());
            }
            feeTransactionRepository.saveAll(transactions);
            log.info("Updated transactions with count: {}", transactions.size());
        } catch (Exception e) {
            log.error("Unexpected exception in processFeeCommand", e);
            throw new BusinessException(EnumError.INTERNAL_SERVER_ERROR.getCode(), EnumError.INTERNAL_SERVER_ERROR.getMessage());
        }
        log.info("End processFeeCommand for requestId: {}", feeCommandDto.getRequestId());
    }
}


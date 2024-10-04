package org.pj.fee.service.feeTransaction;

import lombok.extern.slf4j.Slf4j;
import org.pj.fee.constant.EnumTransactionStatus;
import org.pj.fee.entity.FeeTransaction;
import org.pj.fee.exception.BusinessException;
import org.pj.fee.repository.FeeTransactionRepository;
import org.pj.fee.service.IFeeTransactionCronJobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static org.pj.fee.constant.EnumError.FEE_UPDATE_JOB_ERROR;

@Slf4j
@Service
public class FeeTransactionCronJobServiceImpl implements IFeeTransactionCronJobService {

    private final FeeTransactionRepository feeTransactionRepository;

    public FeeTransactionCronJobServiceImpl(FeeTransactionRepository feeTransactionRepository) {
        this.feeTransactionRepository = feeTransactionRepository;
    }

    // Job chạy mỗi 3 phút
    @Scheduled(cron = "0 */3 * * * ?")
    public void updateFeeTransactions() {
        log.info("Begin updateFeeTransactions cron job");
        try {
            List<FeeTransaction> transactions = feeTransactionRepository.findByStatusAndTotalScanLessThan(EnumTransactionStatus.THU_PHI.getCode(), 5);
            log.info("Transactions to update count: {}", transactions.size());

            for (FeeTransaction transaction : transactions) {
                transaction.setTotalScan(transaction.getTotalScan() + 1);
                transaction.setModifiedDate(new Timestamp(System.currentTimeMillis()));

                if (transaction.getTotalScan() >= 5) {
                    transaction.setStatus(EnumTransactionStatus.DUNG_THU.getCode());
                }
            }
            feeTransactionRepository.saveAll(transactions);
            log.info("Updated transactions count: {}", transactions.size());
            log.info("End updateFeeTransactions cron job");
        } catch (Exception e) {
            log.error("Unexpected exception in updateFeeTransactions", e);
            throw new BusinessException(FEE_UPDATE_JOB_ERROR.getCode(), FEE_UPDATE_JOB_ERROR.getMessage());
        }
    }
}

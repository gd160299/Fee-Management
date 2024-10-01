package org.pj.fee.service.feeTransaction;

import org.pj.fee.entity.FeeTransaction;
import org.pj.fee.constant.EnumTransactionStatus;
import org.pj.fee.repository.FeeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeeTransactionCronJobService {
    private static final Logger logger = LoggerFactory.getLogger(FeeTransactionCronJobService.class);

    private final FeeTransactionRepository feeTransactionRepository;

    public FeeTransactionCronJobService(FeeTransactionRepository feeTransactionRepository) {
        this.feeTransactionRepository = feeTransactionRepository;
    }

    // Job chạy mỗi 3 phút
    @Scheduled(cron = "0 */3 * * * ?")
    public void updateFeeTransactions() {
        logger.info("Begin updateFeeTransactions cron job");

        List<FeeTransaction> transactions = feeTransactionRepository.findByStatusAndTotalScanLessThan(EnumTransactionStatus.THU_PHI.getCode(), 5);
        logger.info("Transactions to update count: {}", transactions.size());

        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(transaction.getTotalScan() + 1);
            transaction.setModifiedDate(LocalDateTime.now() );

            if (transaction.getTotalScan() >= 5) {
                transaction.setStatus(EnumTransactionStatus.DUNG_THU.getCode());
            }
        }
        feeTransactionRepository.saveAll(transactions);
        logger.info("Updated transactions count: {}", transactions.size());
        logger.info("End updateFeeTransactions cron job");
    }
}

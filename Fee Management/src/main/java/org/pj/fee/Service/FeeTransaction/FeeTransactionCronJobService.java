package org.pj.fee.Service.FeeTransaction;

import org.pj.fee.Entity.FeeTransaction;
import org.pj.fee.Enum.TransactionStatus;
import org.pj.fee.Repository.FeeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeeTransactionCronJobService {
    private static final Logger logger = LoggerFactory.getLogger(FeeTransactionCronJobService.class);

    @Autowired
    private FeeTransactionRepository feeTransactionRepository;

    // Job chạy mỗi 3 phút
    @Scheduled(cron = "0 */3 * * * ?")
    public void updateFeeTransactions() {
        logger.info("Begin updateFeeTransactions cron job");

        List<FeeTransaction> transactions = feeTransactionRepository.findByStatusAndTotalScanLessThan(TransactionStatus.THU_PHI.getCode(), 5);
        logger.info("Transactions to update count: {}", transactions.size());

        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(transaction.getTotalScan() + 1);
            transaction.setModifiedDate(LocalDateTime.now());

            if (transaction.getTotalScan() >= 5) {
                transaction.setStatus(TransactionStatus.DUNG_THU.getCode());
            }
        }
        feeTransactionRepository.saveAll(transactions);
        logger.info("Updated transactions count: {}", transactions.size());
        logger.info("End updateFeeTransactions cron job");
    }
}

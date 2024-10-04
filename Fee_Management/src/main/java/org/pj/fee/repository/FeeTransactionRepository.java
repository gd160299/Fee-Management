package org.pj.fee.repository;

import org.pj.fee.entity.FeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeTransactionRepository extends JpaRepository<FeeTransaction, Long> {
    List<FeeTransaction> findByCommandCode(String commandCode);
    List<FeeTransaction> findByStatusAndTotalScanLessThan(String status, int totalScan);
}

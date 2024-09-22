package org.pj.fee.Service.FeeCommand;

import org.pj.fee.Dto.Request.FeeCommandDto;
import org.pj.fee.Entity.FeeCommand;
import org.pj.fee.Repository.FeeCommandRepository;
import org.pj.fee.Repository.FeeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeeCommandManagementService {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandManagementService.class);

    @Autowired
    private FeeCommandRepository feeCommandRepository;

//    @Autowired
//    private FeeTransactionRepository feeTransactionRepository;

    public void addFeeCommand(FeeCommandDto feeCommandDto) {
        logger.info("Begin addFeeCommand with commandCode: {}", feeCommandDto.getCommandCode());

        // Tạo mới FeeCommand
        FeeCommand feeCommand = new FeeCommand();
        feeCommand.setCommandCode(generateUniqueCommandCode());
        feeCommand.setTotalRecord(feeCommandDto.getTotalRecord());
        feeCommand.setTotalFee(feeCommandDto.getTotalFee());
        feeCommand.setCreatedUser(feeCommandDto.getCreatedUser());
        feeCommand.setCreatedDate(LocalDateTime.now());

        feeCommandRepository.save(feeCommand);
        logger.info("FeeCommand saved with id: {}", feeCommand.getId());

//        // Tạo danh sách FeeTransaction
//        List<FeeTransaction> transactions = new ArrayList<>();
//        for (int i = 0; i < feeCommandDto.getTotalRecord(); i++) {
//            FeeTransaction transaction = new FeeTransaction();
//            transaction.setTransactionCode(generateUniqueTransactionCode());
//            transaction.setCommandCode(feeCommand.getCommandCode());
//            transaction.setFeeAmount(BigDecimal.ZERO); // Số tiền phí có thể từ feeCommandDto
//            transaction.setStatus(TransactionStatus.KHOI_TAO.getCode());
//            transaction.setTotalScan(0);
//            transaction.setCreatedDate(LocalDateTime.now());
//            transactions.add(transaction);
//        }
//
//        feeTransactionRepository.saveAll(transactions);
//        logger.info("FeeTransactions saved with count: {}", transactions.size());
//
//        logger.info("End addFeeCommand for commandCode: {}", feeCommandDto.getCommandCode());
    }

    private String generateUniqueCommandCode() {
        return String.format("CMD%d", System.currentTimeMillis());
    }

    private String generateUniqueTransactionCode() {
        return String.format("TRX%d", System.currentTimeMillis());
    }
}


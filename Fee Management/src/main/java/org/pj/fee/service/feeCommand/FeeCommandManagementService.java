package org.pj.fee.service.feeCommand;

import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.entity.FeeCommand;
import org.pj.fee.entity.FeeTransaction;
import org.pj.fee.constant.EnumTransactionStatus;
import org.pj.fee.exception.BusinessException;
import org.pj.fee.repository.FeeCommandRepository;
import org.pj.fee.repository.FeeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeeCommandManagementService {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandManagementService.class);

    private final FeeCommandRepository feeCommandRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final FeeTransactionRepository feeTransactionRepository;

    @Autowired
    public FeeCommandManagementService(FeeCommandRepository feeCommandRepository, RedisTemplate<String, Object> redisTemplate, FeeTransactionRepository feeTransactionRepository) {
        this.feeCommandRepository = feeCommandRepository;
        this.redisTemplate = redisTemplate;
        this.feeTransactionRepository = feeTransactionRepository;
    }

    @Transactional
    public void addFeeCommand(FeeCommandDTO feeCommandDto) {
        validate(feeCommandDto);
        logger.info("Begin addFeeCommand with requestId: {}", feeCommandDto.getRequestId());
        // Tạo mới FeeCommand
        FeeCommand feeCommand = new FeeCommand();
        feeCommand.setCommandCode(generateUniqueCommandCode());
        feeCommand.setTotalRecord(feeCommandDto.getTotalRecord());
        feeCommand.setTotalFee(feeCommandDto.getTotalFee());
        feeCommand.setCreatedUser(feeCommandDto.getCreatedUser());
        feeCommand.setCreatedDate(LocalDateTime.now());

        feeCommandRepository.save(feeCommand);
        logger.info("FeeCommand saved with id: {}", feeCommand.getId());

        // Tạo danh sách FeeTransaction
        List<FeeTransaction> transactions = new ArrayList<>();
        for (int i = 0; i < feeCommandDto.getTotalRecord(); i++) {
            FeeTransaction transaction = new FeeTransaction();
            transaction.setTransactionCode(generateUniqueTransactionCode());
            transaction.setCommandCode(feeCommand.getCommandCode());
            transaction.setFeeAmount(BigDecimal.ZERO); // Có thể điều chỉnh theo yêu cầu
            transaction.setStatus(EnumTransactionStatus.KHOI_TAO.getCode());
            transaction.setAccountNumber("abc");
            transaction.setTotalScan(0);
            transaction.setCreatedDate(LocalDateTime.now());
            transactions.add(transaction);
        }

        feeTransactionRepository.saveAll(transactions);
        logger.info("FeeTransactions saved with count: {}", transactions.size());

        logger.info("End addFeeCommand for requestId: {}", feeCommandDto.getRequestId());

        // Lưu requestId vào Redis với thời gian sống 1 ngày
        String requestIdKey = String.format("requestId:%s",feeCommandDto.getRequestId());
        redisTemplate.opsForValue().set(requestIdKey, true, Duration.ofDays(1));
        logger.info("RequestId stored in Redis: {}", feeCommandDto.getRequestId());
    }

    private void validate(FeeCommandDTO feeCommandDto) {
        // Kiểm tra trùng requestId trong 1 ngày
        String requestIdKey = String.format("requestId:%s",feeCommandDto.getRequestId());
        Boolean isDuplicate = redisTemplate.hasKey(requestIdKey);
        if (Boolean.TRUE.equals(isDuplicate)) {
            logger.error("Duplicate requestId: {}", feeCommandDto.getRequestId());
            throw new BusinessException(HttpStatus.CONFLICT.value(), String.format("Duplicate requestId: [%s].", feeCommandDto.getRequestId()));
        }
        // Kiểm tra requestTime không quá 10 phút so với thời gian hiện tại
        LocalDateTime requestTime = LocalDateTime.parse(feeCommandDto.getRequestTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (Duration.between(requestTime, LocalDateTime.now()).abs().toMinutes() > 10) {
            logger.error("Invalid requestTime: {}", feeCommandDto.getRequestTime());
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "RequestTime is not within 10 minutes of current time.");
        }
        logger.info("RequestTime validated successfully for requestId: {}", feeCommandDto.getRequestId());
    }

    private String generateUniqueCommandCode() {
        return String.format("CMD%s", UUID.randomUUID());
    }

    private String generateUniqueTransactionCode() {
        return String.format("TRX%s", UUID.randomUUID());
    }
}


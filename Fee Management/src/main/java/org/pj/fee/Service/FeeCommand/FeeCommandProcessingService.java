package org.pj.fee.Service.FeeCommand;

import org.pj.fee.Dto.Request.FeeCommandDto;
import org.pj.fee.Entity.FeeTransaction;
import org.pj.fee.Enum.TransactionStatus;
import org.pj.fee.Exception.CustomException;
import org.pj.fee.Repository.FeeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FeeCommandProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandProcessingService.class);

    @Autowired
    private FeeTransactionRepository feeTransactionRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void processFeeCommand(FeeCommandDto feeCommandDto) {
        logger.info("Begin processFeeCommand with requestId: {}", feeCommandDto.getRequestId());
        // Kiểm tra trùng requestId trong 1 ngày
        String requestIdKey = String.format("requestId:%s", feeCommandDto.getRequestId());
        Boolean isDuplicate = redisTemplate.hasKey(requestIdKey);
        if (Boolean.TRUE.equals(isDuplicate)) {
            logger.error("Duplicate requestId detected: {}", feeCommandDto.getRequestId());
            throw new CustomException(HttpStatus.CONFLICT.value(), String.format("Duplicate requestId : [%s].", feeCommandDto.getRequestId()));
        }
        redisTemplate.opsForValue().set(requestIdKey, true, Duration.ofDays(1));
        logger.info("RequestId stored in Redis: {}", feeCommandDto.getRequestId());

        // Kiểm tra requestTime không quá 10 phút so với thời gian hiện tại
        LocalDateTime requestTime = LocalDateTime.parse(feeCommandDto.getRequestTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (Duration.between(requestTime, LocalDateTime.now()).abs().toMinutes() > 10) {
            logger.error("Invalid requestTime: {}", feeCommandDto.getRequestTime());
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "RequestTime is not within 10 minutes of current time.");
        }
        logger.info("RequestTime validated successfully for requestId: {}", feeCommandDto.getRequestId());

        // Lấy thông tin các giao dịch theo commandCode
        List<FeeTransaction> transactions = feeTransactionRepository.findByCommandCode(feeCommandDto.getCommandCode());
        logger.info("Retrieved transactions with count: {}", transactions.size());

        // Cập nhật các giao dịch
        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(1);
            transaction.setModifiedDate(LocalDateTime.now());
            transaction.setStatus(TransactionStatus.THU_PHI.getCode());
        }
        feeTransactionRepository.saveAll(transactions);
        logger.info("Updated transactions with count: {}", transactions.size());

        logger.info("End processFeeCommand for requestId: {}", feeCommandDto.getRequestId());
    }
}


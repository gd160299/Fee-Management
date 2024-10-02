package org.pj.fee.service.feeCommand;

import lombok.extern.slf4j.Slf4j;
import org.pj.fee.constant.EnumError;
import org.pj.fee.constant.EnumTransactionStatus;
import org.pj.fee.constant.FeeConstant;
import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.entity.FeeCommand;
import org.pj.fee.entity.FeeTransaction;
import org.pj.fee.exception.BusinessException;
import org.pj.fee.repository.FeeCommandRepository;
import org.pj.fee.repository.FeeTransactionRepository;
import org.pj.fee.service.IFeeCommandManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FeeCommandManagementService implements IFeeCommandManagementService {

    private final FeeCommandRepository feeCommandRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final FeeTransactionRepository feeTransactionRepository;

    @Autowired
    public FeeCommandManagementService(FeeCommandRepository feeCommandRepository, RedisTemplate<String, Object> redisTemplate,
                                       FeeTransactionRepository feeTransactionRepository) {
        this.feeCommandRepository = feeCommandRepository;
        this.redisTemplate = redisTemplate;
        this.feeTransactionRepository = feeTransactionRepository;
    }

    @Transactional
    public void addFeeCommand(FeeCommandDTO feeCommandDto) {
        log.info("Begin addFeeCommand: {}", feeCommandDto);
        try {
            validate(feeCommandDto);
            // Tạo mới FeeCommand
            FeeCommand feeCommand = new FeeCommand();
            feeCommand.setCommandCode(generateUniqueCommandCode());
            feeCommand.setTotalRecord(feeCommandDto.getTotalRecord());
            feeCommand.setTotalFee(feeCommandDto.getTotalFee());
            feeCommand.setCreatedUser(feeCommandDto.getCreatedUser());
            feeCommand.setCreatedDate(new Timestamp(System.currentTimeMillis()));

            feeCommandRepository.save(feeCommand);
            log.info("FeeCommand saved with id: {}", feeCommand.getId());

            // Tạo danh sách FeeTransaction
            List<FeeTransaction> transactions = new ArrayList<>();
            int totalRecord = feeCommandDto.getTotalRecord();
            String commandCode = feeCommand.getCommandCode();
            for (int i = 0; i < totalRecord; i++) {
                FeeTransaction transaction = createFeeTransaction(commandCode);
                transactions.add(transaction);
            }

            feeTransactionRepository.saveAll(transactions);
            log.info("FeeTransactions saved with count: {}", transactions.size());

            // Lưu requestId vào Redis với thời gian sống 1 ngày
            String requestIdKey =  new StringBuilder(FeeConstant.REQUEST_ID_PREFIX).
                    append(feeCommandDto.getRequestId()).toString();
            Duration timeToExpire = calculateTimeToEndOfDay();
            redisTemplate.opsForValue().set(requestIdKey, true, timeToExpire);
            log.info("RequestId stored in Redis: {}", feeCommandDto.getRequestId());
        } catch (Exception e) {
            log.info("Unexpected exception in addFeeCommand", e);
            throw new BusinessException(EnumError.INTERNAL_SERVER_ERROR.getCode(), EnumError.INTERNAL_SERVER_ERROR.getMessage());
        }
        log.info("End addFeeCommand for requestId: {}", feeCommandDto.getRequestId());
    }

    private void validate(FeeCommandDTO feeCommandDto) {
        // Kiểm tra trùng requestId trong 1 ngày
        String requestIdKey = new StringBuilder(FeeConstant.REQUEST_ID_PREFIX).
                append(feeCommandDto.getRequestId()).toString();
        Boolean isDuplicate = redisTemplate.hasKey(requestIdKey);
        if (Boolean.TRUE.equals(isDuplicate)) {
            log.info("Duplicate requestId: {}", feeCommandDto.getRequestId());
            throw new BusinessException(EnumError.DUPLICATE_REQUEST_ID.getCode(),
                    EnumError.DUPLICATE_REQUEST_ID.getMessage(feeCommandDto.getRequestId()));
        }
        // Kiểm tra requestTime không quá 10 phút so với thời gian hiện tại
        LocalDateTime requestTime = LocalDateTime.parse(feeCommandDto.getRequestTime(),
                DateTimeFormatter.ofPattern(FeeConstant.DATE_FORMAT_YYYY_MM_DD_HHMMSS));
        if (Duration.between(requestTime, LocalDateTime.now()).abs().toMinutes() > FeeConstant.REQUEST_TIME_LIMIT_MINUTES) {
            log.info("Invalid requestTime: {}", feeCommandDto.getRequestTime());
            throw new BusinessException(EnumError.INVALID_REQUEST_TIME.getCode(), EnumError.INVALID_REQUEST_TIME.getMessage());
        }
        log.info("RequestTime validated successfully for requestId: {}", feeCommandDto.getRequestId());
    }

    private FeeTransaction createFeeTransaction(String commandCode) {
        FeeTransaction transaction = new FeeTransaction();
        transaction.setTransactionCode(generateUniqueTransactionCode());
        transaction.setCommandCode(commandCode);
        transaction.setFeeAmount(BigDecimal.ZERO);
        transaction.setStatus(EnumTransactionStatus.KHOI_TAO.getCode());
        transaction.setAccountNumber(FeeConstant.DEFAULT_ACCOUNT_NUMBER);
        transaction.setTotalScan(0);
        transaction.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        return transaction;
    }

    private String generateUniqueCommandCode() {
        return new StringBuilder("CMD")
                .append(UUID.randomUUID()).toString();
    }

    private String generateUniqueTransactionCode() {
        return new StringBuilder("TRX")
                .append(UUID.randomUUID()).toString();
    }

    private Duration calculateTimeToEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);
        return Duration.between(now, endOfDay);
    }
}


package org.pj.fee.service.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.pj.fee.config.RabbitMqConfig;
import org.pj.fee.constant.EnumError;
import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.exception.BusinessException;
import org.pj.fee.service.feeCommand.FeeCommandProcessingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeeCommandConsumer {

    private final FeeCommandProcessingService feeCommandProcessingService;

    @Autowired
    public FeeCommandConsumer(FeeCommandProcessingService feeCommandProcessingService) {
        this.feeCommandProcessingService = feeCommandProcessingService;
    }

    @RabbitListener(queues = RabbitMqConfig.FEE_COMMAND_QUEUE)
    public void receiveFeeCommand(FeeCommandDTO feeCommandDto) {
        log.info("Received requestId: {} from queue", feeCommandDto.getRequestId());
        try {
            feeCommandProcessingService.processFeeCommand(feeCommandDto);
        } catch (Exception e) {
            log.error("Error processing FeeCommand in Consumer", e);
            throw new BusinessException(EnumError.INTERNAL_SERVER_ERROR.getCode(), EnumError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
}



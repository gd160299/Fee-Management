package org.pj.fee.service.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.pj.fee.config.RabbitMqConfig;
import org.pj.fee.constant.EnumError;
import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.exception.BusinessException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeeCommandProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public FeeCommandProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendFeeCommand(FeeCommandDTO feeCommandDto) {
        log.info("Begin sendFeeCommand: {}", feeCommandDto);
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.FEE_COMMAND_QUEUE, feeCommandDto);
            log.info("Sent to queue successfully for requestId: {}", feeCommandDto.getRequestId());
        } catch (Exception e) {
            log.error("Error sending FeeCommand to queue", e);
            throw new BusinessException(EnumError.INTERNAL_SERVER_ERROR.getCode(), EnumError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
}


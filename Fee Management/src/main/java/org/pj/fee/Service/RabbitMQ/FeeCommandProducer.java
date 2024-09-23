package org.pj.fee.Service.RabbitMQ;

import org.pj.fee.Config.RabbitMqConfig;
import org.pj.fee.Dto.Request.FeeCommandDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeCommandProducer {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public FeeCommandProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendFeeCommand(FeeCommandDto feeCommandDto) {
        logger.info("Begin sendFeeCommand for requestId: {}", feeCommandDto.getRequestId());
        rabbitTemplate.convertAndSend(RabbitMqConfig.FEE_COMMAND_QUEUE, feeCommandDto);
        logger.info("Sent to queue successfully for requestId: {}", feeCommandDto.getRequestId());
    }
}


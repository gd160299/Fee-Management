package org.pj.fee.Service.RabbitMQ;

import org.pj.fee.Config.RabbitMqConfig;
import org.pj.fee.Dto.Request.FeeCommandDto;
import org.pj.fee.Service.FeeCommand.FeeCommandProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandConsumer.class);

    private final FeeCommandProcessingService feeCommandProcessingService;

    @Autowired
    public FeeCommandConsumer(FeeCommandProcessingService feeCommandProcessingService) {
        this.feeCommandProcessingService = feeCommandProcessingService;
    }

    @RabbitListener(queues = RabbitMqConfig.FEE_COMMAND_QUEUE)
    public void receiveFeeCommand(FeeCommandDto feeCommandDto) {
        logger.info("Received requestId: {} from queue", feeCommandDto.getRequestId());
        feeCommandProcessingService.processFeeCommand(feeCommandDto);
    }
}


